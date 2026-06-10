package com.jetbrains.kmpapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Interface defining operations for local data storage of museum objects.
 */
interface MuseumStorage {
    /**
     * Saves a list of museum objects into storage.
     *
     * @param newObjects The list of objects to save.
     */
    suspend fun saveObjects(newObjects: List<MuseumObject>)

    /**
     * Retrieves a flow of a specific museum object by its ID.
     *
     * @param objectId The ID of the object to retrieve.
     * @return A [Flow] emitting the object or null.
     */
    fun getObjectById(objectId: Int): Flow<MuseumObject?>

    /**
     * Retrieves a flow of all museum objects in storage.
     *
     * @return A [Flow] emitting the entire list of objects.
     */
    fun getObjects(): Flow<List<MuseumObject>>
}

/**
 * An in-memory implementation of [MuseumStorage].
 * Useful for caching data during the app's lifecycle without persisting it to disk.
 */
class InMemoryMuseumStorage : MuseumStorage {
    // Backing field holding the current state of stored objects
    private val storedObjects = MutableStateFlow(emptyList<MuseumObject>())

    /**
     * Updates the in-memory state flow with the newly fetched objects.
     */
    override suspend fun saveObjects(newObjects: List<MuseumObject>) {
        storedObjects.value = newObjects
    }

    /**
     * Maps the underlying state flow of objects to return only the object matching the given [objectId].
     */
    override fun getObjectById(objectId: Int): Flow<MuseumObject?> {
        return storedObjects.map { objects ->
            // Search the list for an object with a matching ID
            objects.find { it.objectID == objectId }
        }
    }

    /**
     * Returns the underlying state flow containing all stored objects.
     */
    override fun getObjects(): Flow<List<MuseumObject>> = storedObjects
}
