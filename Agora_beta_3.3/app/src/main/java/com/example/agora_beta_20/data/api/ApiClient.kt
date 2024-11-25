package com.example.agora_beta_20.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object ApiClient {

//    private const val BASE_URL = "http://10.0.2.2:8080" // 로컬 서버 주소
    private const val BASE_URL = "http://192.168.56.1:8080" // 로컬 서버 주소

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }

    // ScheduleApiService 초기화
    val scheduleApi: ScheduleApiService by lazy {
        retrofit.create(ScheduleApiService::class.java)
    }
}