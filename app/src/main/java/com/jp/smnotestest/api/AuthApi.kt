package com.jp.smnotestest.api

import com.jp.smnotestest.models.AuthResponse
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @GET("auth/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): Response<AuthResponse>

    @GET("auth/email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body user: Map<String, String>): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): Response<AuthResponse>

    @PUT("auth/softDelete/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<AuthResponse>

    @PUT("auth/update/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: Map<String, String>): Response<AuthResponse>

    @PUT("auth/changePassword/{id}")
    suspend fun changePassword(@Path("id") id: Int, @Body password: Map<String, String>): Response<AuthResponse>
}