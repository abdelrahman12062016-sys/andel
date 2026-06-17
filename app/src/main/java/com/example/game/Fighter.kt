package com.example.game

import androidx.compose.ui.graphics.Color

enum class SpecialActionType {
    FIREBALL,
    SWORD_DASH,
    ARROW_SHOT,
    THUNDER_BOLT,
    EARTHQUAKE,
    STONE_DROP
}

enum class FighterSelection(
    val characterName: String,
    val title: String,
    val primaryColor: Color,
    val speed: Float,
    val weight: Float, // Higher means less knockback
    val jumpPower: Float,
    val attackPower: Float,
    val specialType: SpecialActionType,
    val description: String,
    val normalMoveName: String,
    val specialMoveName: String
) {
    PLUMBER_JOE(
        characterName = "Plumber Joe",
        title = "Superstar Plumber",
        primaryColor = Color(0xFFE53935), // Red
        speed = 4.8f,
        weight = 1.0f,
        jumpPower = 11.5f,
        attackPower = 12f,
        specialType = SpecialActionType.FIREBALL,
        description = "Balanced, versatile fighter with excellent recovery. Shoots bouncy fireballs for space control.",
        normalMoveName = "Spin Punch",
        specialMoveName = "Fireball Shoot"
    ),
    SHADOW_HUNTER(
        characterName = "Shadow Hunter",
        title = "The Dark Blade",
        primaryColor = Color(0xFF6A1B9A), // Deep Purple
        speed = 5.2f,
        weight = 1.1f,
        jumpPower = 9.8f,
        attackPower = 14f,
        specialType = SpecialActionType.SWORD_DASH,
        description = "Swift and lethal ninja. Executes a lightning-fast Sword Dash that slices through targets.",
        normalMoveName = "Katana Slash",
        specialMoveName = "Shadow Dash Cut"
    ),
    ELVEN_ARCHER(
        characterName = "Elven Archer",
        title = "Woodland Guardian",
        primaryColor = Color(0xFF2E7D32), // Forest Green
        speed = 5.0f,
        weight = 0.85f,
        jumpPower = 11.2f,
        attackPower = 10f,
        specialType = SpecialActionType.ARROW_SHOT,
        description = "Long-range marksman. Keeps opponents at bay with lightning-fast arrows across the battlefield.",
        normalMoveName = "Bow-Smash",
        specialMoveName = "Piercing Arrow"
    ),
    SPARKY(
        characterName = "Sparky",
        title = "Electric Rodent",
        primaryColor = Color(0xFFFBC02D), // Golden Yellow
        speed = 6.4f,
        weight = 0.72f,
        jumpPower = 12.5f,
        attackPower = 9f,
        specialType = SpecialActionType.THUNDER_BOLT,
        description = "Extremely fast, nimble, but featherlight. Conjures electrical thunderbolts from the heavens.",
        normalMoveName = "Tail Whip",
        specialMoveName = "Thunder Strike"
    ),
    IRON_GOLEM(
        characterName = "Iron Golem",
        title = "Ancient Aegis",
        primaryColor = Color(0xFF00ACC1), // Cyan Steel
        speed = 3.2f,
        weight = 1.6f,
        jumpPower = 8.2f,
        attackPower = 18f,
        specialType = SpecialActionType.EARTHQUAKE,
        description = "Slow, heavy colossus. Impervious to light attacks and triggers mini-earthquakes to shatter foes.",
        normalMoveName = "Heavy Slam",
        specialMoveName = "Earthquake Shaker"
    ),
    PINK_PUFF(
        characterName = "Pink Puff",
        title = "Cosmic Swallower",
        primaryColor = Color(0xFFEC407A), // Hot Pink
        speed = 4.2f,
        weight = 0.78f,
        jumpPower = 8.5f,
        attackPower = 11f,
        specialType = SpecialActionType.STONE_DROP,
        description = "Uniquely floaty fighter with extraordinary 5x multi-jumps. Slams down as solid stone.",
        normalMoveName = "Air Kick",
        specialMoveName = "Anvil Slam"
    )
}

data class FighterState(
    val selection: FighterSelection,
    var x: Float = 0f,
    var y: Float = 0f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var isFacingLeft: Boolean = false,
    var damagePercentage: Int = 0,
    var stockCount: Int = 3,
    var maxJumps: Int = if (selection == FighterSelection.PINK_PUFF) 5 else 2,
    var jumpsRemaining: Int = maxJumps,
    var isShieldActive: Boolean = false,
    var shieldHealth: Float = 100f, // 0 to 100
    var isHitStun: Boolean = false,
    var hitStunTicks: Int = 0,
    var isCrouching: Boolean = false,
    var attackCooldown: Int = 0,
    var specialCooldown: Int = 0,
    var respawnTimer: Int = 0, // > 0 means currently respawning
    var isInvincible: Boolean = false,
    var invincibilityTicks: Int = 0,
    var isCpu: Boolean = false,
    var specialActionActive: Boolean = false, // True if currently doing visual move e.g. Stone Drop
    var totalDamageDealt: Int = 0,
    var totalKOs: Int = 0,
    var totalFalls: Int = 0
) {
    val isDead: Boolean get() = stockCount <= 0
    val isRespawning: Boolean get() = respawnTimer > 0

    fun resetStatsForRespawn(spawnX: Float, spawnY: Float) {
        x = spawnX
        y = spawnY
        vx = 0f
        vy = 0f
        damagePercentage = 0
        jumpsRemaining = maxJumps
        isShieldActive = false
        shieldHealth = 100f
        isHitStun = false
        hitStunTicks = 0
        isCrouching = false
        attackCooldown = 0
        specialCooldown = 0
        respawnTimer = 0
        isInvincible = true
        invincibilityTicks = 90 // 1.5 seconds of invincibility
        specialActionActive = false
    }
}
