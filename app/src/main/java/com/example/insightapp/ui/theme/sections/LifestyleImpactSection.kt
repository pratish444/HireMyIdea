package com.example.insightapp.ui.theme.sections


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insightapp.data.LifestyleRow
import com.example.insightapp.ui.theme.CardBackground
import com.example.insightapp.ui.theme.DividerColor
import com.example.insightapp.ui.theme.TextPrimary
import com.example.insightapp.ui.theme.TextSecondary


// Empty cell colour — subtle lavender-grey matching the Figma background
private val CellEmpty = Color(0xFFEFEDF6)

// Maps a 0–4 intensity value to an alpha against the row's base colour
private fun intensityAlpha(level: Int): Float = when (level) {
    0    -> 0f          // empty — handled separately
    1    -> 0.30f
    2    -> 0.55f
    3    -> 0.75f
    else -> 1.00f       // 4 → full
}

@Composable
fun LifestyleImpactSection(
    data: List<LifestyleRow>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .padding(20.dp)
    ) {
        // ── Section heading ───────────────────────────────────────────
        Text(
            text       = "Lifestyle Impact",
            style      = MaterialTheme.typography.titleLarge,
            color      = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Sub-header row ────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = "Correlation Strength",
                style      = MaterialTheme.typography.bodyMedium,
                color      = TextPrimary,
                fontWeight = FontWeight.Medium
            )

            // Period selector pill — matches Figma border style
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, DividerColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text  = "4 months",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text  = "∨",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ── Heatmap rows ──────────────────────────────────────────────
        data.forEach { row ->
            HeatmapRow(row = row)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HeatmapRow(
    row: LifestyleRow,
    modifier: Modifier = Modifier
) {
    Row(
        modifier          = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Label ─────────────────────────────────────────────────────
        Text(
            text     = row.label,
            style    = MaterialTheme.typography.bodySmall,
            color    = TextPrimary,
            modifier = Modifier.width(60.dp),
            maxLines = 1
        )

        Spacer(modifier = Modifier.width(10.dp))

        // ── 9 heatmap cells ──────────────────────────────────────────
        Row(
            modifier              = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            row.values.forEach { level ->
                val cellColor = if (level == 0) {
                    CellEmpty
                } else {
                    row.baseColor.copy(alpha = intensityAlpha(level))
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.4f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(cellColor)
                )
            }
        }
    }
}