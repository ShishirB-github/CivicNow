package com.example.civicnow.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentRole(
    val title: String,
    @SerialName("org_classification")
    val orgClassification: String,
    val district: String,
    @SerialName("division_id")
    val divisionId: String,
)

