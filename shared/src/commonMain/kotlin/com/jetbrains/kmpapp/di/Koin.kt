package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.data.InMemoryMuseumStorage
import com.jetbrains.kmpapp.data.KtorMuseumApi
import com.jetbrains.kmpapp.data.MuseumApi
import com.jetbrains.kmpapp.data.MuseumRepository
import com.jetbrains.kmpapp.data.MuseumStorage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * The core data module for Koin dependency injection.
 * Provides the definitions for network client, API, storage, and the repository.
 */
val dataModule = module {
    // Provide a singleton instance of Ktor HttpClient
    single {
        // Configure JSON serialization to ignore unknown keys to prevent crashes on schema changes
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            // Install content negotiation for JSON parsing
            install(ContentNegotiation) {
                // Accepts any content type as JSON to work around API headers
                // TODO: Fix API so it serves application/json natively
                json(json, contentType = ContentType.Any)
            }
        }
    }

    // Provide the Ktor implementation of MuseumApi
    single<MuseumApi> { KtorMuseumApi(get()) }
    // Provide the in-memory implementation of MuseumStorage
    single<MuseumStorage> { InMemoryMuseumStorage() }
    
    // Provide the MuseumRepository as a singleton and initialize it upon creation
    single {
        MuseumRepository(get(), get()).apply {
            initialize() // Start background data refresh
        }
    }
}

/**
 * Initializes Koin for dependency injection.
 * This overload is typically used from iOS where no extra platform modules are required.
 */
fun initKoin() = initKoin(emptyList())

/**
 * Initializes Koin with the core data module and any additional platform-specific modules.
 * This is typically called from the Android Application class.
 *
 * @param extraModules Additional Koin modules to be loaded.
 */
fun initKoin(extraModules: List<Module>) {
    startKoin {
        modules(
            dataModule,
            *extraModules.toTypedArray(),
        )
    }
}
