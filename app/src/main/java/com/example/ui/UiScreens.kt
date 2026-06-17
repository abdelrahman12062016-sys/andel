package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.*

// The resource ID of our generated banner
// Note: Saved at /app/src/main/res/drawable/img_game_banner_1781709301801.jpg
val IMG_BANNER_RES = com.example.R.drawable.img_game_banner_1781709301801

@Composable
fun MainMenuScreen(
    recordsManager: BattleRecordsManager,
    onNavigateToFighterSelect: (isTraining: Boolean) -> Unit,
    onNavigateToRoster: () -> Unit,
    onNavigateToRecords: () -> Unit
) {
    val totalPlay = recordsManager.getTotalBattles()
    val totalWins = recordsManager.getPlayerWins()
    val totalKOs = recordsManager.getTotalKOsAllTime()
    val maxDmg = recordsManager.getMaxDamageDealt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8FD))
            .verticalScroll(rememberScrollState())
    ) {
        // Welcome Header mimicking the design HTML
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "WELCOME BACK",
                    color = Color(0xFF6750A4),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Smasher_01",
                    color = Color(0xFF1C1B1F),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
            }
            // Styled Avatar exactly as in design HTML
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEADDFF))
                    .border(2.dp, Color(0xFFD0BCFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6750A4))
                )
            }
        }

        // Hero Splash Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .height(260.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF6750A4), Color(0xFF4F378B))
                    )
                )
        ) {
            Image(
                painter = painterResource(id = IMG_BANNER_RES),
                contentDescription = "Smash Brawlers Battle Poster",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.24f)
            )

            // Dynamic layout matching design HTML
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(100.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "SEASON 4: MULTIVERSE",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "BRAWL\nREADY?",
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 36.sp,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Jump back into the arena of ultimate combat.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                    )
                }

                // CTA Quick Match trigger to match Tailwind button py-4 bg-[#D0BCFF] text-[#381E72] active:scale-95
                Button(
                    onClick = { onNavigateToFighterSelect(false) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD0BCFF),
                        contentColor = Color(0xFF381E72)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "QUICK MATCH",
                        color = Color(0xFF381E72),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Battle Records Dashboard (Mini Panel in bg-[#F3EDF7] rounded-[1.5rem] p-4 border-[#CAC4D0])
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFCAC4D0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatsCell("MATCHES", "$totalPlay")
                StatsDivider()
                StatsCell("WINS", "$totalWins")
                StatsDivider()
                StatsCell("TOTAL KOS", "$totalKOs")
                StatsDivider()
                StatsCell("MAX DMG", "$maxDmg%")
            }
        }

        // Daily Reward Promo Banner from Theme (bg-[#FFE082] rounded-2xl p-4 border-[#FFD54F])
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE082)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎁", fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "DAILY REWARD",
                            color = Color(0xFF4527A0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Collect 200 Gold Coins bonus",
                            color = Color(0xFF4527A0).copy(alpha = 0.73f),
                            fontSize = 10.sp
                        )
                    }
                }
                var rewardsClaimed by remember { mutableStateOf(false) }
                Button(
                    onClick = { rewardsClaimed = true },
                    enabled = !rewardsClaimed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4527A0),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF4527A0).copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(100.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = if (rewardsClaimed) "CLAIMED" else "CLAIM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Main Menu Button Nodes
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MenuNodeButton(
                title = "Live Combat Arena",
                subtitle = "Player vs CPU intense tactical battle",
                color = Color(0xFFEC407A),
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFEC407A)) },
                onClick = { onNavigateToFighterSelect(false) }
            )

            MenuNodeButton(
                title = "Training Ground",
                subtitle = "Infinite lives sandbox dummy practice",
                color = Color(0xFFFBC02D),
                icon = { Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFFFBC02D)) },
                onClick = { onNavigateToFighterSelect(true) }
            )

            MenuNodeButton(
                title = "Fighter Roster Guide",
                subtitle = "Stats, specials, and customized moves dockets",
                color = Color(0xFF42A5F5),
                icon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF42A5F5)) },
                onNavigateToRoster
            )

            MenuNodeButton(
                title = "Arena Logs & History",
                subtitle = "All-time statistics and match logs",
                color = Color(0xFF34D399),
                icon = { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF34D399)) },
                onNavigateToRecords
            )
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
fun StatsCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = Color(0xFF49454F),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            color = Color(0xFF1C1B1F),
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun StatsDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(28.dp)
            .background(Color(0xFFCAC4D0))
    )
}

@Composable
fun MenuNodeButton(
    title: String,
    subtitle: String,
    color: Color,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color(0xFFF3EDF7),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8DEF8)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color(0xFF1C1B1F),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF49454F),
                    fontSize = 11.sp
                )
            }
        }
    }
}

