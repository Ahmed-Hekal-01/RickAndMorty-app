package com.example.rickandmortyapp.di
import com.example.rickandmortyapp.data.api.KtorClient
import com.example.rickandmortyapp.data.api.service.IRickAndMortyApiService
import com.example.rickandmortyapp.data.api.service.KtorRickAndMortyService
import com.example.rickandmortyapp.data.repository.CharacterRepository
import com.example.rickandmortyapp.data.repository.EpisodeRepository
import com.example.rickandmortyapp.data.repository.ICharacterRepository
import com.example.rickandmortyapp.data.repository.IEpisodeRepository
import com.example.rickandmortyapp.data.repository.ILocationRepository
import com.example.rickandmortyapp.data.repository.LocationRepository
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
    fun provideHttpClient(): HttpClient = KtorClient.httpClient

    @Provides
    @Singleton
    fun provideRickAndMortyApiService(
        client: HttpClient
    ): IRickAndMortyApiService = KtorRickAndMortyService(client)

    @Provides
    @Singleton
    fun provideCharacterRepository(
        apiService: IRickAndMortyApiService
    ): ICharacterRepository = CharacterRepository(apiService)

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
}
