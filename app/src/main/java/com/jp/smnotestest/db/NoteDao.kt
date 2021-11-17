package com.jp.smnotestest.db

import androidx.room.*
import com.jp.smnotestest.models.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM note WHERE userId = :id AND deleted = 0")
    suspend fun getNotesByUser(id: Int): List<Note>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllNotes(note: List<Note>)

    @Query("UPDATE note SET deleted = 1 WHERE id = :id")
    suspend fun softDeleteNote(id: Int)
}