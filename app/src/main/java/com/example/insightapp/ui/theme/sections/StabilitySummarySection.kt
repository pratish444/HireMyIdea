package com.example.insightapp.ui.theme.sections

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import com.example.insightapp.data.StabilityData
import com.example.insightapp.ui.theme.CardBackground
import com.example.insightapp.ui.theme.DarkChip
import com.example.insightapp.ui.theme.StabilityLayer1
import com.example.insightapp.ui.theme.StabilityLayer2
import com.example.insightapp.ui.theme.StabilityLayer3
import com.example.insightapp.ui.theme.TextPrimary
import com.example.insightapp.ui.theme.TextSecondary

@Composable
fun StabilitySummarySection(
    data: StabilityData,
    modifier: Modifier = Modifier
) {
    // Track selected point for interactive tooltip
    var selectedPixelX by remember { mutableStateOf<Float?>(null) }
    var selectedPixelY by remember { mutableStateOf<Float?>(null) }

    val yMin = 23f
    val yMax = 34f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFF8F5), // subtle warm peach tint
                        CardBackground      // fades to white
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            // Added dismiss listener: Clicking anywhere on the card background (outside the chart)
            // will clear the selection.
            .pointerInput(Unit) {
                detectTapGestures {
                    selectedPixelX = null
                    selectedPixelY = null
                }
            }
            .padding(20.dp)
    ) {
        // Section Title
        Text(
            text = "Stability Summary",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "Based on your recent logs and symptom\npatterns.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Stability Score (NO badge here — badge appears on chart interaction)
        Column {
            Text(
                text = "Stability Score",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Text(
                text = "${data.score}%",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Chart + Y-axis labels
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Y-axis labels (aligned to chart grid)
            Column(
                modifier = Modifier
                    .height(160.dp)
                    .padding(top = 8.dp, bottom = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "32d",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = "28d",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = "24d",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Chart area with interactive tooltip
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp)
            ) {
                // Area chart canvas
                StabilityAreaChart(
                    dataLayers = data.monthlyData,
                    yMin = yMin,
                    yMax = yMax,
                    selectedX = selectedPixelX,
                    onTap = { px, py ->
                        selectedPixelX = px
                        selectedPixelY = py
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Interactive Tooltip overlay
                if (selectedPixelX != null && selectedPixelY != null) {
                    val density = LocalDensity.current
                    val tooltipXDp = with(density) { selectedPixelX!!.toDp() }
                    val tooltipYDp = with(density) { selectedPixelY!!.toDp() }

                    Box(
                        modifier = Modifier
                            .offset(
                                x = (tooltipXDp - 48.dp).coerceAtLeast(0.dp),
                                y = (tooltipYDp - 58.dp).coerceAtLeast(0.dp)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkChip)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Stability",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Improving",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
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
            data.months.forEach { month ->
                Text(
                    text = month,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

/**
 * Multi-layer area chart with smooth bezier curves, gradient fills,
 * tap-to-inspect interaction showing a teal dot + dashed vertical guide.
 */
@Composable
private fun StabilityAreaChart(
    dataLayers: List<List<Float>>,
    yMin: Float,
    yMax: Float,
    selectedX: Float?,
    onTap: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // Chart layer colors: darkest (bottom) to lightest (top)
    val layerFillColors = listOf(
        listOf(StabilityLayer1.copy(alpha = 0.7f), StabilityLayer1.copy(alpha = 0.15f)),
        listOf(StabilityLayer2.copy(alpha = 0.55f), StabilityLayer2.copy(alpha = 0.1f)),
        listOf(StabilityLayer3.copy(alpha = 0.4f), StabilityLayer3.copy(alpha = 0.05f))
    )
    val lineColors = listOf(
        StabilityLayer1.copy(alpha = 0.85f),
        StabilityLayer2.copy(alpha = 0.7f),
        StabilityLayer3.copy(alpha = 0.5f)
    )

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // Calculate the Y position of the topmost layer at this X
                    val topLayer = dataLayers.firstOrNull() ?: return@detectTapGestures
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    val stepX = w / (topLayer.size - 1).coerceAtLeast(1)
                    val range = yMax - yMin

                    val idx = (offset.x / stepX).toInt().coerceIn(0, topLayer.size - 2)
                    val t = ((offset.x - idx * stepX) / stepX).coerceIn(0f, 1f)
                    val interpolatedValue = topLayer[idx] + t * (topLayer[idx + 1] - topLayer[idx])
                    val yScreen = h - ((interpolatedValue - yMin) / range * h * 0.85f + h * 0.05f)

                    onTap(offset.x, yScreen)
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val range = yMax - yMin

        // Helper to map data value to screen Y
        fun valueToY(value: Float): Float =
            height - ((value - yMin) / range * height * 0.85f + height * 0.05f)

        // Draw each filled area layer (iterate reversed so bottom/darkest draws first)
        for (layerIdx in dataLayers.indices.reversed()) {
            val data = dataLayers[layerIdx]
            val linePath = Path()
            val fillPath = Path()
            val stepX = width / (data.size - 1).coerceAtLeast(1)

            val points = data.mapIndexed { i, value ->
                Offset(i * stepX, valueToY(value))
            }

            if (points.isNotEmpty()) {
                linePath.moveTo(points[0].x, points[0].y)
                fillPath.moveTo(points[0].x, points[0].y)

                for (i in 1 until points.size) {
                    val cpX = (points[i - 1].x + points[i].x) / 2
                    linePath.cubicTo(cpX, points[i - 1].y, cpX, points[i].y, points[i].x, points[i].y)
                    fillPath.cubicTo(cpX, points[i - 1].y, cpX, points[i].y, points[i].x, points[i].y)
                }

                // Close fill area down to bottom
                fillPath.lineTo(points.last().x, height)
                fillPath.lineTo(points.first().x, height)
                fillPath.close()

                // Gradient fill
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = layerFillColors[layerIdx % layerFillColors.size],
                        startY = 0f,
                        endY = height
                    )
                )

                // Curve outline
                drawPath(
                    path = linePath,
                    color = lineColors[layerIdx % lineColors.size],
                    style = Stroke(width = 2f)
                )
            }
        }

        // ── Selected-point indicator (dashed line + teal dot) ──
        selectedX?.let { sx ->
            val topLayer = dataLayers.firstOrNull() ?: return@let
            val stepX = width / (topLayer.size - 1).coerceAtLeast(1)
            val idx = (sx / stepX).toInt().coerceIn(0, topLayer.size - 2)
            val t = ((sx - idx * stepX) / stepX).coerceIn(0f, 1f)
            val value = topLayer[idx] + t * (topLayer[idx + 1] - topLayer[idx])
            val yScreen = valueToY(value)

            // Dashed vertical guide line
            drawLine(
                color = Color(0xFF999999),
                start = Offset(sx, yScreen + 10f),
                end = Offset(sx, height),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 5f))
            )

            // Teal indicator dot (outer)
            drawCircle(
                color = Color(0xFF7EBFB5),
                radius = 8f,
                center = Offset(sx, yScreen)
            )
            // White inner dot
            drawCircle(
                color = Color.White,
                radius = 3.5f,
                center = Offset(sx, yScreen)
            )
        }
    }
}