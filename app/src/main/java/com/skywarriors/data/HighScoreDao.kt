package com.skywarriors.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HighScoreDao {

    @Query("SELECT * FROM high_scores ORDER BY score DESC LIMIT :limit")
    suspend fun getTopScores(limit: Int = 10): List<HighScore>

    @Query("SELECT COALESCE(MAX(score), 0) FROM high_scores")
    suspend fun getHighestScore(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(highScore: HighScore)

    @Query("SELECT COUNT(*) FROM high_scores WHERE score > :score")
    suspend fun getRank(score: Int): Int
}
