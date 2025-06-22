package com.korniiienko.notesapp.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.korniiienko.data.remote.RemoteApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class NetworkManager {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)

            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            })
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $AUTH_TOKEN")
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")

                originalRequest.header("X-Generate-Fails")?.let {
                    requestBuilder.header("X-Generate-Fails", it)
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val apiService: RemoteApiService by lazy {
        retrofit.create(RemoteApiService::class.java)
    }

    companion object {
        private const val BASE_URL = "https://hive.mrdekk.ru/todo/"
        private const val AUTH_TOKEN = "3c5cc32b-d699-4346-b233-567ef8bf7e32"
    }
}