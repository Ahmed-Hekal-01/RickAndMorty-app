package com.example.rickandmortyapp.feature.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel for the MVI pattern.
 *
 * Subclasses only need to:
 *  1. Provide [createInitialState] — the starting state for the screen.
 *  2. Implement [handleEvent] — describe what to do for each user action.
 *
 * The base class wires up the [StateFlow] for state and a [MutableSharedFlow]
 * for one-shot [UiEffect]s so every feature ViewModel gets consistent plumbing
 * for free.
 *
 * @param S Screen state — must implement [UiState].
 * @param E User events — must implement [UiEvent].
 * @param F One-shot side-effects — must implement [UiEffect].
 *
 * ─── Why SharedFlow instead of Channel ───────────────────────────────────────
 *
 * `Channel(BUFFERED)` accumulates effects in a buffer. When the collector
 * (LaunchedEffect) is cancelled mid-flight by navigation, those buffered effects
 * survive and are replayed the next time the screen enters composition — causing
 * snackbars or navigation actions to fire again on back-navigation.
 *
 * `MutableSharedFlow(replay = 0, extraBufferCapacity = 0)` with [tryEmit]:
 * • `replay = 0`            — no cached value is ever replayed to new subscribers.
 * • `extraBufferCapacity = 0` — no internal buffer; emission is either delivered
 *                              synchronously to a current subscriber or dropped.
 * • [tryEmit]               — non-suspending; returns false (drops the effect)
 *                              if no subscriber is actively collecting right now.
 *
 * Result: effects are truly one-shot. If the screen navigates away before the
 * effect is consumed, it is silently dropped — never replayed on back-navigation.
 * For UI feedback (snackbars, toasts) this is always the correct behaviour.
 */
abstract class MviViewModel<S : UiState, E : UiEvent, F : UiEffect> : ViewModel() {

    // ─── State ───────────────────────────────────────────────────────────────

    private val initialState: S by lazy { createInitialState() }

    private val _state: MutableStateFlow<S> by lazy { MutableStateFlow(initialState) }

    /** The current UI state. Compose screens collect this as State<S>. */
    val state: StateFlow<S> by lazy { _state.asStateFlow() }

    // ─── Effects ─────────────────────────────────────────────────────────────

    private val _effect = MutableSharedFlow<F>(
        replay = 0,
        extraBufferCapacity = 0
    )

    /**
     * One-shot effects to be consumed by the View exactly once.
     *
     * Backed by [MutableSharedFlow] with zero replay and zero buffer so stale
     * effects are never re-delivered after the collector re-subscribes (e.g.
     * on back-navigation).
     */
    val effect: SharedFlow<F> = _effect.asSharedFlow()

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
     *
     * Uses [MutableSharedFlow.tryEmit] — non-suspending and thread-safe.
     * The effect is delivered only if a subscriber is actively collecting
     * at this moment. If the screen is off-screen (collector cancelled by
     * navigation), the effect is silently dropped and will NOT replay when
     * the screen returns to composition.
     *
     * Usage: `setEffect(HomeEffect.NavigateToDetail(id))`
     */
    protected fun setEffect(effect: F) {
        val emitted = _effect.tryEmit(effect)
        if (!emitted) {
            // The collector was not active (screen navigated away).
            // Launch a coroutine to wait for the next subscriber window.
            // The coroutine is cancelled with viewModelScope when the ViewModel
            // is cleared, preventing leaks.
            viewModelScope.launch { _effect.emit(effect) }
        }
    }
}
