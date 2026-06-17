package com.example.game

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class BattleHistoryEntry(
    val id: String,
    val dateString: String,
    val playerCharacter: String,
    val cpuCharacter: String,
    val stageName: String,
    val matchDurationSeconds: Int,
    val winner: String,
    val playerKOs: Int,
    val playerFalls: Int,
    val playerDamageDealt: Int
)

class BattleRecordsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("smash_brawlers_prefs", Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val historyAdapter = moshi.adapter<List<BattleHistoryEntry>>(
        Types.newParameterizedType(List::class.java, BattleHistoryEntry::class.java)
    )

    fun getHistoryList(): List<BattleHistoryEntry> {
        val json = prefs.getString("battle_history", null) ?: return emptyList()
        return try {
            historyAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveHistoryList(list: List<BattleHistoryEntry>) {
        val json = historyAdapter.toJson(list)
        prefs.edit().putString("battle_history", json).apply()
    }

    fun addBattleRecord(
        playerFighter: String,
        cpuFighter: String,
        stage: String,
        duration: Int,
        winner: String,
        kos: Int,
        falls: Int,
        damageDealt: Int
    ) {
        val currentHistory = getHistoryList().toMutableList()
        val formatter = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
        val dateString = formatter.format(java.util.Date())

        val entry = BattleHistoryEntry(
            id = java.util.UUID.randomUUID().toString(),
            dateString = dateString,
            playerCharacter = playerFighter,
            cpuCharacter = cpuFighter,
            stageName = stage,
            matchDurationSeconds = duration,
            winner = winner,
            playerKOs = kos,
            playerFalls = falls,
            playerDamageDealt = damageDealt
        )

        currentHistory.add(0, entry) // Add at start
        saveHistoryList(currentHistory.take(20)) // Limit to last 20

        // Update overall stats
        val totalBattles = prefs.getInt("total_battles", 0) + 1
        val playerWins = prefs.getInt("player_wins", 0) + (if (winner == "PLAYER 1") 1 else 0)
        val maxDamage = prefs.getInt("max_damage_dealt", 0).coerceAtLeast(damageDealt)
        val totalKOs = prefs.getInt("total_kos_all_time", 0) + kos

        prefs.edit()
            .putInt("total_battles", totalBattles)
            .putInt("player_wins", playerWins)
            .putInt("max_damage_dealt", maxDamage)
            .putInt("total_kos_all_time", totalKOs)
            .apply()
    }

    fun getTotalBattles(): Int = prefs.getInt("total_battles", 0)
    fun getPlayerWins(): Int = prefs.getInt("player_wins", 0)
    fun getMaxDamageDealt(): Int = prefs.getInt("max_damage_dealt", 0)
    fun getTotalKOsAllTime(): Int = prefs.getInt("total_kos_all_time", 0)

    fun clearHistory() {
        prefs.edit().clear().apply()
    }
}
