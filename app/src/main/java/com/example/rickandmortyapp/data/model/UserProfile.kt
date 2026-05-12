package com.example.rickandmortyapp.data.model

/**
 * Domain model representing the currently authenticated user's profile.
 * Sourced from Firebase Authentication.
 */
data class UserProfile(
    val uid: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?
)
