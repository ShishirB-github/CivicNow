package com.example.civicnow.network

import kotlinx.serialization.Serializable

@Serializable
data class Jurisdiction(
    val id: String,
    val name: String,
    val classification: String,
)
