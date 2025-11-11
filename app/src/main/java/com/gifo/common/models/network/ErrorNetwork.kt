package com.gifo.common.models.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ErrorNetwork(
    @SerialName("meta")
    val meta: Data?
) {

    @Serializable
    data class Data(
        @SerialName("msg")
        val msg: String?,
        @SerialName("status")
        val status: Int?,
    )
}