package com.example.rickandmortyapp.feature.splash

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.repository.IAuthRepository
import com.example.rickandmortyapp.data.repository.ISessionRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Resolves the app's launch destination based on both auth and session state.
 *
 * Decision logic (in order):
 * 1. If the user is currently signed into Firebase AND a session token is persisted
 *    → navigate to HOME (returning authenticated user).
 * 2. If only Firebase auth is active but no persisted token (e.g. token was cleared
 *    by a force-logout) → navigate to LOGIN and let re-auth flow begin.
 * 3. Otherwise → navigate to LOGIN.
 *
 * Reading the persisted token is a suspend operation (DataStore read), so
 * [checkAuthState] runs inside [viewModelScope] to stay off the main thread.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val sessionRepository: ISessionRepository
) : MviViewModel<SplashState, SplashEvent, SplashEffect>() {

    override fun createInitialState() = SplashState()

    init {
        onEvent(SplashEvent.CheckAuthState)
    }

    override fun handleEvent(event: SplashEvent) {
        when (event) {
            is SplashEvent.CheckAuthState -> checkAuthState()
        }
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            // Both conditions must be true for a fully valid session:
            //  • Firebase still holds an auth object (token not revoked server-side)
            //  • We persisted a token locally after the last successful login/register
            val isFirebaseLoggedIn = authRepository.isLoggedIn
            val persistedToken = sessionRepository.authToken.first()
            val hasValidSession = isFirebaseLoggedIn && persistedToken != null

            val destination = if (hasValidSession) Destination.HOME else Destination.LOGIN
            setState { copy(isLoading = false, destination = destination) }
            setEffect(SplashEffect.NavigateTo(destination))
        }
    }
}
