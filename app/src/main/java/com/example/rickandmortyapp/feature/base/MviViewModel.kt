package com.example.rickandmortyapp.feature.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel for the MVI pattern.
 *
 * Subclasses only need to:
 *  1. Provide [createInitialState] — the starting state for the screen.
 *  2. Implement [handleEvent] — describe what to do for each user action.
 *
 * The base class wires up the [StateFlow] for state and a [Channel] for
 * one-shot [UiEffect]s so every feature ViewModel gets consistent plumbing
 * for free.
 *
 * @param S Screen state — must implement [UiState].
 * @param E User events — must implement [UiEvent].
 * @param F One-shot side-effects — must implement [UiEffect].
 */
abstract class MviViewModel<S : UiState, E : UiEvent, F : UiEffect> : ViewModel() {

    // ─── State ───────────────────────────────────────────────────────────────

    private val initialState: S by lazy { createInitialState() }

    private val _state: MutableStateFlow<S> by lazy { MutableStateFlow(initialState) }

    /** The current UI state. Compose screens collect this as State<S>. */
    val state: StateFlow<S> by lazy { _state.asStateFlow() }

    // ─── Effects ─────────────────────────────────────────────────────────────

    private val _effect: Channel<F> = Channel(Channel.BUFFERED)

    /**
     * One-shot effects to be consumed by the View exactly once.
     * Uses a [Channel] internally so effects are not replayed on recomposition.
     */
    val effect: Flow<F> = _effect.receiveAsFlow()

    // ─── Abstract API ─────────────────────────────────────────────────────────

    /** Returns the initial state for this screen. Called lazily on first access. */
    abstract fun createInitialState(): S

    /**
     * Route each incoming [UiEvent] to the appropriate logic.
     * Called from [onEvent] which is the entry point for the View.
     */
    protected abstract fun handleEvent(event: E)

    // ─── Public helpers ───────────────────────────────────────────────────────

    /**
     * Entry-point for the View to send user actions.
     * Delegates to [handleEvent].
     */
    fun onEvent(event: E) = handleEvent(event)

    /**
     * Atomically update the current state using a reducer.
     *
     * Usage: `setState { copy(isLoading = true) }`
     */
    protected fun setState(reduce: S.() -> S) {
        _state.update { it.reduce() }
    }

    /**
     * Emit a one-shot [UiEffect] to the View.
     * The effect is sent on [viewModelScope] so it respects the ViewModel lifecycle.
     *
     * Usage: `setEffect(HomeEffect.NavigateToDetail(id))`
     */
    protected fun setEffect(effect: F) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
