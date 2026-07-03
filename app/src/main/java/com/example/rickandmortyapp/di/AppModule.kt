package com.example.rickandmortyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.rickandmortyapp.data.api.KtorClient
import com.example.rickandmortyapp.data.api.service.IRickAndMortyApiService
import com.example.rickandmortyapp.data.api.service.KtorRickAndMortyService
import com.example.rickandmortyapp.data.local.dao.CharacterCacheDao
import com.example.rickandmortyapp.data.repository.AuthRepository
import com.example.rickandmortyapp.data.repository.CharacterRepository
import com.example.rickandmortyapp.data.repository.EpisodeRepository
import com.example.rickandmortyapp.data.repository.IAuthRepository
import com.example.rickandmortyapp.data.repository.ICharacterRepository
import com.example.rickandmortyapp.data.repository.IEpisodeRepository
import com.example.rickandmortyapp.data.repository.ILocationRepository
import com.example.rickandmortyapp.data.repository.ISessionRepository
import com.example.rickandmortyapp.data.repository.ISettingsRepository
import com.example.rickandmortyapp.data.repository.IUserProfileRepository
import com.example.rickandmortyapp.data.repository.LocationRepository
import com.example.rickandmortyapp.data.repository.SessionRepository
import com.example.rickandmortyapp.data.repository.SettingsRepository
import com.example.rickandmortyapp.data.repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Named
import javax.inject.Singleton

// ─── DataStore instances ───────────────────────────────────────────────────────
// Two separate DataStore files: one for auth session, one for user settings.

private val Context.sessionDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "session_prefs")

private val Context.settingsDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "settings_prefs")

// ──────────────────────────────────────────────────────────────────────────────

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ─── Network ──────────────────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = KtorClient.httpClient

    @Provides
    @Singleton
    fun provideRickAndMortyApiService(
        client: HttpClient
    ): IRickAndMortyApiService = KtorRickAndMortyService(client)

    // ─── Rick & Morty Repositories ────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideCharacterRepository(
        apiService: IRickAndMortyApiService,
        cacheDao: CharacterCacheDao
    ): ICharacterRepository = CharacterRepository(apiService, cacheDao)

    @Provides
    @Singleton
    fun provideLocationRepository(
        apiService: IRickAndMortyApiService
    ): ILocationRepository = LocationRepository(apiService)

    @Provides
    @Singleton
    fun provideEpisodeRepository(
        apiService: IRickAndMortyApiService
    ): IEpisodeRepository = EpisodeRepository(apiService)

    // ─── Firebase ─────────────────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth
    ): IAuthRepository = AuthRepository(firebaseAuth)

    @Provides
    @Singleton
    fun provideUserProfileRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): IUserProfileRepository = UserProfileRepository(firebaseAuth, firestore)

    // ─── DataStore ────────────────────────────────────────────────────────────

    @Provides
    @Singleton
    @Named("session")
    fun provideSessionDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.sessionDataStore

    @Provides
    @Singleton
    @Named("settings")
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.settingsDataStore

    @Provides
    @Singleton
    fun provideSessionRepository(
        @Named("session") dataStore: DataStore<Preferences>
    ): ISessionRepository = SessionRepository(dataStore)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @Named("settings") dataStore: DataStore<Preferences>
    ): ISettingsRepository = SettingsRepository(dataStore)

    @Provides
    @Singleton
    fun provideStringProvider(
        @ApplicationContext context: Context
    ): com.example.rickandmortyapp.util.StringProvider = com.example.rickandmortyapp.util.StringProviderImpl(context)
}
