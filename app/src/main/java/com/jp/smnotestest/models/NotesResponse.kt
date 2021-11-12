package com.jp.smnotestest.models

data class NotesResponse(
    val result: MutableList<Note>?,
    val status: Int,
    val errorCode: Int,
    val extra: ExtraResponse?
)
