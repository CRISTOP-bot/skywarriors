package com.skywarriors.game.objects

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.skywarriors.util.Constants
import kotlin.random.Random

enum class EnemyType {
    BASIC, FAST, TANK, BOSS
}

class Enemy(type: EnemyType = EnemyType.BASIC) : GameObject() {
    var enemyType: EnemyType = type
    var health: Int = 1
    private var maxHealth: Int = 1
    var canShoot: Boolean = false
    var scoreValue: Int = 100

    private val bodyPaint = Paint().apply { isAntiAlias = true }
    private val detailPaint = Paint().apply { isAntiAlias = true }

    private var timeAlive: Long = 0
    var shootTimer: Long = 0

    init {
        configureForType(type)
    }

    private fun configureForType(type: EnemyType) {
        enemyType = type
        when (type) {
            EnemyType.BASIC -> {
                width = Constants.ENEMY_BASIC_SIZE
                height = Constants.ENEMY_BASIC_SIZE
                velocityY = Constants.ENEMY_BASIC_SPEED
                health = Constants.ENEMY_BASIC_HEALTH
                maxHealth = Constants.ENEMY_BASIC_HEALTH
                canShoot = false
                scoreValue = 100
                bodyPaint.color = android.graphics.Color.rgb(255, 82, 82)
                detailPaint.color = android.graphics.Color.rgb(200, 50, 50)
            }
            EnemyType.FAST -> {
                width = Constants.ENEMY_FAST_SIZE
                height = Constants.ENEMY_FAST_SIZE
                velocityY = Constants.ENEMY_FAST_SPEED
                health = Constants.ENEMY_FAST_HEALTH
                maxHealth = Constants.ENEMY_FAST_HEALTH
                canShoot = false
                scoreValue = 150
                bodyPaint.color = android.graphics.Color.rgb(255, 171, 64)
                detailPaint.color = android.graphics.Color.rgb(220, 140, 30)
            }
            EnemyType.TANK -> {
                width = Constants.ENEMY_TANK_SIZE
                height = Constants.ENEMY_TANK_SIZE
                velocityY = Constants.ENEMY_TANK_SPEED
                health = Constants.ENEMY_TANK_HEALTH
                maxHealth = Constants.ENEMY_TANK_HEALTH
                canShoot = true
                shootTimer = Constants.ENEMY_SHOOT_INTERVAL
                scoreValue = 300
                bodyPaint.color = android.graphics.Color.rgb(224, 64, 251)
                detailPaint.color = android.graphics.Color.rgb(180, 30, 200)
            }
            EnemyType.BOSS -> {
                width = Constants.BOSS_SIZE
                height = Constants.BOSS_SIZE
                velocityY = Constants.BOSS_SPEED
                health = Constants.BOSS_HEALTH
                maxHealth = Constants.BOSS_HEALTH
                canShoot = true
                shootTimer = Constants.ENEMY_SHOOT_INTERVAL / 2
                scoreValue = 2000
                bodyPaint.color = android.graphics.Color.rgb(255, 0, 0)
                detailPaint.color = android.graphics.Color.rgb(180, 0, 0)
            }
        }
    }

    fun updateTimers(deltaTime: Long, screenHeight: Float): List<Bullet> {
        val bullets = mutableListOf<Bullet>()

        if (canShoot) {
            shootTimer -= deltaTime
            if (shootTimer <= 0) {
                shootTimer = if (enemyType == EnemyType.BOSS)
                    Constants.ENEMY_SHOOT_INTERVAL / 2 + Random.nextLong(500)
                else
                    Constants.ENEMY_SHOOT_INTERVAL + Random.nextLong(1000)

                bullets.add(Bullet(x, y + height / 2, false).apply {
                    velocityY = Constants.ENEMY_BULLET_SPEED
                })

                if (enemyType == EnemyType.BOSS) {
                    bullets.add(Bullet(x - 20f, y + height / 2, false).apply {
                        velocityY = Constants.ENEMY_BULLET_SPEED
                        velocityX = -50f
                    })
                    bullets.add(Bullet(x + 20f, y + height / 2, false).apply {
                        velocityY = Constants.ENEMY_BULLET_SPEED
                        velocityX = 50f
                    })
                }
            }
        }

        return bullets
    }

    fun takeDamage(amount: Int = 1): Boolean {
        health -= amount
        if (health <= 0) {
            isActive = false
            return true
        }
        return false
    }

    fun getHealthPercent(): Float = health.toFloat() / maxHealth

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        timeAlive += (deltaTime * 1000).toLong()
        if (enemyType == EnemyType.BOSS) {
            x += kotlin.math.sin(timeAlive / 500f) * 100f * deltaTime
        }
    }

    override fun render(canvas: Canvas) {
        canvas.save()
        canvas.translate(x, y)

        when (enemyType) {
            EnemyType.BASIC -> renderBasic(canvas)
            EnemyType.FAST -> renderFast(canvas)
            EnemyType.TANK -> renderTank(canvas)
            EnemyType.BOSS -> renderBoss(canvas)
        }

        canvas.restore()
    }

    private fun renderBasic(canvas: Canvas) {
        val halfW = width / 2
        val halfH = height / 2

        val path = Path().apply {
            moveTo(0f, halfH)
            lineTo(-halfW, -halfH * 0.3f)
            lineTo(-halfW * 0.3f, -halfH)
            lineTo(halfW * 0.3f, -halfH)
            lineTo(halfW, -halfH * 0.3f)
            close()
        }
        canvas.drawPath(path, bodyPaint)
        canvas.drawCircle(0f, -halfH * 0.2f, halfW * 0.2f, detailPaint)
    }

    private fun renderFast(canvas: Canvas) {
        val halfW = width / 2
        val halfH = height / 2

        val path = Path().apply {
            moveTo(0f, halfH)
            lineTo(-halfW, 0f)
            lineTo(-halfW * 0.5f, -halfH)
            lineTo(halfW * 0.5f, -halfH)
            lineTo(halfW, 0f)
            close()
        }
        canvas.drawPath(path, bodyPaint)
    }

    private fun renderTank(canvas: Canvas) {
        val halfW = width / 2
        val halfH = height / 2

        canvas.drawRoundRect(
            -halfW, -halfH, halfW, halfH,
            8f, 8f, bodyPaint
        )
        canvas.drawRect(-halfW * 0.6f, -halfH * 0.3f, halfW * 0.6f, halfH * 0.3f, detailPaint)
        canvas.drawCircle(0f, -halfH * 0.3f, halfW * 0.2f, android.graphics.Color.rgb(255, 255, 255))
    }

    private fun renderBoss(canvas: Canvas) {
        val halfW = width / 2
        val halfH = height / 2

        val path = Path().apply {
            moveTo(0f, halfH)
            lineTo(-halfW, halfH * 0.5f)
            lineTo(-halfW * 0.8f, -halfH * 0.3f)
            lineTo(-halfW * 0.3f, -halfH)
            lineTo(halfW * 0.3f, -halfH)
            lineTo(halfW * 0.8f, -halfH * 0.3f)
            lineTo(halfW, halfH * 0.5f)
            close()
        }
        canvas.drawPath(path, bodyPaint)
        canvas.drawCircle(0f, 0f, halfW * 0.3f, detailPaint)
        canvas.drawCircle(-halfW * 0.3f, halfH * 0.3f, halfW * 0.15f, detailPaint)
        canvas.drawCircle(halfW * 0.3f, halfH * 0.3f, halfW * 0.15f, detailPaint)
    }
}
