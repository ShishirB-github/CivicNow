package com.example.civicnow.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Officeholder(
    val id: String,
    val name: String,
    val party: String,
    @SerialName("current_role")
    val currentRole: CurrentRole,
    val jurisdiction: Jurisdiction,
    @SerialName("given_name")
    val givenName: String,
    @SerialName("family_name")
    val familyName: String,
    val image: String,
    val email: String,
    val gender: String,
    @SerialName("birth_date")
    val birthDate: String,
    @SerialName("death_date")
    val deathDate: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("openstates_url")
    val openstatesUrl: String,
)
