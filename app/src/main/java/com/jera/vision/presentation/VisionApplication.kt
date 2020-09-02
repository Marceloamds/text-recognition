package com.jera.vision.presentation

import android.app.Application
import com.jera.vision.presentation.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VisionApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@VisionApplication)
            modules(
                listOf(
                    networkingModule(),
                    viewModelModule(),
                    resourceModule()
                )
            )
        }
    }
}