package com.skywarriors.game.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import com.skywarriors.game.engine.GameState
import com.skywarriors.game.objects.Player
import com.skywarriors.game.objects.Enemy
import com.skywarriors.util.Constants

class HUD {
    private val scorePaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 36f
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }
    private val labelPaint = Paint().apply {
        color = android.graphics.Color.argb(180, 255, 255, 255)
        textSize = 22f
        isAntiAlias = true
    }
    private val titlePaint = Paint().apply {
        color = android.graphics.Color.rgb(0, 229, 255)
        textSize = 72f
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }
    private val subtitlePaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 32f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }
    private val gameOverPaint = Paint().apply {
        color = android.graphics.Color.rgb(255, 50, 50)
        textSize = 80f
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }
    private val powerUpPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 18f
        isAntiAlias = true
    }
    private val healthBarBgPaint = Paint().apply {
        color = android.graphics.Color.argb(100, 255, 0, 0)
    }
    private val healthBarPaint = Paint().apply {
        color = android.graphics.Color.rgb(255, 0, 0)
    }

    private var blinkTimer: Long = 0

    fun renderPlaying(canvas: Canvas, player: Player, boss: Enemy?) {
        renderScore(canvas, player)
        renderLives(canvas, player)
        renderPowerUps(canvas, player)
        if (boss != null) renderBossHealth(canvas, boss)
    }

    private fun renderScore(canvas: Canvas, player: Player) {
        canvas.drawText("SCORE", 20f, 40f, labelPaint)
        canvas.drawText("${player.score}", 20f, 75f, scorePaint)
    }

    private fun renderLives(canvas: Canvas, player: Player) {
        val startX = (Player.screenWidth ?: 1080f) - 30f
        for (i in 0 until player.lives) {
            val x = startX - i * 35f
            val lifePaint = Paint().apply {
                color = android.graphics.Color.rgb(0, 229, 255)
                isAntiAlias = true
            }
            canvas.drawCircle(x, 40f, 10f, lifePaint)
        }
        canvas.drawText("LIVES", startX - (player.lives - 1) * 35f - 50f, 40f, labelPaint)
    }

    private fun renderPowerUps(canvas: Canvas, player: Player) {
        var y = 110f
        if (player.shieldActive) {
            powerUpPaint.color = android.graphics.Color.rgb(68, 138, 255)
            canvas.drawText("SHIELD", 20f, y, powerUpPaint)
            y += 25f
        }
        if (player.rapidFireActive) {
            powerUpPaint.color = android.graphics.Color.rgb(255, 109, 0)
            canvas.drawText("RAPID FIRE", 20f, y, powerUpPaint)
            y += 25f
        }
        if (player.spreadShotActive) {
            powerUpPaint.color = android.graphics.Color.rgb(213, 0, 249)
            canvas.drawText("SPREAD SHOT", 20f, y, powerUpPaint)
        }
    }

    private fun renderBossHealth(canvas: Canvas, boss: Enemy) {
        val screenWidth = Player.screenWidth ?: 1080f
        val barWidth = screenWidth * 0.6f
        val barHeight = 12f
        val barX = (screenWidth - barWidth) / 2
        val barY = 20f
        val healthPct = boss.getHealthPercent()

        canvas.drawRoundRect(barX, barY, barX + barWidth, barY + barHeight, 6f, 6f, healthBarBgPaint)
        if (healthPct > 0) {
            healthBarPaint.color = when {
                healthPct > 0.5f -> android.graphics.Color.rgb(76, 175, 80)
                healthPct > 0.25f -> android.graphics.Color.rgb(255, 193, 7)
                else -> android.graphics.Color.rgb(244, 67, 54)
            }
            canvas.drawRoundRect(barX, barY, barX + barWidth * healthPct, barY + barHeight, 6f, 6f, healthBarPaint)
        }
        val bossLabelPaint = Paint().apply {
            color = android.graphics.Color.rgb(255, 0, 0)
            textSize = 16f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        canvas.drawText("BOSS", screenWidth / 2, barY + barHeight + 18f, bossLabelPaint)
    }

    fun renderMenu(canvas: Canvas) {
        val screenWidth = Player.screenWidth ?: 1080f
        val screenHeight = Player.screenHeight ?: 1920f
        val cx = screenWidth / 2
        val cy = screenHeight / 2

        canvas.drawText("SKY", cx, cy - 120f, titlePaint)
        canvas.drawText("WARRIORS", cx, cy - 50f, titlePaint)

        blinkTimer += 16
        if ((blinkTimer / 500) % 2 == 0L) {
            subtitlePaint.alpha = 255
        } else {
            subtitlePaint.alpha = 100
        }
        canvas.drawText("TAP TO FLY", cx, cy + 100f, subtitlePaint)

        val controlsPaint = Paint().apply {
            color = android.graphics.Color.argb(150, 200, 200, 200)
            textSize = 20f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Touch and drag to move your plane", cx, cy + 200f, controlsPaint)
        canvas.drawText("Destroy enemies and collect power-ups!", cx, cy + 230f, controlsPaint)
    }

    fun renderGameOver(canvas: Canvas, score: Int, highScore: Int, isNewHighScore: Boolean) {
        val screenWidth = Player.screenWidth ?: 1080f
        val screenHeight = Player.screenHeight ?: 1920f
        val cx = screenWidth / 2
        val cy = screenHeight / 2

        canvas.drawText("GAME OVER", cx, cy - 150f, gameOverPaint)

        scorePaint.textAlign = Paint.Align.CENTER
        canvas.drawText("SCORE: $score", cx, cy - 40f, scorePaint)

        if (isNewHighScore) {
            val newHsPaint = Paint().apply {
                color = android.graphics.Color.rgb(255, 215, 0)
                textSize = 36f
                isAntiAlias = true
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
            }
            blinkTimer += 16
            if ((blinkTimer / 400) % 2 == 0L) {
                canvas.drawText("NEW HIGH SCORE!", cx, cy + 20f, newHsPaint)
            }
        }

        canvas.drawText("BEST: $highScore", cx, cy + 80f, subtitlePaint)

        blinkTimer += 16
        if ((blinkTimer / 500) % 2 == 0L) {
            subtitlePaint.alpha = 255
        } else {
            subtitlePaint.alpha = 100
        }
        canvas.drawText("TAP TO RETRY", cx, cy + 180f, subtitlePaint)
    }

    fun renderPaused(canvas: Canvas) {
        val cx = (Player.screenWidth ?: 1080f) / 2
        val cy = (Player.screenHeight ?: 1920f) / 2

        canvas.drawText("PAUSED", cx, cy - 50f, titlePaint)
        subtitlePaint.alpha = 200
        canvas.drawText("TAP TO RESUME", cx, cy + 50f, subtitlePaint)
    }
}
