package com.jp.smnotestest.api

import android.content.Context
import com.jp.smnotestest.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context): Interceptor {

    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        sessionManager.getToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(requestBuilder.build())
    }
}