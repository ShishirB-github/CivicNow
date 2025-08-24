package com.example.civicnow.network

import kotlinx.serialization.Serializable

@Serializable
data class PeoplesData(
    val results: List<Officeholder>,
    val pagination: Pagination,
)


