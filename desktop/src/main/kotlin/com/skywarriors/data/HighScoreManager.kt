package com.skywarriors.data

import java.io.File
import java.nio.file.Paths

data class HighScoreEntry(
    val name: String,
    val score: Int,
    val date: Long = System.currentTimeMillis()
)

class HighScoreManager {
    private var scores = mutableListOf<HighScoreEntry>()
    private val maxScores = 10
    private val dataDir: File
    private val dataFile: File

    init {
        val userHome = System.getProperty("user.home")
        dataDir = Paths.get(userHome, ".skywarriors").toFile()
        dataFile = File(dataDir, "highscores.json")
        dataDir.mkdirs()
        loadScores()
    }

    fun addScore(name: String, score: Int) {
        if (score <= 0) return
        scores.add(HighScoreEntry(name, score, System.currentTimeMillis()))
        scores.sortByDescending { it.score }
        if (scores.size > maxScores) {
            scores = scores.take(maxScores).toMutableList()
        }
        saveScores()
    }

    fun getTopScores(): List<HighScoreEntry> = scores.toList()

    fun getHighestScore(): Int = scores.firstOrNull()?.score ?: 0

    fun isNewHighScore(score: Int): Boolean {
        return score > 0 && (scores.size < maxScores || score > (scores.lastOrNull()?.score ?: 0))
    }

    private fun loadScores() {
        if (!dataFile.exists()) return
        try {
            val json = dataFile.readText()
            val entries = json.split("\n").filter { it.isNotBlank() }
            for (line in entries) {
                val parts = line.split("|")
                if (parts.size >= 2) {
                    scores.add(
                        HighScoreEntry(
                            name = parts[0],
                            score = parts[1].toIntOrNull() ?: 0,
                            date = if (parts.size >= 3) parts[2].toLongOrNull() ?: System.currentTimeMillis()
                            else System.currentTimeMillis()
                        )
                    )
                }
            }
            scores.sortByDescending { it.score }
        } catch (_: Exception) {}
    }

    private fun saveScores() {
        try {
            val sb = StringBuilder()
            for (entry in scores) {
                sb.appendLine("${entry.name}|${entry.score}|${entry.date}")
            }
            dataFile.writeText(sb.toString())
        } catch (_: Exception) {}
    }
}
