package com.gifo.common.models.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationDataResponse(
    @SerialName("count")
    val count: Int?,
    @SerialName("offset")
    val offset: Int?,
    @SerialName("total_count")
    val totalCount: Int?,
)