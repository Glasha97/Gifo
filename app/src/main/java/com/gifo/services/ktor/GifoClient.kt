package com.gifo.services.ktor

import android.util.Log
import com.gifo.BuildConfig
import com.gifo.common.error.GifoError
import com.gifo.common.models.network.ErrorNetwork
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class GifoClient {

    private val json = Json { ignoreUnknownKeys = true }
    private val client = HttpClient(CIO) {
        if (BuildConfig.DEBUG) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("KtorClient", message)
                    }
                }
                level = LogLevel.ALL
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 20_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 20_000
        }

        install(ContentNegotiation)
        {
            json(
                json = Json {
                    useAlternativeNames = false
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                },
                contentType = ContentType.Application.Json,
            )
        }

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.giphy.com"
                parameters.append("limit", "25")
                parameters.append("region", java.util.Locale.getDefault().country)
                parameters.append("api_key", BuildConfig.GIPHY_API_KEY)
            }
        }

        HttpResponseValidator {
            validateResponse { response ->
                when (response.status.value) {
                    in 400..599 -> {
                        val errorBody = runCatching { response.bodyAsText() }.getOrNull()

                        val error = runCatching {
                            json.decodeFromString<ErrorNetwork>(errorBody ?: "").meta
                        }.getOrNull()

                        throw error?.let {
                            GifoError.Network(
                                message = it.msg ?: return@let GifoError.Unknown,
                                code = it.status ?: return@let GifoError.Unknown,
                            )
                        } ?: GifoError.Unknown
                    }
                }
            }
        }
    }

    suspend fun get(
        path: String,
        queryParams: Map<String, String>? = null,
    ): HttpResponse {
        return client.get(path) {
            url {
                queryParams?.forEach { (key, value) -> parameters.append(key, value) }
            }
        }
    }
}