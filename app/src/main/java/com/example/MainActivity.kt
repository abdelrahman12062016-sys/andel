package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.game.*
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

enum class Screen {
  MAIN_MENU,
  FIGHTER_SELECT,
  STAGE_SELECT,
  BRAWLER_PLAY,
  ROSTER,
  RECORDS
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val recordsManager = BattleRecordsManager(applicationContext)

    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = Color(0xFFFDF8FD)
        ) {
          var currentScreen by remember { mutableStateOf(Screen.MAIN_MENU) }
          var isTrainingMode by remember { mutableStateOf(false) }
          var selectedP1 by remember { mutableStateOf(FighterSelection.PLUMBER_JOE) }
          var selectedCpu by remember { mutableStateOf(FighterSelection.SHADOW_HUNTER) }
          var selectedStage by remember { mutableStateOf(StageSelection.BATTLEFIELD_PEAKS) }

          when (currentScreen) {
            Screen.MAIN_MENU -> {
              MainMenuScreen(
                recordsManager = recordsManager,
                onNavigateToFighterSelect = { isTraining ->
                  isTrainingMode = isTraining
                  currentScreen = Screen.FIGHTER_SELECT
                },
                onNavigateToRoster = { currentScreen = Screen.ROSTER },
                onNavigateToRecords = { currentScreen = Screen.RECORDS }
              )
            }
            Screen.FIGHTER_SELECT -> {
              CharacterSelectScreen(
                isTrainingMode = isTrainingMode,
                onBack = { currentScreen = Screen.MAIN_MENU },
                onNavigateToStageSelect = { p1, cpu ->
                  selectedP1 = p1
                  selectedCpu = cpu
                  currentScreen = Screen.STAGE_SELECT
                }
              )
            }
            Screen.STAGE_SELECT -> {
              StageSelectScreen(
                onBack = { currentScreen = Screen.FIGHTER_SELECT },
                player = selectedP1,
                cpu = selectedCpu,
                onReady = { stage ->
                  selectedStage = stage
                  currentScreen = Screen.BRAWLER_PLAY
                }
              )
            }
            Screen.BRAWLER_PLAY -> {
              GamePlayScreen(
                selectedPlayer = selectedP1,
                selectedCpu = selectedCpu,
                selectedStage = selectedStage,
                isTrainingMode = isTrainingMode,
                recordsManager = recordsManager,
                onBackToMenu = { currentScreen = Screen.MAIN_MENU }
              )
            }
            Screen.ROSTER -> {
              RosterScreen(onBack = { currentScreen = Screen.MAIN_MENU })
            }
            Screen.RECORDS -> {
              RecordsScreen(
                recordsManager = recordsManager,
                onBack = { currentScreen = Screen.MAIN_MENU }
              )
            }
          }
        }
      }
    }
  }
}

