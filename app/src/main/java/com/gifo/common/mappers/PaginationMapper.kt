package com.gifo.common.mappers

import com.gifo.common.models.domain.PaginationData
import com.gifo.common.models.network.PaginationDataResponse

internal fun PaginationDataResponse?.toDomain(): PaginationData {
    return PaginationData(
        count = this?.count ?: 0,
        offset = this?.offset ?: 0,
        totalCount = this?.totalCount ?: 0,
    )
}