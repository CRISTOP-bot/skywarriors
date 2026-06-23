package com.skywarriors.game.objects

import android.graphics.Canvas
import android.graphics.Paint
import com.skywarriors.util.Constants
import kotlin.random.Random

class Explosion(
    centerX: Float,
    centerY: Float,
    size: Float = 1f
) {
    data class Particle(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var life: Long,
        val maxLife: Long,
        val size: Float,
        val color: Int
    )

    private val particles = mutableListOf<Particle>()
    var isActive: Boolean = true
    private val paint = Paint().apply { isAntiAlias = true }

    init {
        val count = (Constants.PARTICLE_COUNT * size).toInt()
        val baseColors = listOf(
            android.graphics.Color.rgb(255, 109, 0),
            android.graphics.Color.rgb(255, 171, 64),
            android.graphics.Color.rgb(255, 235, 59),
            android.graphics.Color.rgb(255, 255, 255),
            android.graphics.Color.rgb(244, 67, 54)
        )

        repeat(count) {
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2
            val speed = Random.nextFloat() * Constants.PARTICLE_SPEED * (1f + size * 0.5f)
            particles.add(
                Particle(
                    x = centerX + Random.nextFloat() * 20f - 10f,
                    y = centerY + Random.nextFloat() * 20f - 10f,
                    vx = kotlin.math.cos(angle.toDouble()).toFloat() * speed,
                    vy = kotlin.math.sin(angle.toDouble()).toFloat() * speed,
                    life = (Constants.PARTICLE_LIFETIME * (0.5f + Random.nextFloat() * 0.5f)).toLong(),
                    maxLife = (Constants.PARTICLE_LIFETIME * (0.5f + Random.nextFloat() * 0.5f)).toLong(),
                    size = Random.nextFloat() * 6f * size + 2f,
                    color = baseColors[Random.nextInt(baseColors.size)]
                )
            )
        }
    }

    fun update(deltaTime: Long) {
        var alive = false
        for (p in particles) {
            if (p.life <= 0) continue
            p.life -= deltaTime
            if (p.life <= 0) continue
            alive = true
            p.x += p.vx * deltaTime / 1000f
            p.y += p.vy * deltaTime / 1000f
            p.vx *= 0.95f
            p.vy *= 0.95f
        }
        if (!alive) isActive = false
    }

    fun render(canvas: Canvas) {
        for (p in particles) {
            if (p.life <= 0) continue
            val alpha = (p.life.toFloat() / p.maxLife * 255).toInt().coerceIn(0, 255)
            paint.color = p.color
            paint.alpha = alpha
            canvas.drawCircle(p.x, p.y, p.size * (p.life.toFloat() / p.maxLife), paint)
        }
    }
}
