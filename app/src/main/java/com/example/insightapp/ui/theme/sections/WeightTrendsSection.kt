package com.example.insightapp.ui.theme.sections


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insightapp.data.WeightDataPoint
import com.example.insightapp.ui.theme.CardBackground
import com.example.insightapp.ui.theme.DarkChip
import com.example.insightapp.ui.theme.TextPrimary
import com.example.insightapp.ui.theme.TextSecondary

import kotlin.math.abs

// ── Weight chart palette ──
private val WtLineColor = Color(0xFFE8868B)
private val WtPointBorder = Color(0xFFE8868B)
private val WtFillTop = Color(0x40E8868B)
private val WtFillBottom = Color(0x08E8868B)
private val WtGridColor = Color(0xFFE8E4EE)
private val ToggleBg = Color(0xFFF0EDE4)
private val ToggleActiveBg = Color(0xFF2D2D2D)

@Composable
fun WeightTrendsSection(
    data: List<WeightDataPoint>,
    modifier: Modifier = Modifier
) {
    var isMonthly by remember { mutableStateOf(true) }
    var selectedPxX by remember { mutableStateOf<Float?>(null) }
    var selectedPxY by remember { mutableStateOf<Float?>(null) }
    var selectedValue by remember { mutableStateOf<Float?>(null) }

    val yMin = 20f
    val yMax = 80f
    val gridValues = listOf(75f, 50f, 25f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            // Added dismiss listener: Clicking anywhere on the card background clears selection
            .pointerInput(Unit) {
                detectTapGestures {
                    selectedPxX = null
                    selectedPxY = null
                    selectedValue = null
                }
            }
            .padding(20.dp)
    ) {
        // ── Header: title + subtitle + toggle ──
        Text(
            text = "Body & Metabolic Trends",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "Your weight",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary
                )
                Text(
                    text = "in kg",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            // Monthly / Weekly toggle (dark = active, light = inactive)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(ToggleBg)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isMonthly) ToggleActiveBg else Color.Transparent)
                        .noRippleClickable { isMonthly = true }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Monthly",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isMonthly) Color.White else TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (!isMonthly) ToggleActiveBg else Color.Transparent)
                        .noRippleClickable { isMonthly = false }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Weekly",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (!isMonthly) Color.White else TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Chart area with Y-axis ──
        Row(modifier = Modifier.fillMaxWidth()) {
            // Y-axis labels
            Column(
                modifier = Modifier
                    .height(170.dp)
                    .padding(top = 6.dp, bottom = 6.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                gridValues.forEach { value ->
                    Text(
                        text = "${value.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Chart canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(170.dp)
            ) {
                WeightLineChart(
                    data = data,
                    yMin = yMin,
                    yMax = yMax,
                    gridValues = gridValues,
                    onPointTapped = { px, py, weight ->
                        selectedPxX = px
                        selectedPxY = py
                        selectedValue = weight
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Interactive tooltip overlay
                if (selectedPxX != null && selectedValue != null) {
                    val density = LocalDensity.current
                    val xDp = with(density) { selectedPxX!!.toDp() }
                    val yDp = with(density) { selectedPxY!!.toDp() }

                    Box(
                        modifier = Modifier
                            .offset(
                                x = (xDp - 28.dp).coerceAtLeast(0.dp),
                                y = (yDp - 40.dp).coerceAtLeast(0.dp)
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkChip)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${selectedValue!!.toInt()} kg",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // X-axis labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 36.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { point ->
                Text(
                    text = point.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Weight line chart — bezier curve + gradient fill + data points
// ─────────────────────────────────────────────────────────────────

@Composable
private fun WeightLineChart(
    data: List<WeightDataPoint>,
    yMin: Float,
    yMax: Float,
    gridValues: List<Float>,
    onPointTapped: (Float, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (data.isEmpty()) return@detectTapGestures
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    val range = yMax - yMin
                    val stepX = w / (data.size - 1).coerceAtLeast(1)

                    // Calculate point screen positions
                    val pts = data.mapIndexed { i, pt ->
                        Offset(
                            i * stepX,
                            h - ((pt.weight - yMin) / range * h)
                        )
                    }

                    // Find nearest point by X distance
                    val nearestIdx = pts.indices.minByOrNull {
                        abs(pts[it].x - offset.x)
                    } ?: return@detectTapGestures

                    onPointTapped(pts[nearestIdx].x, pts[nearestIdx].y, data[nearestIdx].weight)
                }
            }
    ) {
        val width = size.width
        val height = size.height
        if (data.isEmpty()) return@Canvas
        val range = yMax - yMin

        fun valueToY(v: Float) = height - ((v - yMin) / range * height)

        val stepX = width / (data.size - 1).coerceAtLeast(1)
        val points = data.mapIndexed { i, pt ->
            Offset(i * stepX, valueToY(pt.weight))
        }

        // ── 1. Dashed horizontal grid lines ──
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 5f))
        gridValues.forEach { v ->
            val y = valueToY(v)
            drawLine(
                color = WtGridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f,
                pathEffect = dashEffect
            )
        }

        // ── 2. Build smooth bezier curve ──
        val linePath = Path()
        val fillPath = Path()

        linePath.moveTo(points[0].x, points[0].y)
        fillPath.moveTo(points[0].x, points[0].y)

        for (i in 1 until points.size) {
            val cpX = (points[i - 1].x + points[i].x) / 2f
            linePath.cubicTo(cpX, points[i - 1].y, cpX, points[i].y, points[i].x, points[i].y)
            fillPath.cubicTo(cpX, points[i - 1].y, cpX, points[i].y, points[i].x, points[i].y)
        }

        // Close fill path
        fillPath.lineTo(points.last().x, height)
        fillPath.lineTo(points.first().x, height)
        fillPath.close()

        // ── 3. Gradient fill under curve ──
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(WtFillTop, WtFillBottom, Color.Transparent),
                startY = points.minOf { it.y },
                endY = height
            )
        )

        // ── 4. Curve stroke ──
        drawPath(
            path = linePath,
            color = WtLineColor,
            style = Stroke(width = 2.5f)
        )

        // ── 5. Data point circles (white fill + pink ring) ──
        points.forEach { pt ->
            drawCircle(color = WtPointBorder, radius = 6f, center = pt)
            drawCircle(color = Color.White, radius = 3.5f, center = pt)
        }
    }
}

// ── Utility (reused from other sections) ──
@Composable
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier {
    return this.then(
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    )
}