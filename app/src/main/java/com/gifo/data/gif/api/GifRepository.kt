package com.gifo.data.gif.api

import com.gifo.common.models.domain.Pagination

internal interface GifRepository {

    suspend fun search(query: String, offset: Int = 0, pageSize: Int): Pagination<Gif>

    suspend fun trending(offset: Int = 0, pageSize: Int): Pagination<Gif>
}