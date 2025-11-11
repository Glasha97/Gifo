package com.gifo.data.gif

import com.gifo.common.mappers.toDomain
import com.gifo.common.models.domain.Pagination
import com.gifo.data.gif.api.Gif
import com.gifo.data.gif.api.GifRepository
import org.koin.core.annotation.Single

@Single
internal class GifRepositoryImpl(private val dataSource: GifRemoteDataSource) : GifRepository {
    override suspend fun search(query: String, offset: Int, pageSize: Int): Pagination<Gif> {
        val result = dataSource.search(query, offset, pageSize)
        return Pagination(
            pagination = result.pagination.toDomain(),
            data = result.data.mapNotNull { it.toDomain() },
        )
    }

    override suspend fun trending(offset: Int, pageSize: Int): Pagination<Gif> {
        val result = dataSource.trending(offset, pageSize)
        return Pagination(
            pagination = result.pagination.toDomain(),
            data = result.data.mapNotNull { it.toDomain() },
        )
    }
}
