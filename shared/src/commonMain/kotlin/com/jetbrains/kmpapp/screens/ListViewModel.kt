package com.jetbrains.kmpapp.screens

import com.jetbrains.kmpapp.data.MuseumObject
import com.jetbrains.kmpapp.data.MuseumRepository
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.stateIn
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.jetbrains.kmpapp.data.UserProfile

/**
 * ViewModel for the List Screen.
 * Inherits from [ViewModel] provided by KMP-ObservableViewModel to support both
 * Compose (Android) and SwiftUI (iOS) natively.
 *
 * @param museumRepository The repository used to fetch the list of objects.
 */
class ListViewModel(museumRepository: MuseumRepository) : ViewModel() {
    
    /**
     * A state flow containing the list of [MuseumObject]s.
     * This exposes the repository's continuous flow as a [StateFlow] scoped to this ViewModel.
     * Annotated with [@NativeCoroutinesState] to automatically bridge to Swift Combine/Async-Await.
     */
    @NativeCoroutinesState
    val objects: StateFlow<List<MuseumObject>> =
        museumRepository.getObjects()
            // Keep the subscription active for 5 seconds after the last collector disconnects
            // Starts with an empty list as the initial state
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * A dummy user profile exposed as shared state.
     * This will feed the Android Navigation Drawer and iOS Profile Sheet.
     */
    @NativeCoroutinesState
    val userProfile: StateFlow<UserProfile> = MutableStateFlow(
        UserProfile(
            name = "John Doe",
            email = "john.doe@example.com",
            avatarUrl = "https://i.pravatar.cc/300"
        )
    )

    // Internal mutable state for liked object IDs
    private val _likedObjectIds = MutableStateFlow<List<Int>>(emptyList())

    /**
     * Shared state tracking which museum objects the user has liked.
     */
    @NativeCoroutinesState
    val likedObjectIds: StateFlow<List<Int>> = _likedObjectIds

    /**
     * Toggles the like status of a specific museum object.
     * @param objectId The ID of the object to toggle.
     */
    fun toggleLike(objectId: Int) {
        _likedObjectIds.update { currentList ->
            if (currentList.contains(objectId)) {
                currentList - objectId
            } else {
                currentList + objectId
            }
        }
    }
}
