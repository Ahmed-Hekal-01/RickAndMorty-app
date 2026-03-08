package com.example.rickandmortyapp.data.api.service

import com.example.rickandmortyapp.data.remote.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KtorRickAndMortyServiceTest {

    @Test
    fun getAllCharacters_returnsSuccess() = runBlocking {
        val client = mockHttpClient { request ->
            assertEquals("/api/character", request.url.encodedPath)
            respondJson(characterPageJson("Rick Sanchez"))
        }

        val result = KtorRickAndMortyService(client).getAllCharacters()

        assertTrue(result is NetworkResult.Success)
        assertEquals("Rick Sanchez", (result as NetworkResult.Success).data.result.first().name)
    }

    @Test
    fun getCharacterByID_returnsSuccess() = runBlocking {
        val client = mockHttpClient { request ->
            assertEquals("/api/character/1", request.url.encodedPath)
            respondJson(characterJson(1, "Rick Sanchez"))
        }

        val result = KtorRickAndMortyService(client).getCharacterByID(1)

        assertTrue(result is NetworkResult.Success)
        assertEquals(1, (result as NetworkResult.Success).data.id)
    }

    @Test
    fun getCharacterByPage_addsPageQuery() = runBlocking {
        val client = mockHttpClient { request ->
            assertEquals("/api/character", request.url.encodedPath)
            assertEquals("2", request.url.parameters["page"])
            respondJson(characterPageJson("Morty Smith"))
        }

        val result = KtorRickAndMortyService(client).getCharacterByPage(2)

        assertTrue(result is NetworkResult.Success)
        assertEquals("Morty Smith", (result as NetworkResult.Success).data.result.first().name)
    }

    @Test
    fun getListOfCharactersByIds_formatsIdsInPath() = runBlocking {
        val client = mockHttpClient { request ->
            assertEquals("/api/character/1,2", request.url.encodedPath)
            respondJson("[${characterJson(1, "Rick Sanchez")},${characterJson(2, "Morty Smith")}]")
        }

        val result = KtorRickAndMortyService(client).getListOfCharactersByIds(listOf(1, 2))

        assertTrue(result is NetworkResult.Success)
        assertEquals(2, (result as NetworkResult.Success).data.size)
    }

    @Test
    fun getAllLocations_returnsSuccess() = runBlocking {
        val client = mockHttpClient { request ->
            assertEquals("/api/location", request.url.encodedPath)
            respondJson(locationPageJson("Citadel of Ricks"))
        }

        val result = KtorRickAndMortyService(client).getAllLocations()

        assertTrue(result is NetworkResult.Success)
        assertEquals("Citadel of Ricks", (result as NetworkResult.Success).data.result.first().name)
    }

    @Test
    fun getLocationByID_returnsSuccess() = runBlocking {
        val client = mockHttpClient { request ->
            assertEquals("/api/location/3", request.url.encodedPath)
            respondJson(locationJson(3, "Anatomy Park"))
        }

        val result = KtorRickAndMortyService(client).getLocationByID(3)

        assertTrue(result is NetworkResult.Success)
        assertEquals(3, (result as NetworkResult.Success).data.id)
    }

    @Test
    fun getLocationByPage_addsPageQuery() = runBlocking {
        val client = mockHttpClient { request ->
            assertEquals("/api/location", request.url.encodedPath)
            assertEquals("4", request.url.parameters["page"])
            respondJson(locationPageJson("Earth (C-137)"))
        }

        val result = KtorRickAndMortyService(client).getLocationByPage(4)

        assertTrue(result is NetworkResult.Success)
        assertEquals("Earth (C-137)", (result as NetworkResult.Success).data.result.first().name)
    }

    @Test
    fun getListOfLocationsByIds_formatsIdsInPath() = runBlocking {
        val client = mockHttpClient { request ->
            assertEquals("/api/location/1,2", request.url.encodedPath)
            respondJson("[${locationJson(1, "Earth")},${locationJson(2, "Abadango")}]")
        }

        val result = KtorRickAndMortyService(client).getListOfLocationsByIds(listOf(1, 2))

        assertTrue(result is NetworkResult.Success)
        assertEquals(2, (result as NetworkResult.Success).data.size)
    }

    @Test
    fun getAllEpisodes_returnsSuccess() = runBlocking {
        val client = mockHttpClient { request ->
            assertEquals("/api/episode", request.url.encodedPath)
            respondJson(episodePageJson("Pilot"))
        }

        val result = KtorRickAndMortyService(client).getAllEpisodes()

        assertTrue(result is NetworkResult.Success)
        assertEquals("Pilot", (result as NetworkResult.Success).data.result.first().name)
    }

    @Test
    fun getEpisodeByID_returnsSuccess() = runBlocking {
        val client = mockHttpClient { request ->
            assertEquals("/api/episode/10", request.url.encodedPath)
            respondJson(episodeJson(10, "Close Rick-counters of the Rick Kind"))
        }

        val result = KtorRickAndMortyService(client).getEpisodeByID(10)

        assertTrue(result is NetworkResult.Success)
        assertEquals(10, (result as NetworkResult.Success).data.id)
    }

    @Test
    fun getEpisodeByPage_addsPageQuery() = runBlocking {
        val client = mockHttpClient { request ->
            assertEquals("/api/episode", request.url.encodedPath)
            assertEquals("3", request.url.parameters["page"])
            respondJson(episodePageJson("Pickle Rick"))
        }

        val result = KtorRickAndMortyService(client).getEpisodeByPage(3)

        assertTrue(result is NetworkResult.Success)
        assertEquals("Pickle Rick", (result as NetworkResult.Success).data.result.first().name)
    }

    @Test
    fun getListOfEpisodesByIds_formatsIdsInPath() = runBlocking {
        val client = mockHttpClient { request ->
            assertEquals("/api/episode/1,2", request.url.encodedPath)
            respondJson("[${episodeJson(1, "Pilot")},${episodeJson(2, "Lawnmower Dog")}]")
        }

        val result = KtorRickAndMortyService(client).getListOfEpisodesByIds(listOf(1, 2))

        assertTrue(result is NetworkResult.Success)
        assertEquals(2, (result as NetworkResult.Success).data.size)
    }

    @Test
    fun safeApiCall_maps404ToNotFound() = runBlocking {
        val client = mockHttpClient { respondJson("{}", HttpStatusCode.NotFound) }
        val result = KtorRickAndMortyService(client).getCharacterByID(404)
        assertEquals(NetworkResult.Error.BackendError.NotFound, result)
    }

    @Test
    fun safeApiCall_maps429ToTooManyRequests() = runBlocking {
        val client = mockHttpClient { respondJson("{}", HttpStatusCode.TooManyRequests) }
        val result = KtorRickAndMortyService(client).getCharacterByID(429)
        assertEquals(NetworkResult.Error.BackendError.TooManyRequests, result)
    }

    @Test
    fun safeApiCall_maps503ToUnavailable() = runBlocking {
        val client = mockHttpClient { respondJson("{}", HttpStatusCode.ServiceUnavailable) }
        val result = KtorRickAndMortyService(client).getCharacterByID(503)
        assertEquals(NetworkResult.Error.BackendError.Unavailable, result)
    }

    @Test
    fun safeApiCall_mapsOtherBackendErrorsToUnknown() = runBlocking {
        val client = mockHttpClient { respondJson("{}", HttpStatusCode.InternalServerError) }
        val result = KtorRickAndMortyService(client).getCharacterByID(500)
        assertEquals(NetworkResult.Error.BackendError.UnKnown, result)
    }

    @Test
    fun safeApiCall_mapsIOExceptionToOfflineError() = runBlocking {
        val client = mockHttpClient { throw IOException("No internet") }
        val result = KtorRickAndMortyService(client).getCharacterByID(1)
        assertEquals(NetworkResult.Error.OfflineError, result)
    }

    @Test
    fun safeApiCall_mapsUnexpectedExceptionsToUnknown() = runBlocking {
        val client = mockHttpClient { throw IllegalStateException("boom") }
        val result = KtorRickAndMortyService(client).getCharacterByID(1)
        assertEquals(NetworkResult.Error.BackendError.UnKnown, result)
    }

    private fun mockHttpClient(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
    ): HttpClient {
        val engine = MockEngine(
            config = MockEngineConfig().apply {
                requestHandlers.add(handler)
            }
        )
        return HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    private fun MockRequestHandleScope.respondJson(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK
    ) = respond(
        content = body,
        status = status,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    )

    private fun characterPageJson(name: String) =
        """{"info":{"count":1,"pages":1,"next":null,"prev":null},"result":[${characterJson(1, name)}]}"""

    private fun locationPageJson(name: String) =
        """{"info":{"count":1,"pages":1,"next":null,"prev":null},"result":[${locationJson(1, name)}]}"""

    private fun episodePageJson(name: String) =
        """{"info":{"count":1,"pages":1,"next":null,"prev":null},"result":[${episodeJson(1, name)}]}"""

    private fun characterJson(id: Int, name: String) =
        """{"id":$id,"name":"$name","status":"Alive","species":"Human","type":"","gender":"Male","image":"https://example.com/$id.png","origin":{"name":"Earth","url":"https://example.com/origin"},"location":{"name":"Earth","url":"https://example.com/location"},"episode":[]}"""

    private fun locationJson(id: Int, name: String) =
        """{"id":$id,"name":"$name","type":"Planet","dimension":"Dimension C-137","residents":[]}"""

    private fun episodeJson(id: Int, name: String) =
        """{"id":$id,"name":"$name","air_date":"December 2, 2013","episode":"S01E01","characters":[]}"""
}
