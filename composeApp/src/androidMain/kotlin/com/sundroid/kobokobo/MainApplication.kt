package com.sundroid.kobokobo

import android.app.Application
import com.sundroid.kobokobo.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApplication)
        }
    }
}
