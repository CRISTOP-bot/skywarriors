package com.skywarriors.game.objects

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import com.skywarriors.util.Constants

enum class PowerUpType {
    SHIELD, RAPID_FIRE, SPREAD_SHOT, EXTRA_LIFE
}

class PowerUp(type: PowerUpType) : GameObject() {

    val powerType: PowerUpType = type
    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = 24f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }
    private val bgPaint = Paint().apply {
        isAntiAlias = true
    }

    init {
        width = Constants.POWERUP_SIZE
        height = Constants.POWERUP_SIZE
        velocityY = Constants.POWERUP_SPEED

        when (type) {
            PowerUpType.SHIELD -> {
                bgPaint.color = android.graphics.Color.rgb(68, 138, 255)
                paint.color = android.graphics.Color.WHITE
            }
            PowerUpType.RAPID_FIRE -> {
                bgPaint.color = android.graphics.Color.rgb(255, 109, 0)
                paint.color = android.graphics.Color.WHITE
            }
            PowerUpType.SPREAD_SHOT -> {
                bgPaint.color = android.graphics.Color.rgb(213, 0, 249)
                paint.color = android.graphics.Color.WHITE
            }
            PowerUpType.EXTRA_LIFE -> {
                bgPaint.color = android.graphics.Color.rgb(255, 23, 68)
                paint.color = android.graphics.Color.WHITE
            }
        }
    }

    fun getLabel(): String = when (powerType) {
        PowerUpType.SHIELD -> "S"
        PowerUpType.RAPID_FIRE -> "R"
        PowerUpType.SPREAD_SHOT -> "P"
        PowerUpType.EXTRA_LIFE -> "+1"
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
    }

    override fun render(canvas: Canvas) {
        canvas.save()
        canvas.translate(x, y)
        val halfW = width / 2
        val halfH = height / 2

        canvas.drawRoundRect(-halfW, -halfH, halfW, halfH, 8f, 8f, bgPaint)
        canvas.drawText(getLabel(), 0f, halfH * 0.4f, paint)
        canvas.restore()
    }
}
