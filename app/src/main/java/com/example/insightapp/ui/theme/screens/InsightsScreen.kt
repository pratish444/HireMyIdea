package com.example.insightapp.ui.theme.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.insightapp.data.SampleData
import com.example.insightapp.ui.theme.BackgroundColor
import com.example.insightapp.ui.theme.TextPrimary
import com.example.insightapp.ui.theme.sections.CycleTrendsSection
import com.example.insightapp.ui.theme.sections.LifestyleImpactSection
import com.example.insightapp.ui.theme.sections.StabilitySummarySection
import com.example.insightapp.ui.theme.sections.SymptomTrendsSection
import com.example.insightapp.ui.theme.sections.WeightTrendsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Subtle warm gradient overlay at top (peach/orange fading)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x22FFCCBC), // subtle warm peach
                            Color(0x14FFE0B2), // fading warm
                            Color(0x08FFF3E0), // very faint warm
                            Color.Transparent
                        )
                    )
                )
        )

        // Additional radial warmth from top-right
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x18FFAB91), // warm peach from right
                            Color(0x10FF8A65)  // subtle orange tint
                        )
                    )
                )
        )

        // Everything scrolls together — header + content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 16.dp)
        ) {
            // Top App Bar — scrolls with content
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Insights",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* menu action */ }) {
                        FourDotsIcon()
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            // Section cards
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Stability Summary
                StabilitySummarySection(
                    data = SampleData.stabilityData
                )

                // 2. Cycle Trends
                CycleTrendsSection(
                    dataPages = SampleData.cycleTrendsPages
                )

                // 3. Body & Metabolic Trends (Weight)
                WeightTrendsSection(
                    data = SampleData.weightData
                )

                // 4. Symptom Trends (Body Signals + Donut Chart)
                SymptomTrendsSection(
                    data = SampleData.symptomTrends
                )

                // 5. Lifestyle Impact (Heatmap)
                LifestyleImpactSection(
                    data = SampleData.lifestyleImpact
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * Custom 4-dot grid icon (2x2 arrangement) matching the Figma header.
 * Uses dark teal colored rounded rectangles.
 */
@Composable
private fun FourDotsIcon(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val dotSize = 8.dp.toPx()
        val gap = 4.dp.toPx()
        val totalW = dotSize * 2 + gap
        val startX = (size.width - totalW) / 2
        val startY = (size.height - totalW) / 2
        val radius = 2.5.dp.toPx()

        val colors = listOf(
            Color(0xFF5B8A8A),  // teal
            Color(0xFF7BA3A3),  // lighter teal
            Color(0xFF8BB5B0),  // soft teal
            Color(0xFF5B8A8A)   // teal
        )

        // Top-left
        drawRoundRect(
            color = colors[0],
            topLeft = Offset(startX, startY),
            size = Size(dotSize, dotSize),
            cornerRadius = CornerRadius(radius)
        )
        // Top-right
        drawRoundRect(
            color = colors[1],
            topLeft = Offset(startX + dotSize + gap, startY),
            size = Size(dotSize, dotSize),
            cornerRadius = CornerRadius(radius)
        )
        // Bottom-left
        drawRoundRect(
            color = colors[2],
            topLeft = Offset(startX, startY + dotSize + gap),
            size = Size(dotSize, dotSize),
            cornerRadius = CornerRadius(radius)
        )
        // Bottom-right
        drawRoundRect(
            color = colors[3],
            topLeft = Offset(startX + dotSize + gap, startY + dotSize + gap),
            size = Size(dotSize, dotSize),
            cornerRadius = CornerRadius(radius)
        )
    }
}
