package com.example.game

import androidx.compose.ui.graphics.Color

data class Platform(
    val xMin: Float,
    val xMax: Float,
    val y: Float,
    val isSemiSolid: Boolean = true // True means can jump UP through it and DROP down through it
)

enum class StageSelection(
    val displayName: String,
    val subtitle: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    // Screen is virtual size: 800 width, 500 height
    val mainPlatform: Platform,
    val floatingPlatforms: List<Platform>,
    val spawnX1: Float,
    val spawnY1: Float,
    val spawnX2: Float,
    val spawnY2: Float,
    val blastZoneLeft: Float = -150f,
    val blastZoneRight: Float = 950f,
    val blastZoneTop: Float = -150f,
    val blastZoneBottom: Float = 620f
) {
    FINAL_FRONTIER(
        displayName = "Final Frontier",
        subtitle = "Deep Space Grid",
        description = "A pristine, flat obsidian platform floating in infinite cosmic stardust. No floating shelves—pure combat skill.",
        primaryColor = Color(0xFF1E1B4B), // Cosmic deep indigo
        secondaryColor = Color(0xFF818CF8), // Glowing violet neon Accent
        mainPlatform = Platform(xMin = 150f, xMax = 650f, y = 380f, isSemiSolid = false),
        floatingPlatforms = emptyList(),
        spawnX1 = 250f,
        spawnY1 = 300f,
        spawnX2 = 550f,
        spawnY2 = 300f
    ),
    BATTLEFIELD_PEAKS(
        displayName = "Battlefield Peaks",
        subtitle = "Mountain Ruins",
        description = "The absolute tournament classic. A wide rock foundation topped by three compact wooden ledges for aerial combos.",
        primaryColor = Color(0xFF1A2E40), // Forest alpine slate
        secondaryColor = Color(0xFFF59E0B), // Warm amber stone Accent
        mainPlatform = Platform(xMin = 120f, xMax = 680f, y = 390f, isSemiSolid = false),
        floatingPlatforms = listOf(
            Platform(xMin = 200f, xMax = 350f, y = 290f, isSemiSolid = true),
            Platform(xMin = 450f, xMax = 600f, y = 290f, isSemiSolid = true),
            Platform(xMin = 300f, xMax = 500f, y = 190f, isSemiSolid = true)
        ),
        spawnX1 = 220f,
        spawnY1 = 300f,
        spawnX2 = 580f,
        spawnY2 = 300f
    ),
    SKY_SANCTUARY(
        displayName = "Sky Sanctuary",
        subtitle = "Cloud Castle",
        description = "A split-level botanical terrace high in the clouds. Generous vertical screen room but narrow recovery edges.",
        primaryColor = Color(0xFF0F172A), // Dark sky slate
        secondaryColor = Color(0xFF34D399), // Floating botanical emerald Accent
        mainPlatform = Platform(xMin = 200f, xMax = 600f, y = 400f, isSemiSolid = false),
        floatingPlatforms = listOf(
            Platform(xMin = 100f, xMax = 250f, y = 300f, isSemiSolid = true),
            Platform(xMin = 550f, xMax = 700f, y = 300f, isSemiSolid = true),
            Platform(xMin = 280f, xMax = 520f, y = 240f, isSemiSolid = true)
        ),
        spawnX1 = 260f,
        spawnY1 = 300f,
        spawnX2 = 540f,
        spawnY2 = 300f
    )
}
