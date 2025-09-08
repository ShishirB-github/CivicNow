package com.example.civicnow.network

import com.example.civicnow.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://v3.openstates.org/"

private val retrofitEvents = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

interface EventsApiService {
    @GET("events")
    suspend fun getEvents(@Query("jurisdiction") jurisdiction: String): EventsListData
}

object EventsApi {
    val retrofitService: EventsApiService by lazy {
        retrofitEvents.create(EventsApiService::class.java)
    }
}