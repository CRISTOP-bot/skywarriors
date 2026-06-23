package com.skywarriors.game.objects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.skywarriors.util.Constants
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Player : GameObject() {
    var lives: Int = Constants.PLAYER_MAX_LIVES
    var score: Int = 0
    var shieldActive: Boolean = false
    var rapidFireActive: Boolean = false
    var spreadShotActive: Boolean = false
    var isInvincible: Boolean = false
    var shootCooldown: Long = 0

    var powerUpTimer: Long = 0L
    var invincibleTimer: Long = 0L

    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f
    private val moveMargin = 20f

    fun init(screenW: Float, screenH: Float) {
        screenWidth = screenW
        screenHeight = screenH
        width = Constants.PLAYER_SIZE
        height = Constants.PLAYER_SIZE
        x = screenW / 2
        y = screenH - 100f
        lives = Constants.PLAYER_MAX_LIVES
        score = 0
        shieldActive = false
        rapidFireActive = false
        spreadShotActive = false
        isInvincible = false
        shootCooldown = 0
    }

    fun hit(): Boolean {
        if (isInvincible) return false
        if (shieldActive) {
            shieldActive = false
            return false
        }
        lives--
        if (lives > 0) {
            isInvincible = true
            invincibleTimer = Constants.PLAYER_INVINCIBLE_TIME.toLong()
        }
        return lives <= 0
    }

    fun update(dt: Float, currentTime: Long) {
        val dtMillis = (dt * 1000).toLong()

        if (shootCooldown > 0) {
            shootCooldown = maxOf(0, shootCooldown - dtMillis)
        }
        if (isInvincible) {
            invincibleTimer -= dtMillis
            if (invincibleTimer <= 0) {
                isInvincible = false
            }
        }
        if (shieldActive || rapidFireActive || spreadShotActive) {
            powerUpTimer -= dtMillis
            if (powerUpTimer <= 0) {
                shieldActive = false
                rapidFireActive = false
                spreadShotActive = false
            }
        }

        x += velocityX * dt
        y += velocityY * dt
        x = x.coerceIn(moveMargin, screenWidth - moveMargin)
        y = y.coerceIn(moveMargin, screenHeight - moveMargin)
    }

    fun canShoot(): Boolean = shootCooldown <= 0

    fun resetShootCooldown() {
        shootCooldown = if (rapidFireActive) Constants.PLAYER_RAPID_COOLDOWN.toLong()
        else Constants.PLAYER_SHOOT_COOLDOWN.toLong()
    }

    fun activatePowerUp(type: PowerUp.PowerUpType) {
        when (type) {
            PowerUp.PowerUpType.SHIELD -> shieldActive = true
            PowerUp.PowerUpType.RAPID_FIRE -> rapidFireActive = true
            PowerUp.PowerUpType.SPREAD_SHOT -> spreadShotActive = true
            PowerUp.PowerUpType.EXTRA_LIFE -> lives = minOf(lives + 1, Constants.PLAYER_MAX_LIVES)
        }
        if (type != PowerUp.PowerUpType.EXTRA_LIFE) {
            powerUpTimer = Constants.POWERUP_DURATION.toLong()
        }
    }

    fun moveTo(targetX: Float, targetY: Float, dt: Float) {
        val dx = targetX - x
        val dy = targetY - y
        val dist = kotlin.math.sqrt(dx * dx + dy * dy)
        if (dist > 5f) {
            velocityX = (dx / dist) * Constants.PLAYER_SPEED
            velocityY = (dy / dist) * Constants.PLAYER_SPEED
        } else {
            velocityX = 0f
            velocityY = 0f
        }
        update(dt, 0L)
    }

    override fun DrawScope.render() {
        if (isInvincible && (System.currentTimeMillis() / 100) % 2 == 0L) return

        val path = Path().apply {
            moveTo(x, y - height / 2)
            lineTo(x - width / 2, y + height / 2)
            lineTo(x - width / 4, y + height / 4)
            lineTo(x, y + height / 3)
            lineTo(x + width / 4, y + height / 4)
            lineTo(x + width / 2, y + height / 2)
            close()
        }
        drawPath(path, Color(0xFF00E5FF), style = Fill)

        val cockpitPath = Path().apply {
            moveTo(x, y - height / 4)
            lineTo(x - width / 6, y + height / 6)
            lineTo(x, y + height / 8)
            lineTo(x + width / 6, y + height / 6)
            close()
        }
        drawPath(cockpitPath, Color.White, style = Fill)

        if (shieldActive) {
            drawCircle(Color(0x80448AFF), radius = width * 0.8f, center = Offset(x, y))
        }
    }
}
