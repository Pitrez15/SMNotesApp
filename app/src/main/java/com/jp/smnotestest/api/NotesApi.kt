package com.jp.smnotestest.api

import com.jp.smnotestest.models.Note
import com.jp.smnotestest.models.NotesResponse
import retrofit2.Response
import retrofit2.http.*

interface NotesApi {

    @GET("user/{userId}")
    suspend fun getNotes(@Path("userId") userId: String): Response<NotesResponse>

    @GET("user/completed/{userId}")
    suspend fun getCompletedNotes(@Path("userId") userId: String): Response<NotesResponse>

    @POST("create")
    suspend fun createNote(@Body note: Map<String, String?>): Response<NotesResponse>

    @PUT("complete/{id}")
    suspend fun completeNote(@Path("id") id: Int, @Body body: Map<String, Boolean>): Response<NotesResponse>

    @PUT("softDelete/{id}")
    suspend fun deleteNote(@Path("id") id: Int): Response<NotesResponse>

    @PUT("update/{id}")
    suspend fun updateNote(@Path("id") id: Int, @Body note: Map<String, String?>): Response<NotesResponse>
}