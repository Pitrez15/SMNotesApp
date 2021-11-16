package com.jp.smnotestest.api

import com.jp.smnotestest.models.NotesResponse
import retrofit2.Response
import retrofit2.http.*

interface NotesApi {

    @GET("notes/user/{userId}")
    suspend fun getNotes(@Path("userId") userId: Int): Response<NotesResponse>

    @GET("notes/user/completed/{userId}")
    suspend fun getCompletedNotes(@Path("userId") userId: Int): Response<NotesResponse>

    @POST("notes/create")
    suspend fun createNote(@Body note: Map<String, String?>): Response<NotesResponse>

    @PUT("notes/complete/{id}")
    suspend fun completeNote(@Path("id") id: Int, @Body body: Map<String, Boolean>): Response<NotesResponse>

    @PUT("notes/softDelete/{id}")
    suspend fun deleteNote(@Path("id") id: Int): Response<NotesResponse>

    @PUT("notes/update/{id}")
    suspend fun updateNote(@Path("id") id: Int, @Body note: Map<String, String?>): Response<NotesResponse>
}