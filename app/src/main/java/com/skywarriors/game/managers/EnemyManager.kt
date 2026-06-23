package com.skywarriors.game.managers

import com.skywarriors.game.objects.Enemy
import com.skywarriors.game.objects.EnemyType
import com.skywarriors.game.objects.Bullet
import com.skywarriors.util.Constants
import kotlin.random.Random

class EnemyManager {
    private val enemies = mutableListOf<Enemy>()
    private var spawnTimer: Long = Constants.SPAWN_INTERVAL_INITIAL
    private var difficultyTimer: Long = 0
    private var totalKills: Int = 0
    private var bossSpawned: Boolean = false

    fun update(deltaTime: Long, screenHeight: Float, currentScore: Int): List<Bullet> {
        val newBullets = mutableListOf<Bullet>()

        difficultyTimer += deltaTime
        val difficultyLevel = (difficultyTimer / Constants.DIFFICULTY_RAMP_INTERVAL).toInt()
        val currentSpawnInterval = (Constants.SPAWN_INTERVAL_INITIAL - difficultyLevel * 200L)
            .coerceAtLeast(Constants.SPAWN_INTERVAL_MIN)

        if (!bossSpawned && currentScore >= Constants.BOSS_INTERVAL_SCORE && currentScore % Constants.BOSS_INTERVAL_SCORE < 100) {
            spawnBoss()
            bossSpawned = true
        }
        if (bossSpawned && currentScore < Constants.BOSS_INTERVAL_SCORE * ((currentScore / Constants.BOSS_INTERVAL_SCORE))) {
            bossSpawned = false
        }

        spawnTimer -= deltaTime
        if (spawnTimer <= 0) {
            spawnTimer = currentSpawnInterval + Random.nextLong(400)
            spawnEnemy(difficultyLevel)
        }

        val iter = enemies.iterator()
        while (iter.hasNext()) {
            val enemy = iter.next()
            enemy.update(deltaTime / 1000f)

            if (enemy.y > screenHeight + enemy.height) {
                iter.remove()
                continue
            }

            newBullets.addAll(enemy.updateTimers(deltaTime, screenHeight))
        }

        return newBullets
    }

    private fun spawnEnemy(difficulty: Int) {
        val type = when {
            difficulty >= 5 && Random.nextFloat() < 0.2f -> EnemyType.TANK
            difficulty >= 2 && Random.nextFloat() < 0.35f -> EnemyType.FAST
            else -> EnemyType.BASIC
        }
        val enemy = Enemy(type)
        val screenWidth = Player.screenWidth ?: 1080f
        enemy.x = Random.nextFloat() * (screenWidth - Constants.ENEMY_BASIC_SIZE) + Constants.ENEMY_BASIC_SIZE / 2
        enemy.y = -enemy.height
        enemies.add(enemy)
    }

    private fun spawnBoss() {
        val boss = Enemy(EnemyType.BOSS)
        val screenWidth = Player.screenWidth ?: 1080f
        boss.x = screenWidth / 2
        boss.y = -boss.height
        enemies.add(boss)
    }

    fun getEnemies(): List<Enemy> = enemies

    fun removeEnemy(enemy: Enemy) {
        enemies.remove(enemy)
        totalKills++
    }

    fun clear() {
        enemies.clear()
        spawnTimer = Constants.SPAWN_INTERVAL_INITIAL
        difficultyTimer = 0
        totalKills = 0
        bossSpawned = false
    }

    fun getActiveEnemies(): List<Enemy> = enemies.filter { it.isActive }
}
