package com.jp.smnotestest.models

data class ExtraResponse(
    val fieldCount: Int?,
    val affectedRows: Int?,
    val insertId: Int?,
    val info: String?,
    val serverStatus: Int?,
    val warningStatus: Int?,
    val changedRows: Int?,
    val token: String?,
    val userId: Int?,
    val email: String?,
    val expiresIn: Long?
)