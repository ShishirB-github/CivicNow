package com.example.civicnow.network

import kotlinx.serialization.Serializable

@Serializable
data class EventsListData(
    val results: List<EventData>,
    val pagination: Pagination,
)
