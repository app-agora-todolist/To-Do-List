package com.example.agora_beta_20.data.api

import com.example.agora_beta_20.data.model.LoginRequest
import com.example.agora_beta_20.data.model.LoginResponse
import com.example.agora_beta_20.data.model.SignUpRequest
import com.example.agora_beta_20.data.model.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse>

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>


}