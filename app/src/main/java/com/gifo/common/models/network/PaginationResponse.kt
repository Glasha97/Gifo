package com.gifo.common.models.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationResponse<T>(
    @SerialName("pagination")
    val pagination: PaginationDataResponse?,
    @SerialName("data")
    val data: List<T>,
)