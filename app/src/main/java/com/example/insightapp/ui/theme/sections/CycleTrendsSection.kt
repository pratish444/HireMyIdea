package com.example.insightapp.ui.theme.sections


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insightapp.data.CycleBarData
import com.example.insightapp.ui.theme.CardBackground
import com.example.insightapp.ui.theme.DividerColor
import com.example.insightapp.ui.theme.TextPrimary
import com.example.insightapp.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ── Cycle-specific palette ──
private val BarLavender = Color(0xFFBEB4DA)
private val BarSageGreen = Color(0xFF7B9D87)
private val BarCoral = Color(0xFFE69B9B)
private val SunIconColor = Color(0xFFD0DDD3)
private val DropIconColor = Color(0xFFF2CBCB)
private val DashedLineColor = Color(0xFFE0DDE8)

private const val MAX_DAYS = 36f

@Composable
fun CycleTrendsSection(
    dataPages: List<List<CycleBarData>>,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableStateOf(0) }
    val currentData = dataPages.getOrElse(currentPage) { dataPages.first() }
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .padding(20.dp)
    ) {
        Text(
            text = "Cycle Trends",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Chart area with left/right nav buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ◂ Left navigation button
            NavArrowButton(
                isLeft = true,
                enabled = currentPage > 0,
                onClick = { currentPage-- }
            )

            // Chart content: dashed lines + bars
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(310.dp)
                    .padding(horizontal = 4.dp)
            ) {
                // ── 3 Dashed horizontal grid lines (behind bars) ──
                // Padded at bottom to align with bar canvas area (above month labels)
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(bottom = 24.dp) // space for month labels
                ) {
                    val h = size.height
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 5f))

                    // Bottom line = bar baseline (bottom of candles)
                    // Top line = near top of tallest bar
                    // Middle = between them
                    val lineYPositions = listOf(
                        h * 0.98f,   // bottom — bar baseline
                        h * 0.54f,   // middle
                        h * 0.10f    // top — near tallest bar top
                    )

                    lineYPositions.forEach { y ->
                        drawLine(
                            color = DashedLineColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1f,
                            pathEffect = dashEffect
                        )
                    }
                }

                // ── Bars row ──
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    currentData.forEach { barData ->
                        CycleBarColumn(
                            barData = barData,
                            textMeasurer = textMeasurer
                        )
                    }
                }
            }

            // ▸ Right navigation button
            NavArrowButton(
                isLeft = false,
                enabled = currentPage < dataPages.size - 1,
                onClick = { currentPage++ }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Single bar column — number is drawn INSIDE the Canvas above the bar,
// so taller bars naturally push the number higher.
// ─────────────────────────────────────────────────────────────────

@Composable
private fun CycleBarColumn(
    barData: CycleBarData,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    modifier: Modifier = Modifier
) {
    val barWidthDp = 20.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Canvas draws BOTH the bar AND the day-count number above it
        Canvas(
            modifier = Modifier
                .width(barWidthDp)
                .weight(1f)
        ) {
            val barW = size.width
            val totalH = size.height
            val barH = (barData.totalDays / MAX_DAYS) * totalH
            val barTop = totalH - barH
            val cornerR = barW / 2f

            // ── Day count number — drawn ABOVE the bar top ──
            val textResult = textMeasurer.measure(
                text = "${barData.totalDays}",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            drawText(
                textLayoutResult = textResult,
                topLeft = Offset(
                    (barW - textResult.size.width) / 2f,
                    barTop - textResult.size.height - 8.dp.toPx()
                )
            )

            // ── Capsule bar ──
            val capsulePath = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = 0f, top = barTop,
                        right = barW, bottom = totalH,
                        radiusX = cornerR, radiusY = cornerR
                    )
                )
            }

            clipPath(capsulePath) {
                // ① Lavender fill — entire bar
                drawRect(
                    color = BarLavender,
                    topLeft = Offset(0f, barTop),
                    size = Size(barW, barH)
                )

                // ② Coral oval at bottom (menstrual phase) — some touch bottom, some float above
                val coralOvalH = barW * 1.4f
                val coralOvalW = barW
                val coralCenterY = totalH - coralOvalH / 2f - (barData.coralOffset * barH)
                drawOval(
                    color = BarCoral,
                    topLeft = Offset(
                        (barW - coralOvalW) / 2f,
                        coralCenterY - coralOvalH / 2f
                    ),
                    size = Size(coralOvalW, coralOvalH)
                )

                // ③ Green oval (ovulation window) — position varies per bar
                val ovalH = barW * 1.5f
                val ovalW = barW
                val ovalCenterY = barTop + barH * (1f - barData.ovulationPosition)
                drawOval(
                    color = BarSageGreen,
                    topLeft = Offset(
                        (barW - ovalW) / 2f,
                        ovalCenterY - ovalH / 2f
                    ),
                    size = Size(ovalW, ovalH)
                )

                // ④ Sun icon inside green oval
                drawSunIcon(
                    center = Offset(barW / 2f, ovalCenterY),
                    color = SunIconColor
                )

                // ⑤ Water-drop icon in coral oval
                drawDropIcon(
                    center = Offset(barW / 2f, coralCenterY),
                    color = DropIconColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Month label
        Text(
            text = barData.label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// Icon helpers
// ─────────────────────────────────────────────────────────────────

private fun DrawScope.drawSunIcon(center: Offset, color: Color) {
    val ringR = 4.5.dp.toPx()
    val dotR = 1.dp.toPx()
    val rayR = 7.5.dp.toPx()

    drawCircle(color = color, radius = 1.5.dp.toPx(), center = center)
    drawCircle(color = color, radius = ringR, center = center, style = Stroke(width = 1.2.dp.toPx()))

    for (i in 0 until 8) {
        val angle = i * (2.0 * PI / 8.0)
        drawCircle(
            color = color,
            radius = dotR,
            center = Offset(
                center.x + cos(angle).toFloat() * rayR,
                center.y + sin(angle).toFloat() * rayR
            )
        )
    }
}

private fun DrawScope.drawDropIcon(center: Offset, color: Color) {
    val dH = 7.dp.toPx()
    val dW = 4.dp.toPx()

    val path = Path().apply {
        moveTo(center.x, center.y - dH / 2f)
        quadraticBezierTo(center.x + dW, center.y + dH * 0.1f, center.x, center.y + dH / 2f)
        quadraticBezierTo(center.x - dW, center.y + dH * 0.1f, center.x, center.y - dH / 2f)
        close()
    }

    drawPath(path, color = color, style = Stroke(width = 1.2.dp.toPx()))
}

// ─────────────────────────────────────────────────────────────────
// Navigation arrow buttons  ‹  ›
// ─────────────────────────────────────────────────────────────────

@Composable
private fun NavArrowButton(
    isLeft: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (enabled) DividerColor else DividerColor.copy(alpha = 0.3f)
    val textColor = if (enabled) TextSecondary else TextSecondary.copy(alpha = 0.25f)

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .border(1.5.dp, borderColor, CircleShape)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isLeft) "‹" else "›",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
            color = textColor
        )
    }
}
