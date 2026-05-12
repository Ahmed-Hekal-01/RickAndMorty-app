package com.example.rickandmortyapp.feature.base

/**
 * Marker interface for all MVI UI events (user intentions).
 *
 * Every feature's sealed event class must implement this so it can be used
 * as the [E] type parameter in [MviViewModel].
 */
interface UiEvent
