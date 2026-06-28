package com.example.rickandmortyapp.feature.auth.register

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import timber.log.Timber

class GoogleAuthUiClient(
    private val context: Context
) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun fetchGoogleIdToken(): NetworkResult<String> {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                NetworkResult.Success(googleIdTokenCredential.idToken)
            } else {
                Timber.w("Unexpected credential type: %s", credential.type)
                NetworkResult.Error.BackendError.UnKnown
            }
        } catch (e: GetCredentialCancellationException) {
            // The user explicitly closed or swiped down the bottom sheet dialog
            Timber.i("Google sign-in cancelled by user")
            NetworkResult.Error.UserCancellation
        } catch (e: GetCredentialException) {
            // Configuration issues, missing Google Play Services, or internal failures
            Timber.e(e, "Google sign-in failed")
            NetworkResult.Error.BackendError.UnKnown
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error during Google sign-in")
            NetworkResult.Error.BackendError.UnKnown
        }
    }

}