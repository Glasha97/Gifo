package com.gifo.data.gif

import com.gifo.common.models.network.PaginationResponse
import com.gifo.services.ktor.GifoClient
import io.ktor.client.call.body
import org.koin.core.annotation.Single

@Single
internal class GifRemoteDataSource(private val gifoClient: GifoClient) {

    suspend fun search(query: String, offset: Int, pageSize: Int): PaginationResponse<GifResponse> {
        return gifoClient.get(
            path = "v1/gifs/search",
            queryParams = mapOf("q" to query, "offset" to offset.toString(), "limit" to pageSize.toString())
        ).body()
    }

    suspend fun trending(offset: Int, pageSize: Int): PaginationResponse<GifResponse> {
        return gifoClient.get(
            path = "v1/gifs/trending",
            queryParams = mapOf("offset" to offset.toString(), "limit" to pageSize.toString())
        ).body()
    }
}