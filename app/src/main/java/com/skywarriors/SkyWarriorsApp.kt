package com.skywarriors

import android.app.Application
import com.skywarriors.data.GameDatabase

class SkyWarriorsApp : Application() {

    val database: GameDatabase by lazy {
        GameDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: SkyWarriorsApp
            private set
    }
}
