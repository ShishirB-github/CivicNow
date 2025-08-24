package com.example.civicnow.network

import kotlinx.serialization.Serializable

@Serializable
data class ElectionsData(
    val elections: List<Election>,
    val kind: String,
)