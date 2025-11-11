package com.gifo.common.models.domain

data class PaginationData(
    val count: Int,
    val offset: Int,
    val totalCount: Int,
)