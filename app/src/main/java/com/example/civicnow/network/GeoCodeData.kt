package com.example.civicnow.network
import kotlinx.serialization.Serializable

@Serializable
data class GeoCodeData(
    val items: List<Item>,
)

@Serializable
data class Item(
    val title: String,
    val id: String,
    val resultType: String,
    val localityType: String,
    val address: Address,
    val position: Position,
    val mapView: MapView,
    val scoring: Scoring,
)

@Serializable
data class Address(
    val label: String,
    val countryCode: String,
    val countryName: String,
    val stateCode: String,
    val state: String,
    val county: String,
    val city: String,
    val postalCode: String,
)

@Serializable
data class Position(
    val lat: Double,
    val lng: Double,
)

@Serializable
data class MapView(
    val west: Double,
    val south: Double,
    val east: Double,
    val north: Double,
)

@Serializable
data class Scoring(
    val queryScore: Double,
    val fieldScore: FieldScore,
)

@Serializable
data class FieldScore(
    val country: Double,
    val postalCode: Double,
)
