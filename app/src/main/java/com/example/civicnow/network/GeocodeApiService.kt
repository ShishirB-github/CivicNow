package com.example.civicnow.network

import com.example.civicnow.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://geocode.search.hereapi.com/"

private val geoCodeJson = Json {
    ignoreUnknownKeys = true
}
private val retrofitGeocode = Retrofit.Builder()
    .addConverterFactory(geoCodeJson.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface GeocodeApiService {
    @GET("v1/geocode")
    suspend fun getLatLong(
        @Query("q") address: String,
        @Query("apiKey") apiKey: String): GeoCodeData
}

object GeocodeApi {
    val retrofitService: GeocodeApiService by lazy {
        retrofitGeocode.create(GeocodeApiService::class.java)
    }
}