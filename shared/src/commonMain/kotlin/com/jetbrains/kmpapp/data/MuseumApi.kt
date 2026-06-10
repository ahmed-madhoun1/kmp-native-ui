package com.jetbrains.kmpapp.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlin.coroutines.cancellation.CancellationException

/**
 * Interface defining the API operations for fetching museum objects.
 */
interface MuseumApi {
    /**
     * Fetches a list of museum objects from the remote API.
     *
     * @return A list of [MuseumObject] instances.
     */
    suspend fun getData(): List<MuseumObject>
}

/**
 * Ktor implementation of [MuseumApi].
 * This class handles network requests to retrieve museum data using an injected [HttpClient].
 *
 * @param client The [HttpClient] used to make network requests.
 */
class KtorMuseumApi(private val client: HttpClient) : MuseumApi {
    companion object {
        /**
         * The remote URL to fetch the JSON list of museum objects.
         */
        private const val API_URL =
            "https://raw.githubusercontent.com/Kotlin/KMP-App-Template-Native/main/list.json"
    }

    /**
     * Fetches the data from [API_URL] and parses it into a list of [MuseumObject].
     * Handles exceptions gracefully by returning an empty list on failure,
     * except for [CancellationException] which is rethrown to properly support coroutine cancellation.
     */
    override suspend fun getData(): List<MuseumObject> {
        return try {
            // Perform a GET request and parse the JSON response body
            client.get(API_URL).body()
        } catch (e: Exception) {
            // If the coroutine was cancelled, rethrow the exception to allow normal cancellation flow
            if (e is CancellationException) throw e
            // Print the stack trace for debugging purposes
            e.printStackTrace()

            // Return an empty list as a fallback in case of a network or parsing error
            emptyList()
        }
    }
}