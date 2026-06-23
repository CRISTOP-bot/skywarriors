package com.skywarriors.game.managers

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.skywarriors.game.objects.Enemy
import com.skywarriors.util.Constants
import kotlin.random.Random

class EnemyManager {
    val enemies = mutableListOf<Enemy>()
    var spawnTimer: Long = 0L
    var difficultyTimer: Long = 0L
    var difficultyLevel: Int = 0
    var currentSpawnInterval: Long = Constants.SPAWN_INTERVAL_START.toLong()
    var bossSpawned: Boolean = false
    private var screenWidth: Float = 1080f

    fun init(screenW: Float) {
        screenWidth = screenW
        enemies.clear()
        spawnTimer = 0L
        difficultyTimer = 0L
        difficultyLevel = 0
        currentSpawnInterval = Constants.SPAWN_INTERVAL_START.toLong()
        bossSpawned = false
    }

    fun update(dt: Float, dtMillis: Long): List<BulletData> {
        val newBullets = mutableListOf<BulletData>()

        enemies.removeAll { !it.isActive }

        difficultyTimer += dtMillis
        if (difficultyTimer >= Constants.DIFFICULTY_INTERVAL.toLong()) {
            difficultyTimer -= Constants.DIFFICULTY_INTERVAL.toLong()
            difficultyLevel++
            currentSpawnInterval = maxOf(
                Constants.SPAWN_INTERVAL_MIN.toLong(),
                currentSpawnInterval - Constants.SPAWN_INTERVAL_DECREASE.toLong()
            )
        }

        spawnTimer += dtMillis
        if (spawnTimer >= currentSpawnInterval) {
            spawnTimer -= currentSpawnInterval
            spawnEnemy()
        }

        for (enemy in enemies) {
            enemy.update(dt)

            if (enemy.type == Enemy.EnemyType.BOSS) {
                enemy.shootTimer -= dtMillis
                if (enemy.shootTimer <= 0) {
                    enemy.shootTimer = 1000L
                    val bulletX = enemy.x
                    val bulletY = enemy.y + enemy.height / 2
                    newBullets.add(BulletData(bulletX, bulletY, 0f, Constants.ENEMY_BULLET_SPEED.toFloat()))
                    newBullets.add(BulletData(bulletX - 30f, bulletY, -50f, Constants.ENEMY_BULLET_SPEED.toFloat()))
                    newBullets.add(BulletData(bulletX + 30f, bulletY, 50f, Constants.ENEMY_BULLET_SPEED.toFloat()))
                }
            } else if (enemy.canShoot) {
                enemy.shootTimer -= dtMillis
                if (enemy.shootTimer <= 0) {
                    enemy.shootTimer = 2000L
                    newBullets.add(BulletData(enemy.x, enemy.y + enemy.height / 2, 0f, Constants.ENEMY_BULLET_SPEED.toFloat()))
                }
            }

            if (enemy.y > 2000f) {
                enemy.isActive = false
            }
        }

        return newBullets
    }

    private fun spawnEnemy() {
        val margin = 50f
        val x = Random.nextFloat() * (screenWidth - margin * 2) + margin

        val type = when {
            difficultyLevel >= 5 && Random.nextFloat() < 0.2f -> Enemy.EnemyType.TANK
            difficultyLevel >= 2 && Random.nextFloat() < 0.35f -> Enemy.EnemyType.FAST
            else -> Enemy.EnemyType.BASIC
        }

        val enemy = Enemy()
        enemy.init(type, x, -50f)
        enemies.add(enemy)
    }

    fun trySpawnBoss(currentScore: Int): Boolean {
        if (!bossSpawned && currentScore > 0 && currentScore % Constants.BOSS_INTERVAL_SCORE == 0) {
            val boss = Enemy()
            boss.init(Enemy.EnemyType.BOSS, screenWidth / 2, -100f)
            enemies.add(boss)
            bossSpawned = true
            return true
        }
        if (bossSpawned) {
            val hasBoss = enemies.any { it.type == Enemy.EnemyType.BOSS && it.isActive }
            if (!hasBoss) {
                bossSpawned = false
            }
        }
        return false
    }

    fun DrawScope.render() {
        for (enemy in enemies) {
            enemy.render()
        }
    }

    data class BulletData(val x: Float, val y: Float, val velX: Float, val velY: Float)
}
