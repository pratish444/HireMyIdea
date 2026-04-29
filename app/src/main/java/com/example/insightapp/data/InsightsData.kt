package com.example.insightapp.data


import androidx.compose.ui.graphics.Color

// ── Data Models ──

data class StabilityData(
    val score: Int = 78,
    val isImproving: Boolean = true,
    val monthlyData: List<List<Float>> = listOf(
        listOf(24.5f, 25.5f, 27.0f, 29.5f, 31.5f, 33.0f),
        listOf(24.3f, 25.0f, 26.0f, 28.0f, 29.5f, 31.0f),
        listOf(24.0f, 24.5f, 25.0f, 26.5f, 27.5f, 29.0f)
    ),
    val months: List<String> = listOf("Jan", "Feb", "Mar", "Apr")
)

data class CycleBarData(
    val label: String,
    val totalDays: Int,
    val menstrualFraction: Float,
    val ovulationPosition: Float,
    val coralOffset: Float = 0f
)

data class WeightDataPoint(
    val label: String,
    val weight: Float
)

data class SymptomData(
    val name: String,
    val percentage: Int,
    val color: Color
)

// icon field removed — not used in the Figma design.
// baseColor added so each row renders with its own hue.
data class LifestyleRow(
    val label: String,
    val values: List<Int>,   // 9 entries, 0 = empty, 1–4 = intensity
    val baseColor: Color
)

// ── Sample Data ──

object SampleData {

    val stabilityData = StabilityData()

    val cycleTrendsPages = listOf(
        listOf(
            CycleBarData("Jan", 28, 0.20f, 0.42f, 0.00f),
            CycleBarData("Feb", 30, 0.15f, 0.62f, 0.05f),
            CycleBarData("Mar", 28, 0.22f, 0.52f, 0.04f),
            CycleBarData("Apr", 32, 0.14f, 0.58f, 0.06f),
            CycleBarData("May", 28, 0.19f, 0.46f, 0.03f),
            CycleBarData("Jun", 28, 0.24f, 0.68f, 0.00f)
        ),
        listOf(
            CycleBarData("Jul", 29, 0.21f, 0.55f, 0.04f),
            CycleBarData("Aug", 27, 0.16f, 0.40f, 0.00f),
            CycleBarData("Sep", 30, 0.18f, 0.64f, 0.07f),
            CycleBarData("Oct", 28, 0.23f, 0.50f, 0.03f),
            CycleBarData("Nov", 31, 0.13f, 0.60f, 0.00f),
            CycleBarData("Dec", 28, 0.20f, 0.44f, 0.05f)
        )
    )

    val weightData = listOf(
        WeightDataPoint("Jan", 28f),
        WeightDataPoint("Feb", 40f),
        WeightDataPoint("Mar", 32f),
        WeightDataPoint("Apr", 68f),
        WeightDataPoint("May", 55f)
    )

    val symptomTrends = listOf(
        SymptomData("Bloating", 31, DonutBloating),
        SymptomData("Mood",     30, DonutMood),
        SymptomData("Fatigue",  21, DonutFatigue),
        SymptomData("Acne",     17, DonutAcne)
    )

    // Figma fill pattern:
    //   Sleep    7/9 filled  — lavender-purple, fading right
    //   Hydrate  3/9 filled  — coral/salmon
    //   Caffeine 5/9 filled  — dark sage teal
    //   Exercise 4/9 filled  — soft rose-pink
    val lifestyleImpact = listOf(
        LifestyleRow(
            label     = "Sleep",
            values    = listOf(4, 4, 4, 4, 3, 2, 1, 0, 0),
            baseColor = Color(0xFFA89CCB)
        ),
        LifestyleRow(
            label     = "Hydrate",
            values    = listOf(4, 3, 2, 0, 0, 0, 0, 0, 0),
            baseColor = Color(0xFFE88585)
        ),
        LifestyleRow(
            label     = "Caffeine",
            values    = listOf(4, 4, 3, 2, 1, 0, 0, 0, 0),
            baseColor = Color(0xFF6E9688)
        ),
        LifestyleRow(
            label     = "Exercise",
            values    = listOf(1, 4, 3, 2, 0, 0, 0, 0, 0),
            baseColor = Color(0xFFF4A0A8)
        )
    )
}