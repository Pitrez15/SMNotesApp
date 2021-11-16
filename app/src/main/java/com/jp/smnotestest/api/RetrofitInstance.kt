package com.jp.smnotestest.api

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jp.smnotestest.utils.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    private lateinit var notesApi: NotesApi
    private lateinit var authApi: AuthApi

    fun getNotesApiInstance(context: Context): NotesApi {
        if (!::notesApi.isInitialized) {
            val retrofit = Retrofit.Builder()
                .client(getClient(context))
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            notesApi = retrofit.create(NotesApi::class.java)
        }
        return notesApi
    }

    fun getAuthApiInstance(context: Context): AuthApi {
        if (!::authApi.isInitialized) {
            val retrofit = Retrofit.Builder()
                .client(getClient(context))
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            authApi = retrofit.create(AuthApi::class.java)
        }
        return authApi
    }

    private fun getClient(context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(logging)
            .build()
    }
}