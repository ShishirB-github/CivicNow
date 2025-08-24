package com.example.civicnow.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Extras(
    @SerialName("P.O. Box")
    val pOBox: String?,
)
