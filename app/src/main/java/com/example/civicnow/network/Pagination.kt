package com.example.civicnow.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pagination(
    @SerialName("per_page")
    val perPage: Long,
    val page: Long,
    @SerialName("max_page")
    val maxPage: Long,
    @SerialName("total_items")
    val totalItems: Long,
)
