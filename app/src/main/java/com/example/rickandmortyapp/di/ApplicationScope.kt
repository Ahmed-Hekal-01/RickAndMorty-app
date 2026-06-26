package com.example.rickandmortyapp.di

import javax.inject.Qualifier

/**
 * Qualifier for a [kotlinx.coroutines.CoroutineScope] that lives for the
 * entire application lifetime (tied to the Hilt [dagger.hilt.components.SingletonComponent]).
 *
 * Inject this into singleton classes (like repositories) that need to launch
 * long-running coroutines outside of a ViewModel lifecycle.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
