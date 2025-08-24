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

val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-API-KEY", BuildConfig.OFFICEHOLDERS_API_KEY)
            .build()
        chain.proceed(request)
    }
    .build()

val json = Json {
    ignoreUnknownKeys = true
}
private val retrofitPeoples = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

interface PeopleApiService {
    @GET("people.geo")
    suspend fun getPeoples(
        @Query("lat") lat: String,
        @Query("lng") long: String): PeoplesData
}

object PeoplesApi {
    val retrofitService: PeopleApiService by lazy {
        retrofitPeoples.create(PeopleApiService::class.java)
    }
}