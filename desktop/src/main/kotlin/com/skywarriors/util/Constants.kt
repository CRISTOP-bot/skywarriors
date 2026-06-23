package com.skywarriors.util

object Constants {
    const val FPS = 60
    const val FRAME_TIME = 1000L / FPS

    const val PLAYER_SIZE = 48f
    const val PLAYER_SPEED = 400f
    const val PLAYER_SHOOT_COOLDOWN = 250L
    const val PLAYER_RAPID_COOLDOWN = 100L
    const val PLAYER_INVINCIBLE_TIME = 2000L
    const val PLAYER_MAX_LIVES = 5

    const val BULLET_SIZE = 8f
    const val BULLET_SPEED = 600f
    const val ENEMY_BULLET_SPEED = 300f

    const val ENEMY_BASIC_SIZE = 36f
    const val ENEMY_BASIC_SPEED = 120f
    const val ENEMY_BASIC_HP = 1

    const val ENEMY_FAST_SIZE = 28f
    const val ENEMY_FAST_SPEED = 250f
    const val ENEMY_FAST_HP = 1

    const val ENEMY_TANK_SIZE = 48f
    const val ENEMY_TANK_SPEED = 80f
    const val ENEMY_TANK_HP = 5

    const val BOSS_SIZE = 80f
    const val BOSS_SPEED = 60f
    const val BOSS_HP = 30

    const val POWERUP_CHANCE = 0.15f
    const val POWERUP_DURATION = 8000L
    const val POWERUP_SIZE = 32f
    const val POWERUP_SPEED = 80f

    const val STAR_COUNT = 40
    const val STAR_BASE_SPEED = 60f

    const val EXPLOSION_PARTICLES = 12
    const val EXPLOSION_SPEED = 150f
    const val EXPLOSION_LIFETIME = 500L

    const val SPAWN_INTERVAL_START = 1200L
    const val SPAWN_INTERVAL_MIN = 400L
    const val SPAWN_INTERVAL_DECREASE = 200L
    const val DIFFICULTY_INTERVAL = 15000L

    const val BOSS_INTERVAL_SCORE = 3000
    const val HIGH_SCORE_LIMIT = 10
}
