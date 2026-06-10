package com.jetbrains.kmpapp

import android.app.Application
import com.jetbrains.kmpapp.di.initKoin
import com.jetbrains.kmpapp.screens.DetailViewModel
import com.jetbrains.kmpapp.screens.ListViewModel
import org.koin.dsl.module

/**
 * The Android Application class.
 * This class is initialized before any activities or services start,
 * making it the ideal place to configure global dependencies.
 */
class MuseumApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin dependency injection for the Android platform
        initKoin(
            // Pass in an extra module specific to Android to provide the ViewModels
            listOf(
                module {
                    // Provide the ViewModels using factories to ensure a fresh instance per screen lifecycle
                    factory { ListViewModel(get()) }
                    factory { DetailViewModel(get()) }
                }
            )
        )
    }
}
