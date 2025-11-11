package com.gifo.common.models.domain

data class Pagination<T>(
    val pagination: PaginationData,
    val data: List<T>,
)