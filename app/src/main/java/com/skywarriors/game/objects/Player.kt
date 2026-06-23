package com.skywarriors.game.objects

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.skywarriors.util.Constants

class Player : GameObject() {
    var lives: Int = 3
    var score: Int = 0
    var shieldActive: Boolean = false
    var rapidFireActive: Boolean = false
    var spreadShotActive: Boolean = false
    var isInvincible: Boolean = false
    var canShoot: Boolean = true

    private var invincibleTimer: Long = 0
    private var shieldTimer: Long = 0
    private var rapidTimer: Long = 0
    private var spreadTimer: Long = 0

    private val bodyPaint = Paint().apply {
        color = android.graphics.Color.rgb(0, 229, 255)
        isAntiAlias = true
    }
    private val cockpitPaint = Paint().apply {
        color = android.graphics.Color.rgb(255, 255, 255)
        isAntiAlias = true
    }
    private val wingPaint = Paint().apply {
        color = android.graphics.Color.rgb(0, 180, 220)
        isAntiAlias = true
    }
    private val shieldPaint = Paint().apply {
        color = android.graphics.Color.argb(100, 68, 138, 255)
        isAntiAlias = true
    }
    private val invinciblePaint = Paint().apply {
        color = android.graphics.Color.argb(80, 255, 255, 255)
        isAntiAlias = true
    }

    init {
        width = Constants.PLAYER_SIZE
        height = Constants.PLAYER_SIZE
    }

    fun reset() {
        lives = 3
        score = 0
        shieldActive = false
        rapidFireActive = false
        spreadShotActive = false
        isInvincible = false
        canShoot = true
        invincibleTimer = 0
        shieldTimer = 0
        rapidTimer = 0
        spreadTimer = 0
    }

    fun startInvincible() {
        isInvincible = true
        invincibleTimer = Constants.PLAYER_INVINCIBLE_DURATION
    }

    fun activateShield() {
        shieldActive = true
        shieldTimer = Constants.POWERUP_DURATION
    }

    fun activateRapidFire() {
        rapidFireActive = true
        rapidTimer = Constants.POWERUP_DURATION
    }

    fun activateSpreadShot() {
        spreadShotActive = true
        spreadTimer = Constants.POWERUP_DURATION
    }

    fun updatePowerups(deltaTime: Long) {
        val dt = deltaTime

        if (shieldActive) {
            shieldTimer -= dt
            if (shieldTimer <= 0) {
                shieldActive = false
                shieldTimer = 0
            }
        }
        if (rapidFireActive) {
            rapidTimer -= dt
            if (rapidTimer <= 0) {
                rapidFireActive = false
                rapidTimer = 0
            }
        }
        if (spreadShotActive) {
            spreadTimer -= dt
            if (spreadTimer <= 0) {
                spreadShotActive = false
                spreadTimer = 0
            }
        }
        if (isInvincible) {
            invincibleTimer -= dt
            if (invincibleTimer <= 0) {
                isInvincible = false
                invincibleTimer = 0
            }
        }
    }

    fun getShootCooldown(): Long {
        return if (rapidFireActive) Constants.PLAYER_RAPID_COOLDOWN
        else Constants.PLAYER_SHOOT_COOLDOWN
    }

    fun hit(): Boolean {
        if (isInvincible) return false
        if (shieldActive) {
            shieldActive = false
            shieldTimer = 0
            return false
        }
        lives--
        if (lives > 0) {
            startInvincible()
        }
        return lives <= 0
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        val halfW = width / 2
        val halfH = height / 2
        x = x.coerceIn(halfW + Constants.MOVE_MARGIN, (screenWidth ?: 1080f) - halfW - Constants.MOVE_MARGIN)
        y = y.coerceIn(halfH + Constants.MOVE_MARGIN, (screenHeight ?: 1920f) - halfH - Constants.MOVE_MARGIN)
    }

    override fun render(canvas: Canvas) {
        if (isInvincible && System.currentTimeMillis() % 200 < 100) return

        canvas.save()
        canvas.translate(x, y)

        val halfW = width / 2
        val halfH = height / 2

        val bodyPath = Path().apply {
            moveTo(0f, -halfH)
            lineTo(-halfW * 0.4f, halfH * 0.2f)
            lineTo(-halfW * 0.2f, halfH * 0.5f)
            lineTo(0f, halfH * 0.3f)
            lineTo(halfW * 0.2f, halfH * 0.5f)
            lineTo(halfW * 0.4f, halfH * 0.2f)
            close()
        }
        canvas.drawPath(bodyPath, bodyPaint)

        canvas.drawRect(-halfW * 0.5f, -halfH * 0.1f, -halfW * 0.3f, halfH * 0.1f, wingPaint)
        canvas.drawRect(halfW * 0.3f, -halfH * 0.1f, halfW * 0.5f, halfH * 0.1f, wingPaint)

        canvas.drawCircle(0f, -halfH * 0.2f, halfW * 0.15f, cockpitPaint)

        canvas.restore()

        if (shieldActive) {
            canvas.drawCircle(x, y, width * 0.7f, shieldPaint)
        }
    }

    companion object {
        var screenWidth: Float? = null
        var screenHeight: Float? = null
    }
}
