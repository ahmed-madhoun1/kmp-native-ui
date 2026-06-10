package com.jetbrains.kmpapp

import com.jetbrains.kmpapp.data.MuseumRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * A helper class for iOS to expose Koin dependencies.
 * Since Swift cannot naturally access Kotlin's inline reified functions for dependency injection,
 * this class provides concrete properties that resolve the dependencies via [KoinComponent].
 */
class KoinDependencies : KoinComponent {
    /**
     * The injected instance of [MuseumRepository] to be consumed by iOS ViewModels.
     */
    val museumRepository: MuseumRepository by inject()
}
