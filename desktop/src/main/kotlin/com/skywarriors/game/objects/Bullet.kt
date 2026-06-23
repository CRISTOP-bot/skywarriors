package com.skywarriors.game.objects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill

class Bullet : GameObject() {
    var isPlayerBullet: Boolean = true

    fun init(xPos: Float, yPos: Float, playerBullet: Boolean) {
        x = xPos
        y = yPos
        isPlayerBullet = playerBullet
        isActive = true
        width = 8f
        height = 12f
        velocityX = 0f
        velocityY = if (playerBullet) -600f else 300f
    }

    fun init(xPos: Float, yPos: Float, velX: Float, velY: Float, playerBullet: Boolean) {
        init(xPos, yPos, playerBullet)
        velocityX = velX
        velocityY = velY
    }

    override fun DrawScope.render() {
        if (!isActive) return
        val color = if (isPlayerBullet) Color(0xFF76FF03) else Color(0xFFFF1744)
        drawRoundRect(
            color = color,
            topLeft = Offset(x - width / 2, y - height / 2),
            size = Size(width, height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f),
            style = Fill
        )
    }
}
