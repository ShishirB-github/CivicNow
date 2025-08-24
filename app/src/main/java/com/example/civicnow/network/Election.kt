package com.example.civicnow.network

import kotlinx.serialization.Serializable

@Serializable
data class Election(
    val id: String,
    val name: String,
    val electionDay: String,
    val ocdDivisionId: String,
)