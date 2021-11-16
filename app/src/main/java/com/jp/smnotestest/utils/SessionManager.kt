package com.jp.smnotestest.utils

import android.content.Context
import com.jp.smnotestest.R
import com.jp.smnotestest.models.ExtraResponse

class SessionManager(context: Context) {
    private val sp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "token"
        const val USER_ID = "userId"
        const val USER_EMAIL = "email"
        const val USER_EXPIRES_IN = "expiresIn"
    }

    fun saveSession(extra: ExtraResponse) {
        val editor = sp.edit()
        editor.putString(USER_TOKEN, extra.token)
        editor.putInt(USER_ID, extra.userId!!)
        editor.putString(USER_EMAIL, extra.email)
        editor.putLong(USER_EXPIRES_IN, extra.expiresIn!!)
        editor.apply()
    }

    fun getToken(): String? {
        return sp.getString(USER_TOKEN, null)
    }

    fun getExpiresIn(): Long {
        return sp.getLong(USER_EXPIRES_IN, 0)
    }

    fun getUserId(): Int {
        return sp.getInt(USER_ID, 0)
    }

    fun finishSession() {
        val editor = sp.edit()
        editor.clear().apply()
    }
}