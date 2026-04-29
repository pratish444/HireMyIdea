package com.example.insightapp.ui.theme.sections

import android.annotation.SuppressLint
import android.graphics.Paint as NativePaint
import android.graphics.RectF
import android.graphics.SweepGradient
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insightapp.data.SymptomData
import com.example.insightapp.ui.theme.CardBackground
import com.example.insightapp.ui.theme.TextPrimary
import com.example.insightapp.ui.theme.TextSecondary

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SymptomTrendsSection(
    data: List<SymptomData>,
    modifier: Modifier = Modifier
) {
    val totalPercent = data.sumOf { it.percentage }
    val gapAngle = 4f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .padding(20.dp)
    ) {
        Text(
            text = "Body Signals",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Symptom Trends",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Compared to last cycle",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Donut + outer badges ──────────────────────────────────────
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            val containerHalf = maxWidth / 2   // Dp
            val badgeSize     = 64.dp
            val badgeHalf     = badgeSize / 2  // 32.dp
            val strokeW       = 40.dp

            // ── Geometry (all in Dp) ──────────────────────────────────
            // outerRingEdge = the ring's physical outer edge from centre
            // outerR        = arc-centre radius (what drawArc uses for its oval)
            // labelR        = badge centre, placed just beyond the ring outer edge
            val outerRingEdge = containerHalf - badgeHalf - 6.dp
            val outerR        = outerRingEdge - strokeW / 2
            val labelR        = outerRingEdge + 6.dp

            // ── Canvas: smooth per-segment SweepGradient ─────────────
            // Native SweepGradient shader = zero stripe artefacts.
            // The 25-arc stepping approach creates a visible seam at
            // every slice boundary due to alpha blending — that's why
            // the old code produced lines in the arc segments.
            Canvas(modifier = Modifier.fillMaxSize()) {
                val outerRPx  = outerR.toPx()
                val strokeWPx = strokeW.toPx()
                val cx = size.width  / 2
                val cy = size.height / 2

                val oval = RectF(cx - outerRPx, cy - outerRPx,
                    cx + outerRPx, cy + outerRPx)

                var startAngle = -90f

                drawIntoCanvas { canvas ->
                    data.forEach { segment ->
                        val sweep = (segment.percentage.toFloat() / totalPercent) * 360f - gapAngle

                        // Rotate canvas so this segment's leading edge sits at
                        // drawing-angle 0 (the "right"/3-o'clock direction).
                        //   rotate(startAngle) → drawing 0° appears at `startAngle` on screen
                        //   SweepGradient pos 0       → segment start  ✓
                        //   SweepGradient pos sweep/360 → segment end  ✓
                        canvas.nativeCanvas.save()
                        canvas.nativeCanvas.rotate(startAngle, cx, cy)

                        val paint = NativePaint().apply {
                            isAntiAlias = true
                            style       = NativePaint.Style.STROKE
                            strokeWidth = strokeWPx
                            strokeCap   = NativePaint.Cap.BUTT

                            // Full colour → faded (~45 % alpha) across the arc.
                            // The third stop at position 1f locks the tail of the
                            // gradient so it doesn't wrap back visibly.
                            shader = SweepGradient(
                                cx, cy,
                                intArrayOf(
                                    segment.color.toArgb(),
                                    segment.color.copy(alpha = 0.45f).toArgb(),
                                    segment.color.copy(alpha = 0.45f).toArgb()
                                ),
                                floatArrayOf(0f, sweep / 360f, 1f)
                            )
                        }

                        canvas.nativeCanvas.drawArc(oval, 0f, sweep, false, paint)
                        canvas.nativeCanvas.restore()

                        startAngle += sweep + gapAngle
                    }
                }
            }

            // ── Badge labels — centred just OUTSIDE the ring ──────────
            var labelStartAngle = -90f

            data.forEach { segment ->
                val sweep    = (segment.percentage.toFloat() / totalPercent) * 360f - gapAngle
                val midAngle = labelStartAngle + sweep / 2f
                val midRad   = midAngle.toDouble() * PI / 180.0

                val offsetX = (cos(midRad).toFloat() * labelR.value).dp
                val offsetY = (sin(midRad).toFloat() * labelR.value).dp

                Box(
                    modifier = Modifier
                        .offset(x = offsetX, y = offsetY)
                        .size(badgeSize)
                        .shadow(
                            elevation    = 6.dp,
                            shape        = CircleShape,
                            ambientColor = TextPrimary.copy(alpha = 0.08f),
                            spotColor    = TextPrimary.copy(alpha = 0.12f)
                        )
                        .background(color = CardBackground, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text  = "${segment.percentage}%",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize   = 14.sp
                            ),
                            color = TextPrimary
                        )
                        Text(
                            text     = segment.name,
                            style    = MaterialTheme.typography.labelSmall.copy(
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color    = TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                labelStartAngle += sweep + gapAngle
            }
        }
    }
}