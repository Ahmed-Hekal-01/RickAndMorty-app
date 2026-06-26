package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.api.service.IRickAndMortyApiService
import com.example.rickandmortyapp.data.local.dao.CharacterCacheDao
import com.example.rickandmortyapp.data.local.mapper.toCachedEntity
import com.example.rickandmortyapp.data.local.mapper.toCharacter
import com.example.rickandmortyapp.data.mapper.mapSuccess
import com.example.rickandmortyapp.data.mapper.toDomain
import com.example.rickandmortyapp.data.mapper.toPage
import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.data.model.Page
import com.example.rickandmortyapp.data.remote.NetworkResult
import timber.log.Timber
import javax.inject.Inject

class CharacterRepository @Inject constructor(
    private val apiService: IRickAndMortyApiService,
    private val cacheDao: CharacterCacheDao
) : ICharacterRepository {

    override suspend fun getAllCharacters(): NetworkResult<Page<Character>> {
        return apiService.getAllCharacters().mapSuccess { dtoPage ->
            dtoPage.toPage { it.toDomain() }
        }
    }

    override suspend fun getCharacterByID(id: Int): NetworkResult<Character> {
        return apiService.getCharacterByID(id).mapSuccess { it.toDomain() }
    }

    override suspend fun getCharacterByPage(page: Int): NetworkResult<Page<Character>> {
        // Attempt network request first
        val networkResult = apiService.getCharacterByPage(page).mapSuccess { dtoPage ->
            dtoPage.toPage { it.toDomain() }
        }

        return when (networkResult) {
            is NetworkResult.Success -> {
                // Network succeeded -> cache the results for this page
                val domainCharacters = networkResult.data.results
                val cachedEntities = domainCharacters.map { it.toCachedEntity(page) }
                
                try {
                    cacheDao.insertCharacters(cachedEntities)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to cache characters for page $page")
                }
                
                networkResult
            }
            is NetworkResult.Error -> {
                // Network failed (e.g., 429 Too Many Requests or Offline) -> fallback to cache
                Timber.w("Network failed for page $page: ${networkResult}. Attempting cache fallback.")
                try {
                    val cachedEntities = cacheDao.getCharactersByPage(page)
                    if (cachedEntities.isNotEmpty()) {
                        Timber.d("Successfully loaded ${cachedEntities.size} characters from cache for page $page")
                        val domainCharacters = cachedEntities.map { it.toCharacter() }
                        // Construct a fallback Page. If we got exactly 20 (API page size), assume there's a next page.
                        val fallbackPage = Page(
                            count = cachedEntities.size,
                            pages = if (cachedEntities.size == 20) page + 1 else page,
                            next = if (cachedEntities.size == 20) "fallback_next_page" else null,
                            prev = if (page > 1) "fallback_prev_page" else null,
                            results = domainCharacters
                        )
                        NetworkResult.Success(fallbackPage)
                    } else {
                        // Cache is also empty, bubble up the network error
                        networkResult
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Cache fallback failed for page $page")
                    networkResult
                }
            }
        }
    }

    override suspend fun getListOfCharactersByIds(ids: List<Int>): NetworkResult<List<Character>> {
        return apiService.getListOfCharactersByIds(ids).mapSuccess { idsList ->
            idsList.map { it.toDomain() }
        }
    }

    override suspend fun searchCharacters(
        name: String,
        status: CharacterStatus?,
        page: Int
    ): NetworkResult<Page<Character>> {
        val statusString = status?.name?.lowercase()
        return apiService.searchCharacters(name, statusString, page).mapSuccess { dtoPage ->
            dtoPage.toPage { it.toDomain() }
        }
    }
}