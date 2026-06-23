package com.skywarriors.game.engine

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.skywarriors.audio.SoundManager
import com.skywarriors.data.GameDatabase
import com.skywarriors.util.Constants

class GameView(
    context: Context,
    private val soundManager: SoundManager,
    private val database: GameDatabase
) : SurfaceView(context), SurfaceHolder.Callback, Runnable {

    private var thread: Thread? = null
    private var isRunning: Boolean = false
    private lateinit var gameEngine: GameEngine

    private var lastFrameTime: Long = 0
    private var deltaTime: Long = 0

    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val width = width.toFloat()
        val height = height.toFloat()

        gameEngine = GameEngine(width, height, soundManager, database)

        isRunning = true
        thread = Thread(this)
        thread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (::gameEngine.isInitialized) {
            gameEngine.resize(width.toFloat(), height.toFloat())
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isRunning = false
        try {
            thread?.join()
        } catch (_: InterruptedException) {
        }
    }

    override fun run() {
        lastFrameTime = System.nanoTime()
        val targetFrameTime = 1_000_000_000L / Constants.TARGET_FPS

        while (isRunning) {
            val now = System.nanoTime()
            deltaTime = (now - lastFrameTime) / 1_000_000L
            lastFrameTime = now

            if (deltaTime > 50) deltaTime = 50

            gameEngine.update(deltaTime)

            val surfaceHolder = holder
            val canvas: Canvas? = surfaceHolder.lockCanvas()
            if (canvas != null) {
                try {
                    canvas.drawColor(android.graphics.Color.rgb(13, 13, 43))
                    gameEngine.render(canvas)
                } catch (_: Exception) {
                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }

            val frameTime = System.nanoTime() - now
            val sleepTime = (targetFrameTime - frameTime) / 1_000_000L
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime)
                } catch (_: InterruptedException) {
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (::gameEngine.isInitialized) {
            gameEngine.handleTouch(event)
            performClick()
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean = true

    fun togglePause() {
        if (::gameEngine.isInitialized) {
            gameEngine.togglePause()
        }
    }
}
