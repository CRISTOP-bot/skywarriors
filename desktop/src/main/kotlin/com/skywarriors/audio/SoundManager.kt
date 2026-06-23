package com.skywarriors.audio

class SoundManager {
    private var initialized = false

    fun init() {
        initialized = true
    }

    fun playShoot() {}
    fun playExplosion() {}
    fun playPowerUp() {}
    fun playHit() {}
    fun playGameOver() {}

    fun release() {
        initialized = false
    }
}
