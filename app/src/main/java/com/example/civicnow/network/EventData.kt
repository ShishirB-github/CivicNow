package com.example.civicnow.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventData (
    val id: String,
    val name: String,
    val jurisdiction: EventJurisdiction,
    val description: String,
    val classification: String,
    @SerialName("start_date")
    val startDate: String,
    @SerialName("end_date")
    val endDate: String,
    @SerialName("all_day")
    val allDay: Boolean,
    val status: String,
    @SerialName("upstream_id")
    val upstreamId: String,
    val deleted: Boolean,
    val location: Location,
)

@Serializable
data class EventJurisdiction(
    val id: String,
    val name: String,
    val classification: String,
)

@Serializable
data class Location(
    val name: String,
    val url: String,
)

