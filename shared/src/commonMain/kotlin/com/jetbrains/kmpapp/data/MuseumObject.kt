package com.jetbrains.kmpapp.data

import kotlinx.serialization.Serializable

/**
 * Represents a single museum object entity.
 * This data class is serializable, meaning it can be automatically converted to/from JSON.
 *
 * @property objectID The unique identifier for the museum object.
 * @property title The title or name of the object.
 * @property artistDisplayName The name of the artist who created the object.
 * @property medium The materials or techniques used to create the object.
 * @property dimensions The physical dimensions of the object.
 * @property objectURL The remote URL providing more details about the object.
 * @property objectDate The date or time period when the object was created.
 * @property primaryImage A URL to a high-resolution primary image of the object.
 * @property primaryImageSmall A URL to a smaller, low-resolution primary image of the object.
 * @property repository The physical location or institution holding the object.
 * @property department The department within the repository that categorizes the object.
 * @property creditLine The credit line indicating how the object was acquired.
 */
@Serializable
data class MuseumObject(
    val objectID: Int,
    val title: String,
    val artistDisplayName: String,
    val medium: String,
    val dimensions: String,
    val objectURL: String,
    val objectDate: String,
    val primaryImage: String,
    val primaryImageSmall: String,
    val repository: String,
    val department: String,
    val creditLine: String,
)
