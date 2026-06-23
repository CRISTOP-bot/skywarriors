package com.skywarriors.game.objects

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.skywarriors.util.Constants
import kotlin.math.cos
import kotlin.math.sin

class Enemy : GameObject() {
    enum class EnemyType {
        BASIC, FAST, TANK, BOSS
    }

    var type: EnemyType = EnemyType.BASIC
    var health: Int = 1
    var maxHealth: Int = 1
    var canShoot: Boolean = false
    var shootTimer: Long = 0L
    private var startX: Float = 0f
    private var time: Float = 0f

    fun init(enemyType: EnemyType, xPos: Float, yPos: Float) {
        type = enemyType
        x = xPos
        startX = xPos
        y = yPos
        time = 0f

        when (type) {
            EnemyType.BASIC -> {
                width = Constants.ENEMY_BASIC_SIZE.toFloat()
                height = Constants.ENEMY_BASIC_SIZE.toFloat()
                velocityY = Constants.ENEMY_BASIC_SPEED.toFloat()
                health = Constants.ENEMY_BASIC_HP
                canShoot = false
            }
            EnemyType.FAST -> {
                width = Constants.ENEMY_FAST_SIZE.toFloat()
                height = Constants.ENEMY_FAST_SIZE.toFloat()
                velocityY = Constants.ENEMY_FAST_SPEED.toFloat()
                health = Constants.ENEMY_FAST_HP
                canShoot = false
            }
            EnemyType.TANK -> {
                width = Constants.ENEMY_TANK_SIZE.toFloat()
                height = Constants.ENEMY_TANK_SIZE.toFloat()
                velocityY = Constants.ENEMY_TANK_SPEED.toFloat()
                health = Constants.ENEMY_TANK_HP
                canShoot = true
                shootTimer = 1500L
            }
            EnemyType.BOSS -> {
                width = Constants.BOSS_SIZE.toFloat()
                height = Constants.BOSS_SIZE.toFloat()
                velocityY = 0f
                velocityX = Constants.BOSS_SPEED.toFloat()
                health = Constants.BOSS_HP
                canShoot = true
                shootTimer = 1000L
            }
        }
        maxHealth = health
        isActive = true
    }

    fun takeDamage(amount: Int): Boolean {
        health -= amount
        if (health <= 0) {
            isActive = false
            return true
        }
        return false
    }

    fun getHealthPercent(): Float = health.toFloat() / maxHealth.toFloat()

    override fun update(deltaTime: Float) {
        when (type) {
            EnemyType.BOSS -> {
                time += deltaTime
                y += velocityY * deltaTime * 0.3f
                x = startX + kotlin.math.sin(time * 1.5f) * 150f
                x = x.coerceIn(width / 2, 1080f - width / 2)
            }
            else -> super.update(deltaTime)
        }
    }

    override fun DrawScope.render() {
        if (!isActive) return

        val color = when (type) {
            EnemyType.BASIC -> Color(0xFFFF5252)
            EnemyType.FAST -> Color(0xFFFFAB40)
            EnemyType.TANK -> Color(0xFFE040FB)
            EnemyType.BOSS -> Color(0xFFFF1744)
        }

        when (type) {
            EnemyType.BASIC -> {
                val path = Path().apply {
                    moveTo(x, y - height / 2)
                    lineTo(x + width / 2, y)
                    lineTo(x, y + height / 2)
                    lineTo(x - width / 2, y)
                    close()
                }
                drawPath(path, color, style = Fill)
            }
            EnemyType.FAST -> {
                val path = Path().apply {
                    moveTo(x, y - height / 2)
                    lineTo(x + width / 2, y + height / 2)
                    lineTo(x - width / 2, y + height / 2)
                    close()
                }
                drawPath(path, color, style = Fill)
            }
            EnemyType.TANK -> {
                val path = Path().apply {
                    moveTo(x, y - height / 2)
                    lineTo(x + width / 2, y - height / 4)
                    lineTo(x + width / 2, y + height / 4)
                    lineTo(x, y + height / 2)
                    lineTo(x - width / 2, y + height / 4)
                    lineTo(x - width / 2, y - height / 4)
                    close()
                }
                drawPath(path, color, style = Fill)
                drawCircle(Color.White, radius = 4f, center = androidx.compose.ui.geometry.Offset(x, y))
            }
            EnemyType.BOSS -> {
                val path = Path().apply {
                    moveTo(x, y - height / 2)
                    lineTo(x + width / 3, y - height / 3)
                    lineTo(x + width / 2, y)
                    lineTo(x + width / 3, y + height / 3)
                    lineTo(x, y + height / 2)
                    lineTo(x - width / 3, y + height / 3)
                    lineTo(x - width / 2, y)
                    lineTo(x - width / 3, y - height / 3)
                    close()
                }
                drawPath(path, color, style = Fill)
            }
        }
    }
}
