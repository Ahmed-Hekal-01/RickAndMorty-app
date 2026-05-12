package com.example.rickandmortyapp.feature.base

/**
 * Marker interface for all MVI one-shot side-effects.
 *
 * Effects are transient events that should happen exactly once regardless of
 * recomposition or screen rotation — e.g. navigation commands and snackbars.
 * Every feature's sealed effect class must implement this so it can be used
 * as the [F] type parameter in [MviViewModel].
 */
interface UiEffect
