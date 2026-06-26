package com.example.rickandmortyapp.di

import android.content.Context
import androidx.room.Room
import com.example.rickandmortyapp.data.local.dao.CharacterCacheDao
import com.example.rickandmortyapp.data.local.dao.FavoriteCharacterDao
import com.example.rickandmortyapp.data.local.database.AppDatabase
import com.example.rickandmortyapp.data.repository.FavoritesRepository
import com.example.rickandmortyapp.data.repository.FirebaseCurrentUserProvider
import com.example.rickandmortyapp.data.repository.ICurrentUserProvider
import com.example.rickandmortyapp.data.repository.IFavoritesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Hilt DI module for the Favorites feature.
 *
 * Deliberately kept separate from [AppModule] so that if the auth module
 * later needs to swap [FirebaseCurrentUserProvider] for a different
 * [ICurrentUserProvider] implementation, they only need to update
 * one binding in this file.
 *
 * **Auth-swap instructions for the auth team:**
 * Replace the [provideCurrentUserProvider] binding below with your own
 * implementation. Everything else stays the same.
 */
@Module
@InstallIn(SingletonComponent::class)
object FavoritesModule {

    // ─── Room ─────────────────────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "rick_morty_db"
    )
        .fallbackToDestructiveMigration(dropAllTables = false)
        .build()

    @Provides
    @Singleton
    fun provideFavoriteCharacterDao(
        db: AppDatabase
    ): FavoriteCharacterDao = db.favoriteCharacterDao()

    @Provides
    @Singleton
    fun provideCharacterCacheDao(
        db: AppDatabase
    ): CharacterCacheDao = db.characterCacheDao()

    // ─── App-lifetime CoroutineScope ─────────────────────────────────────────

    /**
     * A [CoroutineScope] that survives for the entire process lifetime.
     * Uses [SupervisorJob] so a failed child coroutine never cancels siblings.
     * Injected into [FavoritesRepository] which is a @Singleton and cannot
     * use viewModelScope.
     */
    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ─── Firebase Firestore ───────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    // ─── Auth decoupling seam ─────────────────────────────────────────────────

    /**
     * ⚠️ Auth-team hook:
     * Replace this with your own [ICurrentUserProvider] implementation once
     * the auth module is complete. Do NOT change anything else in this file.
     */
    @Provides
    @Singleton
    fun provideCurrentUserProvider(
        firebaseAuth: FirebaseAuth
    ): ICurrentUserProvider = FirebaseCurrentUserProvider(firebaseAuth)

    // ─── Repository ───────────────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideFavoritesRepository(
        dao: FavoriteCharacterDao,
        firestore: FirebaseFirestore,
        currentUserProvider: ICurrentUserProvider,
        @ApplicationScope appScope: CoroutineScope
    ): IFavoritesRepository = FavoritesRepository(dao, firestore, currentUserProvider, appScope)
}
