package com.jp.smnotestest.models

data class AuthResponse(
    val result: User?,
    val status: Int,
    val errorCode: Int,
    val extra: ExtraResponse?
)
