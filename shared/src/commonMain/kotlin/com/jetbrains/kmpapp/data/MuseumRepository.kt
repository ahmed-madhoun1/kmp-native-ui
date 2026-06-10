package com.jetbrains.kmpapp.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * The single source of truth for accessing museum object data.
 * This repository coordinates between the remote API ([MuseumApi]) and local storage ([MuseumStorage]).
 *
 * @property museumApi The remote data source.
 * @property museumStorage The local data storage/cache.
 */
class MuseumRepository(
    private val museumApi: MuseumApi,
    private val museumStorage: MuseumStorage,
) {
    // Defines a coroutine scope with a SupervisorJob for the repository to launch background tasks
    // A SupervisorJob ensures that if one child coroutine fails, it doesn't cancel the entire scope
    private val scope = CoroutineScope(SupervisorJob())

    /**
     * Initializes the repository by launching a background coroutine to refresh data from the API.
     */
    fun initialize() {
        scope.launch {
            refresh()
        }
    }

    /**
     * Fetches fresh data from the remote API and saves it into local storage.
     */
    suspend fun refresh() {
        // Fetch data remotely and update the local in-memory storage
        museumStorage.saveObjects(museumApi.getData())
    }

    /**
     * Retrieves a continuous flow of all museum objects from local storage.
     *
     * @return A [Flow] emitting the list of [MuseumObject]s.
     */
    fun getObjects(): Flow<List<MuseumObject>> = museumStorage.getObjects()

    /**
     * Retrieves a continuous flow of a specific museum object by its ID from local storage.
     *
     * @param objectId The unique identifier of the desired museum object.
     * @return A [Flow] emitting the matching [MuseumObject] or null if not found.
     */
    fun getObjectById(objectId: Int): Flow<MuseumObject?> = museumStorage.getObjectById(objectId)
}
