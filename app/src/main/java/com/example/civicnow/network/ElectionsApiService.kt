package com.example.civicnow.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL =
    "https://www.googleapis.com"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface ElectionsApiService {
    @GET("civicinfo/v2/elections")
    suspend fun getElections(@Query("key") apiKey: String): ElectionsData
}

object ElectionsApi {
    val retrofitService: ElectionsApiService by lazy {
        retrofit.create(ElectionsApiService::class.java)
    }
}