package com.jp.smnotestest.utils

import com.jp.smnotestest.models.Note

interface NoteListener {
    fun onItemClicked(item: Note)
    fun onChangeItemCompleteSwitchClicked(item: Note)
    fun onDeleteNoteClicked(item: Note)
}