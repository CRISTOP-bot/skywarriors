package com.skywarriors.game.ui

import android.view.MotionEvent
import com.skywarriors.game.objects.Player
import com.skywarriors.util.Constants

class TouchHandler {
    private var activePointerId: Int = -1
    private var lastTouchX: Float = 0f
    private var lastTouchY: Float = 0f
    private var isTouching: Boolean = false
    var lastResult: TouchResult? = null

    fun handleTouchEvent(event: MotionEvent): TouchResult {
        val result = handleInternal(event)
        lastResult = result
        return result
    }

    private fun handleInternal(event: MotionEvent): TouchResult {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val idx = event.actionIndex
                activePointerId = event.getPointerId(idx)
                lastTouchX = event.getX(idx)
                lastTouchY = event.getY(idx)
                isTouching = true
                return TouchResult(true, lastTouchX, lastTouchY)
            }
            MotionEvent.ACTION_MOVE -> {
                if (activePointerId != -1) {
                    val idx = event.findPointerIndex(activePointerId)
                    if (idx >= 0) {
                        lastTouchX = event.getX(idx)
                        lastTouchY = event.getY(idx)
                        val dx = lastTouchX - (Player.screenWidth ?: 1080f) / 2
                        val dy = lastTouchY - (Player.screenHeight ?: 1920f) / 2
                        if (kotlin.math.abs(dx) > Constants.TOUCH_DEAD_ZONE ||
                            kotlin.math.abs(dy) > Constants.TOUCH_DEAD_ZONE
                        ) {
                            return TouchResult(true, lastTouchX, lastTouchY)
                        }
                    }
                }
                return TouchResult(false, lastTouchX, lastTouchY)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val idx = event.actionIndex
                val pointerId = event.getPointerId(idx)
                if (pointerId == activePointerId) {
                    activePointerId = -1
                    isTouching = false
                }
                return TouchResult(false, lastTouchX, lastTouchY)
            }
            MotionEvent.ACTION_CANCEL -> {
                activePointerId = -1
                isTouching = false
                return TouchResult(false, lastTouchX, lastTouchY)
            }
        }
        return TouchResult(false, lastTouchX, lastTouchY)
    }

    data class TouchResult(
        val isDown: Boolean,
        val x: Float,
        val y: Float
    )
}
