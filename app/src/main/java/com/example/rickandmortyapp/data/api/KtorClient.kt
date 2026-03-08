package com.example.rickandmortyapp.data.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber

object KtorClient {

    val httpClient = HttpClient(OkHttp) {
        expectSuccess = true
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Timber.tag("HTTP_LOG").d(message)
                }
            }
            level = LogLevel.BODY
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })

        }
    }

}
