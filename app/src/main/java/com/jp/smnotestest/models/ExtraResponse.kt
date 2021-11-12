package com.jp.smnotestest.models

data class ExtraResponse(
    val fieldCount: Int?,
    val affectedRows: Int?,
    val insertId: Int?,
    val info: String?,
    val serverStatus: Int?,
    val warningStatus: Int?,
    val changedRows: Int?
)