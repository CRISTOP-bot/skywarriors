package com.skywarriors.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundManager(context: Context) {

    private var soundPool: SoundPool? = null
    private var shootSoundId: Int = 0
    private var explosionSoundId: Int = 0
    private var powerUpSoundId: Int = 0
    private var hitSoundId: Int = 0
    private var gameOverSoundId: Int = 0
    private var loaded: Boolean = false

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(attrs)
            .build()

        soundPool?.setOnLoadCompleteListener { _, _, _ ->
            loaded = true
        }

        loadSounds(context)
    }

    private fun loadSounds(context: Context) {
        val resId = context.resources

    }

    fun playShoot() {
        playSound(shootSoundId)
    }

    fun playExplosion() {
        playSound(explosionSoundId)
    }

    fun playPowerUp() {
        playSound(powerUpSoundId)
    }

    fun playHit() {
        playSound(hitSoundId)
    }

    fun playGameOver() {
        playSound(gameOverSoundId)
    }

    private fun playSound(soundId: Int) {
        if (loaded && soundId != 0) {
            soundPool?.play(soundId, 0.7f, 0.7f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
