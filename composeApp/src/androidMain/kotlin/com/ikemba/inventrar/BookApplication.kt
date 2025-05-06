package com.ikemba.inventrar

import android.app.Application
import com.ikemba.inventrar.di.initKoin

import org.koin.android.ext.koin.androidContext

class BookApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@BookApplication)
        }
    }
}