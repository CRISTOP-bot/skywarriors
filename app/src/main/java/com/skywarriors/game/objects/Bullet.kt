package com.skywarriors.game.objects

import android.graphics.Canvas
import android.graphics.Paint
import com.skywarriors.util.Constants

class Bullet(
    startX: Float,
    startY: Float,
    val isPlayerBullet: Boolean
) : GameObject() {

    init {
        x = startX
        y = startY
        width = Constants.BULLET_SIZE
        height = Constants.BULLET_SIZE * 2.5f
        velocityY = if (isPlayerBullet) -Constants.BULLET_SPEED else Constants.ENEMY_BULLET_SPEED
    }

    private val paint = Paint().apply {
        color = if (isPlayerBullet)
            android.graphics.Color.rgb(118, 255, 3)
        else
            android.graphics.Color.rgb(255, 23, 68)
        isAntiAlias = true
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
    }

    override fun render(canvas: Canvas) {
        canvas.save()
        canvas.translate(x, y)
        val halfW = width / 2
        val halfH = height / 2
        canvas.drawRoundRect(-halfW, -halfH, halfW, halfH, 3f, 3f, paint)
        canvas.restore()
    }
}
