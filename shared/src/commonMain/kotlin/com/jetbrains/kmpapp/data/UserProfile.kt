package com.jetbrains.kmpapp.data

/**
 * A dummy user profile model to demonstrate shared state across platforms.
 */
data class UserProfile(
    val name: String,
    val email: String,
    val avatarUrl: String
)
