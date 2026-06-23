package com.skywarriors.game.objects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.skywarriors.util.Constants
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Explosion {
    data class Particle(
        var x: Float, var y: Float,
        var velocityX: Float, var velocityY: Float,
        var lifetime: Long, var maxLifetime: Long,
        var size: Float, var color: Color
    )

    private val particles = mutableListOf<Particle>()
    var isActive: Boolean = true

    fun init(x: Float, y: Float, sizeMultiplier: Float = 1f) {
        val count = (Constants.EXPLOSION_PARTICLES * sizeMultiplier).toInt()
        val colors = listOf(
            Color(0xFFFF6D00),
            Color(0xFFFFAB00),
            Color(0xFFFFD600),
            Color.White,
            Color(0xFFFF1744)
        )

        for (i in 0 until count) {
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2
            val speed = Random.nextFloat() * Constants.EXPLOSION_SPEED.toFloat()
            val lifetime = (Constants.EXPLOSION_LIFETIME * (0.5f + Random.nextFloat() * 0.5f)).toLong()
            val size = (2f + Random.nextFloat() * 4f) * sizeMultiplier

            particles.add(
                Particle(
                    x = x + Random.nextFloat() * 20f - 10f,
                    y = y + Random.nextFloat() * 20f - 10f,
                    velocityX = cos(angle) * speed,
                    velocityY = sin(angle) * speed,
                    lifetime = lifetime,
                    maxLifetime = lifetime,
                    size = size,
                    color = colors[Random.nextInt(colors.size)]
                )
            )
        }
        isActive = true
    }

    fun update(dt: Float) {
        val dtMillis = (dt * 1000).toLong()
        val toRemove = mutableListOf<Particle>()

        for (p in particles) {
            p.lifetime -= dtMillis
            if (p.lifetime <= 0) {
                toRemove.add(p)
                continue
            }
            p.x += p.velocityX * dt
            p.y += p.velocityY * dt
            p.velocityX *= 0.95f
            p.velocityY *= 0.95f
            p.size *= 0.98f
        }

        particles.removeAll(toRemove)
        if (particles.isEmpty()) {
            isActive = false
        }
    }

    fun DrawScope.render() {
        for (p in particles) {
            val alpha = (p.lifetime.toFloat() / p.maxLifetime.toFloat()).coerceIn(0f, 1f)
            drawCircle(
                color = p.color,
                radius = maxOf(p.size, 0.5f),
                center = Offset(p.x, p.y),
                alpha = alpha,
                style = Fill
            )
        }
    }

    fun reset() {
        particles.clear()
        isActive = false
    }
}
