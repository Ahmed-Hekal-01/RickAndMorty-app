package com.example.rickandmortyapp.di
import com.example.rickandmortyapp.data.api.KtorClient
import com.example.rickandmortyapp.data.api.service.IRickAndMortyApiService
import com.example.rickandmortyapp.data.api.service.KtorRickAndMortyService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient() : HttpClient {
        return KtorClient.httpClient
    }

    fun provideRickAndMortyApiService(client: HttpClient): IRickAndMortyApiService {
        return KtorRickAndMortyService(client)
    }
}