// --- FIGHTER CHARACTER SELECTION SCREEN ---
@Composable
fun CharacterSelectScreen(
    isTrainingMode: Boolean,
    onBack: () -> Unit,
    onNavigateToStageSelect: (player: FighterSelection, cpu: FighterSelection) -> Unit
) {
    var player1Selected by remember { mutableStateOf(FighterSelection.PLUMBER_JOE) }
    var player2Selected by remember { mutableStateOf(FighterSelection.SHADOW_HUNTER) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8FD))
            .padding(16.dp)
    ) {
        // Back toolbar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF6750A4)
                )
            }
            Text(
                text = if (isTrainingMode) "PICK COMBAT DUMMY" else "CHOOSE FIGHTERS",
                color = Color(0xFF1C1B1F),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(Modifier.height(12.dp))

        // Active dossier presentation
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFCAC4D0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            player1Selected.characterName,
                            color = player1Selected.primaryColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            player1Selected.title,
                            color = Color(0xFF49454F),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(player1Selected.primaryColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "PLAYER 1 HERO",
                            color = player1Selected.primaryColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Stats comparative view
                StatBarComparative("SPEED", player1Selected.speed, player2Selected.speed, max = 8f)
                StatBarComparative("WEIGHT", player1Selected.weight * 4.5f, player2Selected.weight * 4.5f, max = 8f)
                StatBarComparative("JUMP", player1Selected.jumpPower * 0.6f, player2Selected.jumpPower * 0.6f, max = 8f)
                StatBarComparative("POWER", player1Selected.attackPower * 0.45f, player2Selected.attackPower * 0.45f, max = 8f)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Fighters selector section
        Text(
            "SELECT PLAYER 1",
            color = Color(0xFF6750A4),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(FighterSelection.values()) { fighter ->
                val isP1 = player1Selected == fighter
                val isP2 = player2Selected == fighter

                Box(
                    modifier = Modifier
                        .height(85.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isP1) fighter.primaryColor
                            else if (isP2) Color(0xFFEADDFF)
                            else Color(0xFFF3EDF7)
                        )
                        .border(
                            2.dp,
                            if (isP1) Color(0xFF6750A4) else if (isP2) Color(0xFF6750A4).copy(alpha = 0.5f) else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { player1Selected = fighter }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isP1) Color.White else fighter.primaryColor)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            fighter.characterName,
                            color = if (isP1) Color.White else Color(0xFF1C1B1F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        if (isP2) {
                            Text(
                                "CPU",
                                color = Color(0xFF6750A4),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Select Player 2 / CPU Sector
        Text(
            if (isTrainingMode) "SELECT TRAINING DUMMY" else "SELECT CPU OPPONENT",
            color = Color(0xFF6750A4),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FighterSelection.values().forEach { fighter ->
                val isP2 = player2Selected == fighter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isP2) fighter.primaryColor else Color(0xFFF3EDF7))
                        .border(1.dp, if (isP2) Color.Transparent else Color(0xFFCAC4D0), RoundedCornerShape(12.dp))
                        .clickable { player2Selected = fighter }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        fighter.characterName,
                        color = if (isP2) Color.White else Color(0xFF1C1B1F),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { onNavigateToStageSelect(player1Selected, player2Selected) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4), contentColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                "SELECT STAGE ▶",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun StatBarComparative(label: String, val1: Float, val2: Float, max: Float) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color(0xFF49454F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(
                "P1: ${(val1 / max * 10).toInt()} vs CPU: ${(val2 / max * 10).toInt()}",
                color = Color(0xFF1C1B1F),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(2.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            LinearProgressIndicator(
                progress = { (val1 / max).coerceIn(0f, 1f) },
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(CircleShape),
                color = Color(0xFF6750A4),
                trackColor = Color(0xFFE8DEF8)
            )
        }
    }
}

// --- STAGE SELECTION SCREEN ---
@Composable
fun StageSelectScreen(
    onBack: () -> Unit,
    player: FighterSelection,
    cpu: FighterSelection,
    onReady: (StageSelection) -> Unit
) {
    var stageSelected by remember { mutableStateOf(StageSelection.BATTLEFIELD_PEAKS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8FD))
            .padding(16.dp)
    ) {
        // Back toolbar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF6750A4)
                )
            }
            Text(
                "CHOOSE THE BATTLEFIELD",
                color = Color(0xFF1C1B1F),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(Modifier.height(16.dp))

        // Display selected stage details card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, stageSelected.secondaryColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    stageSelected.displayName,
                    color = Color(0xFF1C1B1F),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    stageSelected.subtitle,
                    color = stageSelected.secondaryColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    stageSelected.description,
                    color = Color(0xFF49454F),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Stage row cards list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(StageSelection.values()) { stage ->
                val isSelected = stageSelected == stage

                Surface(
                    onClick = { stageSelected = stage },
                    color = if (isSelected) stage.primaryColor else Color(0xFFF3EDF7),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) stage.secondaryColor else Color(0xFFCAC4D0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(stage.secondaryColor)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                stage.displayName,
                                color = if (isSelected) Color.White else Color(0xFF1C1B1F),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Auxiliary platform items: ${if (stage.floatingPlatforms.isNotEmpty()) stage.floatingPlatforms.size else "No"} shelves",
                                color = if (isSelected) Color.White.copy(alpha = 0.75f) else Color(0xFF49454F),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { onReady(stageSelected) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4), contentColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                "ENGAGE FIGHT ▶",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
    }
}

// --- FIGHTERS ROSTER GUIDE ---
@Composable
fun RosterScreen(onBack: () -> Unit) {
    var inspectorFighter by remember { mutableStateOf(FighterSelection.PLUMBER_JOE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8FD))
            .padding(16.dp)
    ) {
        // Back toolbar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF6750A4)
                )
            }
            Text(
                "FIGHTER COMPENDIUM",
                color = Color(0xFF1C1B1F),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(Modifier.height(14.dp))

        // Side-by-side Layout: Left is grid of fighters, Right is detailed dossier sheet
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left list grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1.1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(FighterSelection.values()) { fighter ->
                    val isSelected = investigatorMatch(fighter, inspectorFighter)

                    Box(
                        modifier = Modifier
                            .height(115.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) fighter.primaryColor else Color(0xFFF3EDF7))
                            .border(
                                2.dp,
                                if (isSelected) Color(0xFF6750A4) else Color(0xFFCAC4D0),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { inspectorFighter = fighter }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White else fighter.primaryColor)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                fighter.characterName,
                                color = if (isSelected) Color.White else Color(0xFF1C1B1F),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Class info",
                                color = if (isSelected) Color.White.copy(alpha = 0.75f) else Color(0xFF49454F),
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            // Right dossier container
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(2.dp, inspectorFighter.primaryColor)
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = inspectorFighter.characterName,
                        color = Color(0xFF1C1B1F),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = inspectorFighter.title,
                        color = inspectorFighter.primaryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = inspectorFighter.description,
                        color = Color(0xFF49454F),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(Modifier.height(14.dp))

                    // Move set listings
                    MoveLine("Normal Strike: A", inspectorFighter.normalMoveName)
                    MoveLine("Special Smash: B", inspectorFighter.specialMoveName)

                    Spacer(Modifier.height(12.dp))

                    // Stats lines
                    DossierStatLine("Speed Index", "${(inspectorFighter.speed * 1.2f).toInt()}/10")
                    DossierStatLine("Weight Grade", "${(inspectorFighter.weight * 5.5f).toInt()}/10")
                    DossierStatLine("Launch resistance", if (inspectorFighter.weight >= 1.2f) "EXCELLENT" else if (inspectorFighter.weight >= 0.95f) "MEDIUM" else "SENSITIVE")
                }
            }
        }
    }
}

private fun investigatorMatch(a: FighterSelection, b: FighterSelection): Boolean = a == b

@Composable
fun MoveLine(type: String, name: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(type, color = Color(0xFF6750A4), fontWeight = FontWeight.Bold, fontSize = 10.sp)
        Text(name, color = Color(0xFF1C1B1F), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}

@Composable
fun DossierStatLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF49454F), fontSize = 10.sp)
        Text(value, color = Color(0xFF1C1B1F), fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}

// --- SCORE RECORDS HISTORY SCREEN ---
@Composable
fun RecordsScreen(recordsManager: BattleRecordsManager, onBack: () -> Unit) {
    var historyList by remember { mutableStateOf(recordsManager.getHistoryList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8FD))
            .padding(16.dp)
    ) {
        // Back toolbar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF6750A4)
                    )
                }
                Text(
                    "COMBAT LOGS HISTORY",
                    color = Color(0xFF1C1B1F),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Clear statistics
            IconButton(
                onClick = {
                    recordsManager.clearHistory()
                    historyList = emptyList()
                }
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Clear All Records", tint = Color(0xFFC62828))
            }
        }

        Spacer(Modifier.height(12.dp))

        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillSome()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "NO BATTLE LOGS YET",
                        color = Color(0xFF49454F).copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Engage in Arena Mode battles to log data records.",
                        color = Color(0xFF49454F).copy(alpha = 0.35f),
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(historyList) { entry ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFCAC4D0))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    entry.dateString,
                                    color = Color(0xFF49454F),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                               )

                                Text(
                                    text = "WINNER: ${entry.winner}",
                                    color = if (entry.winner == "PLAYER 1") Color(0xFF2E7D32) else Color(0xFFC62828),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${entry.playerCharacter} vs ${entry.cpuCharacter}",
                                    color = Color(0xFF1C1B1F),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Stage: ${entry.stageName}",
                                    color = Color(0xFF49454F),
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MiniStatsDose("Total KOs", "${entry.playerKOs}")
                                MiniStatsDose("Total Falls", "${entry.playerFalls}")
                                MiniStatsDose("Damage Smashed", "${entry.playerDamageDealt}%")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Typo correction: Modifier extension or local helper for empty fillers
private fun Modifier.fillSome(): Modifier = this.fillMaxWidth().fillMaxHeight()

@Composable
fun MiniStatsDose(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color(0xFF49454F), fontSize = 10.sp)
        Text(value, color = Color(0xFF1C1B1F), fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
