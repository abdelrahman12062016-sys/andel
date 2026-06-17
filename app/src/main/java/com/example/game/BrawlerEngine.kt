package com.example.game

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt

enum class EffectType {
    DAMAGE_POP,
    SMASH_RING,
    BLAST_RING,
    SHIELD_RING,
    SPARK_BURST,
    PROJECTILE_BLOOM
}

data class GameEffect(
    val id: Long,
    var x: Float,
    var y: Float,
    val text: String,
    val color: Color,
    val type: EffectType,
    val totalTicks: Int,
    var ticksRemaining: Int = totalTicks,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var sizeScale: Float = 1.0f
)

data class Projectile(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val isFromPlayer1: Boolean,
    val color: Color,
    val radius: Float,
    val specialType: SpecialActionType,
    var bounceCount: Int = 0,
    var lifeTicks: Int = 120,
    val damage: Int = 8,
    val knockbackPower: Float = 6f
)

class BrawlerEngine(
    val selectedPlayer: FighterSelection,
    val selectedCpu: FighterSelection,
    val selectedStage: StageSelection
) {
    // Engine constants
    val GRAVITY = 0.42f
    val TERMINAL_VELOCITY = 12f
    val GROUND_FRICTION = 0.85f
    val AIR_FRICTION = 0.94f
    val FIGHTER_RADIUS = 22f

    // Screen Viewport size: 800 width, 500 height

    // Fighters
    var player1 = FighterState(
        selection = selectedPlayer,
        isCpu = false
    )
    var player2 = FighterState(
        selection = selectedCpu,
        isCpu = true
    )

    // Lists of active entities
    var projectiles = mutableListOf<Projectile>()
    var effects = mutableListOf<GameEffect>()

    // Global match variables
    var matchTimeSeconds = 120
    var matchTicks = 0
    var isMatchOver = false
    var victoryMessage = ""
    var idCounter = 0L

    init {
        resetMatch()
    }

    fun resetMatch() {
        player1.resetStatsForRespawn(selectedStage.spawnX1, selectedStage.spawnY1)
        player1.stockCount = 3
        player2.resetStatsForRespawn(selectedStage.spawnX2, selectedStage.spawnY2)
        player2.stockCount = 3
        projectiles.clear()
        effects.clear()
        matchTimeSeconds = 120
        matchTicks = 0
        isMatchOver = false
        victoryMessage = ""
    }

    private fun getNextId(): Long {
        return ++idCounter
    }

    // High fidelity frame update tick (60fps)
    fun updateTicks(
        p1LeftPressed: Boolean,
        p1RightPressed: Boolean,
        p1DownPressed: Boolean,
        p1JumpPressed: Boolean,
        p1AttackPressed: Boolean,
        p1SpecialPressed: Boolean,
        p1ShieldPressed: Boolean
    ) {
        if (isMatchOver) return

        matchTicks++
        if (matchTicks % 60 == 0 && matchTimeSeconds > 0) {
            matchTimeSeconds--
            if (matchTimeSeconds <= 0) {
                concludeMatchByTime()
            }
        }

        // 1. Fighter Cooldowns & Status Regeneration
        updateFighterTicks(player1)
        updateFighterTicks(player2)

        // 2. Player Controls Processing
        processFighterInput(
            fighter = player1,
            opponent = player2,
            left = p1LeftPressed,
            right = p1RightPressed,
            down = p1DownPressed,
            jump = p1JumpPressed,
            attack = p1AttackPressed,
            special = p1SpecialPressed,
            shield = p1ShieldPressed,
            isP1 = true
        )

        // 3. CPU Intelligence Loop
        processCpuAI()

        // 4. Kinematics, Gravity, and Vector Integration
        applyFighterPhysics(player1, p1DownPressed)
        applyFighterPhysics(player2, false) // CPU registers down press dynamically in AI

        // 5. Stage Bounds and Blast Zone KO Calculations
        checkBlastZones(player1, player2)
        checkBlastZones(player2, player1)

        // 6. Projectiles and Energy Balls Physics
        updateProjectiles()

        // 7. Visual Particle Effects Simulation
        updateEffects()
    }

    private fun updateFighterTicks(fighter: FighterState) {
        if (fighter.isRespawning) {
            fighter.respawnTimer--
            return
        }

        if (fighter.attackCooldown > 0) fighter.attackCooldown--
        if (fighter.specialCooldown > 0) fighter.specialCooldown--

        if (fighter.isHitStun) {
            fighter.hitStunTicks--
            if (fighter.hitStunTicks <= 0) {
                fighter.isHitStun = false
            }
        }

        if (fighter.isInvincible) {
            fighter.invincibilityTicks--
            if (fighter.invincibilityTicks <= 0) {
                fighter.isInvincible = false
            }
        }

        // Shield energy handling
        if (fighter.isShieldActive) {
            fighter.shieldHealth -= 1.1f
            if (fighter.shieldHealth <= 0f) {
                // Shield Explodes! Exhaustive Stun!
                fighter.isShieldActive = false
                fighter.isHitStun = true
                fighter.hitStunTicks = 150 // Stunned for 2.5 seconds
                triggerShieldPopEffect(fighter.x, fighter.y, fighter.selection.primaryColor)
            }
        } else {
            if (fighter.shieldHealth < 100f) {
                fighter.shieldHealth = (fighter.shieldHealth + 0.4f).coerceAtMost(100f)
            }
        }
    }

    private fun processFighterInput(
        fighter: FighterState,
        opponent: FighterState,
        left: Boolean,
        right: Boolean,
        down: Boolean,
        jump: Boolean,
        attack: Boolean,
        special: Boolean,
        shield: Boolean,
        isP1: Boolean
    ) {
        if (fighter.isDead || fighter.isRespawning || fighter.isHitStun) return

        // Holding Shield prevents actions but defends all hits
        if (shield && fighter.shieldHealth > 15f) {
            fighter.isShieldActive = true
            fighter.vx *= 0.5f // Drag speed
            return
        } else {
            fighter.isShieldActive = false
        }

        // Horizontal Movement
        val accel = fighter.selection.speed * 0.13f
        if (left) {
            fighter.vx -= accel
            fighter.isFacingLeft = true
        } else if (right) {
            fighter.vx += accel
            fighter.isFacingLeft = false
        }

        // Crouching
        fighter.isCrouching = down && abs(fighter.vx) < 1f && fighter.vy == 0f

        // Jump Execution
        if (jump && fighter.jumpsRemaining > 0) {
            fighter.vy = -fighter.selection.jumpPower
            fighter.jumpsRemaining--
            // Spawn jump cloud ring
            triggerSparkBurst(fighter.x, fighter.y + FIGHTER_RADIUS, Color.White, 6)
        }

        // Attack Execution (A)
        if (attack && fighter.attackCooldown == 0) {
            executeAttack(fighter, opponent, isP1)
        }

        // Special Attack Execution (B)
        if (special && fighter.specialCooldown == 0) {
            executeSpecialMove(fighter, opponent, isP1)
        }
    }

    private fun executeAttack(fighter: FighterState, opponent: FighterState, isP1: Boolean) {
        fighter.attackCooldown = 15 // Quarter second cooldown

        // Attack Hitbox is a sector ahead of the fighter
        val direction = if (fighter.isFacingLeft) -1f else 1f
        val hitboxX = fighter.x + (direction * 25f)
        val hitboxY = fighter.y
        val range = 45f

        // Add visual swipe spark
        triggerSparkBurst(hitboxX, hitboxY, fighter.selection.primaryColor, 4)

        if (checkFighterOverlap(hitboxX, hitboxY, range, opponent)) {
            // Apply Hit!
            dealCombatBlow(
                attacker = fighter,
                defender = opponent,
                damage = (fighter.selection.attackPower * 0.8f).toInt().coerceAtLeast(6),
                baseKnockback = 6.5f,
                directionX = direction,
                directionY = -0.4f
            )
        }
    }

    private fun executeSpecialMove(fighter: FighterState, opponent: FighterState, isP1: Boolean) {
        fighter.specialCooldown = 45 // 0.75 second cooldown
        val direction = if (fighter.isFacingLeft) -1f else 1f

        when (fighter.selection.specialType) {
            SpecialActionType.FIREBALL -> {
                // Shoot projectile (bouncing)
                projectiles.add(
                    Projectile(
                        id = getNextId(),
                        x = fighter.x + (direction * 30f),
                        y = fighter.y - 4f,
                        vx = direction * 7.5f,
                        vy = -2f,
                        isFromPlayer1 = isP1,
                        color = Color(0xFFFF5722), // Fire red
                        radius = 10f,
                        specialType = SpecialActionType.FIREBALL,
                        damage = 9,
                        knockbackPower = 5.5f
                    )
                )
                triggerSparkBurst(fighter.x + (direction * 20f), fighter.y, Color(0xFFFF9800), 5)
            }
            SpecialActionType.SWORD_DASH -> {
                // Fast forward strike with iframe
                fighter.specialActionActive = true
                val dashDist = 110f * direction
                val oldX = fighter.x
                fighter.x = (fighter.x + dashDist).coerceIn(10f, 790f)
                fighter.vx = direction * 4f
                fighter.invincibilityTicks = 8 // Bulletproof dash frames

                // Spark trace
                triggerSparkBurst((oldX + fighter.x) / 2, fighter.y, fighter.selection.primaryColor, 8)

                // Check collision line
                val start = if (direction > 0) oldX else fighter.x
                val end = if (direction > 0) fighter.x else oldX
                if (opponent.x in start..end && abs(opponent.y - fighter.y) < 45f) {
                    dealCombatBlow(
                        attacker = fighter,
                        defender = opponent,
                        damage = 16,
                        baseKnockback = 8.8f,
                        directionX = direction,
                        directionY = -0.3f
                    )
                }
            }
            SpecialActionType.ARROW_SHOT -> {
                // Fast Piercing Laser-Arrow
                projectiles.add(
                    Projectile(
                        id = getNextId(),
                        x = fighter.x + (direction * 30f),
                        y = fighter.y - 1f,
                        vx = direction * 14f,
                        vy = 0f,
                        isFromPlayer1 = isP1,
                        color = Color(0xFF4CAF50), // Green Arrow
                        radius = 6f,
                        specialType = SpecialActionType.ARROW_SHOT,
                        damage = 7,
                        knockbackPower = 4.8f,
                        lifeTicks = 60
                    )
                )
                triggerSparkBurst(fighter.x + (direction * 25f), fighter.y, Color(0xFF81C784), 4)
            }
            SpecialActionType.THUNDER_BOLT -> {
                // Spawn electricity from heaven directly on head of enemy
                val enemyTargetX = opponent.x
                projectiles.add(
                    Projectile(
                        id = getNextId(),
                        x = enemyTargetX,
                        y = 20f,
                        vx = 0f,
                        vy = 12f,
                        isFromPlayer1 = isP1,
                        color = Color(0xFFFFEB3B), // Spark spark
                        radius = 12f,
                        specialType = SpecialActionType.THUNDER_BOLT,
                        damage = 14,
                        knockbackPower = 9.0f,
                        lifeTicks = 35
                    )
                )
                triggerSparkBurst(enemyTargetX, 30f, Color.White, 3)
            }
            SpecialActionType.EARTHQUAKE -> {
                // Slam floor - launches shockwave projectiles left & right if on floor
                triggerSparkBurst(fighter.x, fighter.y + FIGHTER_RADIUS, Color(0xFF00E5FF), 10)
                if (fighter.vy == 0f) {
                    projectiles.add(
                        Projectile(
                            id = getNextId(),
                            x = fighter.x - 20f,
                            y = fighter.y + 10f,
                            vx = -7f,
                            vy = 0f,
                            isFromPlayer1 = isP1,
                            color = Color(0xFF00ACC1),
                            radius = 9f,
                            specialType = SpecialActionType.EARTHQUAKE,
                            damage = 11,
                            knockbackPower = 7.0f,
                            lifeTicks = 45
                        )
                    )
                    projectiles.add(
                        Projectile(
                            id = getNextId(),
                            x = fighter.x + 20f,
                            y = fighter.y + 10f,
                            vx = 7f,
                            vy = 0f,
                            isFromPlayer1 = isP1,
                            color = Color(0xFF00ACC1),
                            radius = 9f,
                            specialType = SpecialActionType.EARTHQUAKE,
                            damage = 11,
                            knockbackPower = 7.0f,
                            lifeTicks = 45
                        )
                    )
                }
            }
            SpecialActionType.STONE_DROP -> {
                // Fast Down Plunge
                fighter.vy = 12f
                fighter.vx = 0f
                fighter.specialActionActive = true
                fighter.invincibilityTicks = 20 // Bulletproof down slam frames
                triggerSparkBurst(fighter.x, fighter.y - 15f, Color(0xFFEC407A), 5)
            }
        }
    }

    private fun dealCombatBlow(
        attacker: FighterState,
        defender: FighterState,
        damage: Int,
        baseKnockback: Float,
        directionX: Float,
        directionY: Float
    ) {
        if (defender.isRespawning || defender.isDead) return

        // 1. Shield Check
        if (defender.isShieldActive) {
            defender.shieldHealth -= damage * 1.5f
            triggerSparkBurst(defender.x, defender.y, Color.White, 6)
            // Stiff push but no huge flight
            defender.vx += directionX * 2.5f
            defender.vy += directionY * 1.2f
            return
        }

        if (defender.isInvincible) return

        // 2. Apply Custom Damage
        defender.damagePercentage += damage
        attacker.totalDamageDealt += damage

        // 3. Compute Knockback vector scaled by current damage percentage
        // Classic Smash Bros formula: knockback increases exponentially with damage
        val scalingFactor = (defender.damagePercentage / 90f) + 0.6f
        val trueKnockback = (baseKnockback * scalingFactor) / (defender.selection.weight)

        // Crouching cushions 35% damage knockback
        val finalKnockback = if (defender.isCrouching) trueKnockback * 0.65f else trueKnockback

        defender.vx = directionX * finalKnockback
        defender.vy = directionY * finalKnockback * 0.9f

        // Set stun frames proportional to intensity
        defender.isHitStun = true
        defender.hitStunTicks = (finalKnockback * 3f + 14).toInt().coerceIn(12, 70)

        // 4. Effects & Sparks
        val impactColor = attacker.selection.primaryColor
        triggerDamagePop(defender.x, defender.y - 30f, "$damage%", impactColor)
        triggerSmashRing(defender.x, defender.y, impactColor)

        // Heavy impact triggers aesthetic spark burst
        if (finalKnockback > 9f) {
            triggerSparkBurst(defender.x, defender.y, Color.Red, 12)
        }
    }

    private fun applyFighterPhysics(fighter: FighterState, isDownPressed: Boolean) {
        if (fighter.isDead) return

        if (fighter.isRespawning) {
            fighter.vx = 0f
            fighter.vy = 0f
            return
        }

        // Apply constant Downward Gravity
        fighter.vy += GRAVITY
        if (fighter.vy > TERMINAL_VELOCITY) {
            fighter.vy = TERMINAL_VELOCITY
        }

        // Integrate delta positions
        val oldY = fighter.y
        fighter.x += fighter.vx
        fighter.y += fighter.vy

        // Apply friction to dampen horizontal speed
        val currentFriction = if (fighter.vy == 0f) GROUND_FRICTION else AIR_FRICTION
        fighter.vx *= currentFriction

        // Stop moving when micro velocity
        if (abs(fighter.vx) < 0.05f) {
            fighter.vx = 0f
        }

        // --- PLATFORM COLLISIONS ---
        var landed = false

        // 1. Check solid main platform (Can't fall through it, can't jump up through)
        val main = selectedStage.mainPlatform
        val halfFighterWidth = 14f
        val feetY = fighter.y + FIGHTER_RADIUS

        // Check if character intersected the main platform downward
        if (oldY + FIGHTER_RADIUS <= main.y + 4f && feetY >= main.y - 1f) {
            if (fighter.x + halfFighterWidth >= main.xMin && fighter.x - halfFighterWidth <= main.xMax) {
                // Land on solid floor
                fighter.y = main.y - FIGHTER_RADIUS
                fighter.vy = 0f
                fighter.jumpsRemaining = fighter.maxJumps
                landed = true

                // Pink Puff anvil smash shockwave landing
                if (fighter.selection == FighterSelection.PINK_PUFF && fighter.specialActionActive && fighter.vy == 0f) {
                    executePinkPuffLandSlam(fighter)
                }
            }
        }

        // 2. Check Semi-solid floating platforms
        if (!landed && !isDownPressed) {
            for (plat in selectedStage.floatingPlatforms) {
                if (oldY + FIGHTER_RADIUS <= plat.y + 4f && feetY >= plat.y - 1f) {
                    if (fighter.x + halfFighterWidth >= plat.xMin && fighter.x - halfFighterWidth <= plat.xMax) {
                        // Land on floating platform!
                        // Ensure player is actually moving downwards to snap land
                        if (fighter.vy >= 0f) {
                            fighter.y = plat.y - FIGHTER_RADIUS
                            fighter.vy = 0f
                            fighter.jumpsRemaining = fighter.maxJumps
                            landed = false // break or set true helper
                            break
                        }
                    }
                }
            }
        }

        // Reset special action actions on flat footing
        if (fighter.vy == 0f) {
            fighter.specialActionActive = false
        }
    }

    private fun executePinkPuffLandSlam(fighter: FighterState) {
        fighter.specialActionActive = false
        triggerSparkBurst(fighter.x, fighter.y + FIGHTER_RADIUS, Color.Magenta, 12)
        // Damage any opponent nearby on floor
        val opp = if (fighter.isCpu) player1 else player2
        if (abs(opp.x - fighter.x) < 70f && abs(opp.y - fighter.y) < 40f) {
            dealCombatBlow(
                attacker = fighter,
                defender = opp,
                damage = 20,
                baseKnockback = 12f,
                directionX = if (opp.x < fighter.x) -1f else 1f,
                directionY = -0.5f
            )
        }
    }

    private fun checkBlastZones(fighter: FighterState, opponent: FighterState) {
        if (fighter.isDead || fighter.isRespawning) return

        val bounds = selectedStage
        val isKo = fighter.x < bounds.blastZoneLeft ||
                fighter.x > bounds.blastZoneRight ||
                fighter.y < bounds.blastZoneTop ||
                fighter.y > bounds.blastZoneBottom

        if (isKo) {
            // Spectacular stock blast!
            fighter.stockCount--
            fighter.totalFalls++
            opponent.totalKOs++

            triggerBlastRing(fighter.x.coerceIn(20f, 780f), fighter.y.coerceIn(20f, 480f), fighter.selection.primaryColor)

            if (fighter.stockCount > 0) {
                fighter.respawnTimer = 80 // 1.3 seconds wait
                val spawnX = if (fighter.isCpu) selectedStage.spawnX2 else selectedStage.spawnX1
                val spawnY = if (fighter.isCpu) selectedStage.spawnY2 else selectedStage.spawnY1
                fighter.resetStatsForRespawn(spawnX, spawnY)
            } else {
                concludeMatch(winnerIsP1 = !fighter.isCpu)
            }
        }
    }

    private fun updateProjectiles() {
        val iterator = projectiles.iterator()
        while (iterator.hasNext()) {
            val proj = iterator.next()
            proj.lifeTicks--
            if (proj.lifeTicks <= 0) {
                iterator.remove()
                continue
            }

            // Move
            val oldY = proj.y
            proj.x += proj.vx
            proj.y += proj.vy

            // Bouncing Fireballs gravity
            if (proj.specialType == SpecialActionType.FIREBALL) {
                proj.vy += 0.3f
                // Platform Bounce check
                val main = selectedStage.mainPlatform
                if (oldY <= main.y && proj.y >= main.y) {
                    if (proj.x >= main.xMin && proj.x <= main.xMax) {
                        proj.y = main.y - 2f
                        proj.vy = -proj.vy * 0.85f // reverse & bounce
                        proj.bounceCount++
                    }
                }
                for (plat in selectedStage.floatingPlatforms) {
                    if (oldY <= plat.y && proj.y >= plat.y) {
                        if (proj.x >= plat.xMin && proj.x <= plat.xMax) {
                            proj.y = plat.y - 2f
                            proj.vy = -proj.vy * 0.85f
                            proj.bounceCount++
                        }
                    }
                }

                if (proj.bounceCount > 4) {
                    triggerSparkBurst(proj.x, proj.y, proj.color, 4)
                    iterator.remove()
                    continue
                }
            }

            // Bounds check
            if (proj.x < -50f || proj.x > 850f || proj.y < -50f || proj.y > 550f) {
                iterator.remove()
                continue
            }

            // Target collision
            val target = if (proj.isFromPlayer1) player2 else player1
            val attacker = if (proj.isFromPlayer1) player1 else player2

            if (checkFighterOverlap(proj.x, proj.y, proj.radius, target)) {
                // Collided with fighter!
                dealCombatBlow(
                    attacker = attacker,
                    defender = target,
                    damage = proj.damage,
                    baseKnockback = proj.knockbackPower,
                    directionX = sign(proj.vx).let { if (it == 0f) 1f else it },
                    directionY = -0.3f
                )
                triggerProjectileBloom(proj.x, proj.y, proj.color)
                iterator.remove()
            }
        }
    }

    private fun updateEffects() {
        val iterator = effects.iterator()
        while (iterator.hasNext()) {
            val fx = iterator.next()
            fx.ticksRemaining--
            if (fx.ticksRemaining <= 0) {
                iterator.remove()
                continue
            }
            // Move subtle drift
            fx.x += fx.vx
            fx.y += fx.vy
        }
    }

    // --- CPU PLAYING INTELLIGENCE STATE MACHINE ---
    private fun processCpuAI() {
        if (player2.isDead || player2.isRespawning || player1.isDead || player2.isHitStun) return

        val p = player1 // target
        val cpu = player2

        val rawDx = p.x - cpu.x
        val rawDy = p.y - cpu.y
        val dist = sqrt((rawDx * rawDx + rawDy * rawDy).toDouble()).toFloat()

        // 1. Off-stage Survival Steering (Priority #1)
        val stageRightX = selectedStage.mainPlatform.xMax
        val stageLeftX = selectedStage.mainPlatform.xMin
        val mainFloorY = selectedStage.mainPlatform.y

        val isOffStageLeft = cpu.x < stageLeftX - 15f
        val isOffStageRight = cpu.x > stageRightX + 15f
        val isBelowFloor = cpu.y > mainFloorY + 10f

        if (isOffStageLeft || isOffStageRight || isBelowFloor) {
            // Steer back urgently!
            val targetSpawnX = (stageLeftX + stageRightX) / 2
            if (cpu.x < targetSpawnX) {
                cpu.vx += cpu.selection.speed * 0.15f // urgent steer right
                cpu.isFacingLeft = false
            } else {
                cpu.vx -= cpu.selection.speed * 0.15f // urgent steer left
                cpu.isFacingLeft = true
            }

            // Jump recovery if falling below safety
            if (cpu.vy > 1.5f && cpu.jumpsRemaining > 0 && matchTicks % 25 == 0) {
                cpu.vy = -cpu.selection.jumpPower
                cpu.jumpsRemaining--
                triggerSparkBurst(cpu.x, cpu.y + FIGHTER_RADIUS, Color.White, 4)
            }
            return
        }

        // 2. Dynamic Chase / Engage Logic
        cpu.isCrouching = false
        val facingDir = if (cpu.isFacingLeft) -1f else 1f

        if (dist > 180f) {
            // Far chase player
            if (rawDx > 0) {
                cpu.vx += cpu.selection.speed * 0.11f
                cpu.isFacingLeft = false
            } else {
                cpu.vx -= cpu.selection.speed * 0.11f
                cpu.isFacingLeft = true
            }

            // Shoot Projectile when far away & ready (Spam and Space Control)
            if (cpu.specialCooldown == 0 && (cpu.selection.specialType == SpecialActionType.FIREBALL || cpu.selection.specialType == SpecialActionType.ARROW_SHOT) && matchTicks % 90 == 0) {
                executeSpecialMove(cpu, p, isP1 = false)
            }
        } else if (dist > 50f) {
            // Mid Range tactical approaching
            if (rawDx > 0) {
                cpu.vx += cpu.selection.speed * 0.10f
                cpu.isFacingLeft = false
            } else {
                cpu.vx -= cpu.selection.speed * 0.10f
                cpu.isFacingLeft = true
            }

            // Low frequency special moves (Sword dash, sparky thunder striking, shockwave)
            if (cpu.specialCooldown == 0 && matchTicks % 60 == 0) {
                executeSpecialMove(cpu, p, isP1 = false)
            }
        } else {
            // Close Range combat!
            cpu.isFacingLeft = rawDx < 0

            // 1. Attack chance
            if (cpu.attackCooldown == 0 && matchTicks % 12 == 0) {
                executeAttack(cpu, p, isP1 = false)
            }

            // 2. Shield defensive block chance (if player is attacking)
            if (p.attackCooldown == 14 && cpu.shieldHealth > 35f && matchTicks % 3 == 0) {
                cpu.isShieldActive = true
                cpu.vx *= 0.3f
            } else {
                // Decay shield block
                if (matchTicks % 18 == 0) {
                    cpu.isShieldActive = false
                }
            }
        }

        // 3. Platform Leap Navigation: jump up if player is higher on a platform
        if (rawDy < -80f && cpu.vy == 0f && matchTicks % 40 == 0) {
            cpu.vy = -cpu.selection.jumpPower
            cpu.jumpsRemaining--
        }
    }

    private fun checkFighterOverlap(x: Float, y: Float, radius: Float, target: FighterState): Boolean {
        if (target.isDead || target.isRespawning) return false
        val dx = x - target.x
        val dy = y - target.y
        val distance = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        return distance <= (radius + FIGHTER_RADIUS)
    }

    private fun concludeMatchByTime() {
        isMatchOver = true
        // Winner is who has more stocks, or lower damage if stocks are tied
        if (player1.stockCount > player2.stockCount) {
            victoryMessage = "MATCH OVER - PLAYER 1 VICTORIOUS!"
        } else if (player2.stockCount > player1.stockCount) {
            victoryMessage = "MATCH OVER - CPU VICTORIOUS!"
        } else {
            if (player1.damagePercentage < player2.damagePercentage) {
                victoryMessage = "TIE BREAKER - PLAYER 1 WINS ON LOWER DAMAGE!"
            } else if (player2.damagePercentage < player1.damagePercentage) {
                victoryMessage = "TIE BREAKER - CPU WINS ON LOWER DAMAGE!"
            } else {
                victoryMessage = "MATCH FINISHED IN A PERFECT TIED DRAW!"
            }
        }
    }

    private fun concludeMatch(winnerIsP1: Boolean) {
        isMatchOver = true
        victoryMessage = if (winnerIsP1) {
            "KO MATCH OVER! PLAYER 1 VICTORIOUS!"
        } else {
            "KO MATCH OVER! CPU TAKES THE TROPHY!"
        }
    }

    // --- PARTICLE / EFFECT BUILDERS ---
    private fun triggerDamagePop(x: Float, y: Float, text: String, color: Color) {
        effects.add(
            GameEffect(
                id = getNextId(),
                x = x,
                y = y,
                text = text,
                color = color,
                type = EffectType.DAMAGE_POP,
                totalTicks = 45,
                vy = -1.2f,
                vx = (Math.random() * 1.6 - 0.8).toFloat()
            )
        )
    }

    private fun triggerSmashRing(x: Float, y: Float, color: Color) {
        effects.add(
            GameEffect(
                id = getNextId(),
                x = x,
                y = y,
                text = "",
                color = color,
                type = EffectType.SMASH_RING,
                totalTicks = 20,
                sizeScale = 0.5f
            )
        )
    }

    private fun triggerBlastRing(x: Float, y: Float, color: Color) {
        effects.add(
            GameEffect(
                id = getNextId(),
                x = x,
                y = y,
                text = "KO!",
                color = Color.Red,
                type = EffectType.BLAST_RING,
                totalTicks = 45,
                sizeScale = 1.0f
            )
        )
        // Spawn sparks in all directions
        for (i in 0..11) {
            val angle = i * (Math.PI * 2 / 12)
            effects.add(
                GameEffect(
                    id = getNextId(),
                    x = x,
                    y = y,
                    text = "",
                    color = color,
                    type = EffectType.SPARK_BURST,
                    totalTicks = 35,
                    vx = (Math.cos(angle) * 7f).toFloat(),
                    vy = (Math.sin(angle) * 7f).toFloat()
                )
            )
        }
    }

    private fun triggerShieldPopEffect(x: Float, y: Float, color: Color) {
        effects.add(
            GameEffect(
                id = getNextId(),
                x = x,
                y = y,
                text = "SHIELD BROKEN!",
                color = Color.Yellow,
                type = EffectType.DAMAGE_POP,
                totalTicks = 60,
                vy = -1.5f
            )
        )
        effects.add(
            GameEffect(
                id = getNextId(),
                x = x,
                y = y,
                text = "",
                color = color,
                type = EffectType.SHIELD_RING,
                totalTicks = 30,
                sizeScale = 0.8f
            )
        )
    }

    private fun triggerSparkBurst(x: Float, y: Float, color: Color, count: Int) {
        for (i in 0 until count) {
            val vxScale = (Math.random() * 4 - 2).toFloat()
            val vyScale = (Math.random() * 4 - 2).toFloat()
            effects.add(
                GameEffect(
                    id = getNextId(),
                    x = x,
                    y = y,
                    text = "",
                    color = color,
                    type = EffectType.SPARK_BURST,
                    totalTicks = 15 + (Math.random() * 15).toInt(),
                    vx = vxScale,
                    vy = vyScale
                )
            )
        }
    }

    private fun triggerProjectileBloom(x: Float, y: Float, color: Color) {
        effects.add(
            GameEffect(
                id = getNextId(),
                x = x,
                y = y,
                text = "",
                color = color,
                type = EffectType.PROJECTILE_BLOOM,
                totalTicks = 15,
                sizeScale = 0.4f
            )
        )
        triggerSparkBurst(x, y, color, 4)
    }
}
