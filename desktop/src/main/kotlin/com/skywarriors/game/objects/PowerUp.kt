package com.skywarriors.game.objects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.skywarriors.util.Constants

class PowerUp : GameObject() {
    enum class PowerUpType {
        SHIELD, RAPID_FIRE, SPREAD_SHOT, EXTRA_LIFE
    }

    var powerUpType: PowerUpType = PowerUpType.SHIELD

    fun init(xPos: Float, yPos: Float, type: PowerUpType) {
        x = xPos
        y = yPos
        powerUpType = type
        isActive = true
        width = Constants.POWERUP_SIZE.toFloat()
        height = Constants.POWERUP_SIZE.toFloat()
        velocityX = 0f
        velocityY = Constants.POWERUP_SPEED.toFloat()
    }

    override fun DrawScope.render() {
        if (!isActive) return

        val (color, label) = when (powerUpType) {
            PowerUpType.SHIELD -> Color(0xFF448AFF) to "S"
            PowerUpType.RAPID_FIRE -> Color(0xFFFF6D00) to "R"
            PowerUpType.SPREAD_SHOT -> Color(0xFFD500F9) to "P"
            PowerUpType.EXTRA_LIFE -> Color(0xFFFF1744) to "+1"
        }

        drawRoundRect(
            color = color,
            topLeft = Offset(x - width / 2, y - height / 2),
            size = Size(width, height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f),
            style = Fill
        )
    }
}
