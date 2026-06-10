package com.jetbrains.kmpapp.screens

import com.jetbrains.kmpapp.data.MuseumObject
import com.jetbrains.kmpapp.data.MuseumRepository
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.stateIn
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * ViewModel for the Detail Screen.
 * Inherits from [ViewModel] provided by KMP-ObservableViewModel to support both
 * Compose (Android) and SwiftUI (iOS) natively.
 *
 * @property museumRepository The repository used to fetch object details.
 */
class DetailViewModel(private val museumRepository: MuseumRepository) : ViewModel() {
    // Mutable state holding the currently selected object ID
    private val objectId = MutableStateFlow<Int?>(null)

    /**
     * A state flow holding the detailed [MuseumObject].
     * Reacts to changes in [objectId] and fetches the latest data from the repository.
     * Annotated with [@NativeCoroutinesState] to automatically bridge to Swift Combine/Async-Await.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @NativeCoroutinesState
    val museumObject: StateFlow<MuseumObject?> = objectId
        .flatMapLatest {
            // If no ID is set, emit null
            val id = it ?: return@flatMapLatest flowOf(null)
            // Fetch the object by its ID from the repository
            museumRepository.getObjectById(id)
        }
        // Convert the Flow into a StateFlow scoped to the ViewModel
        // Keeps the subscription active for 5 seconds after the last collector disconnects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /**
     * Sets the ID of the object to be displayed, triggering a data refresh for [museumObject].
     *
     * @param objectId The ID of the museum object.
     */
    fun setId(objectId: Int) {
        this.objectId.value = objectId
    }
}
