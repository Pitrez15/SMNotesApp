package com.jp.smnotestest.utils

sealed class ResultHelper<T>(
    val message: String? = null,
    val data: T? = null,
) {
    class Success<T>(message: String, data: T? = null) : ResultHelper<T>(message, data)
    class Error<T>(message: String, data: T? = null) : ResultHelper<T>(message, data)
}