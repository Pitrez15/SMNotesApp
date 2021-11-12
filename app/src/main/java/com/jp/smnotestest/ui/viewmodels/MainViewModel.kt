package com.jp.smnotestest.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jp.smnotestest.api.RetrofitInstance
import com.jp.smnotestest.models.Note
import com.jp.smnotestest.models.NotesResponse
import kotlinx.coroutines.launch
import retrofit2.http.Body

class MainViewModel : ViewModel() {

    val notes: MutableLiveData<List<Note>> = MutableLiveData()
    private var notesResponse: NotesResponse? = null

    val notesCompleted: MutableLiveData<List<Note>> = MutableLiveData()
    private var notesCompletedResponse: NotesResponse? = null

    private val firebaseAuth = Firebase.auth
    private val uid = firebaseAuth.currentUser?.uid!!

    fun getNotes() = viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.getNotes(uid)
            if (response.isSuccessful) {
                response.body()?.let {
                    notesResponse = it
                    notes.postValue(notesResponse?.result)
                }
            }
        }
        catch (e: Exception) {
            Log.d("exception", e.toString())
        }
    }

    fun getCompletedNotes() = viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.getCompletedNotes(uid)
            if (response.isSuccessful) {
                response.body()?.let {
                    notesCompletedResponse = it
                    notesCompleted.postValue(notesCompletedResponse?.result)
                }
            }
        }
        catch (e: Exception) {
            Log.d("exception", e.toString())
        }
    }

    fun createNote(title: String, description: String) = viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.createNote(
                mapOf("title" to title, "description" to description, "user_id" to uid)
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    notesResponse?.result?.add(it.result!![0])
                    notes.postValue(notesResponse?.result)
                }
            }
        }
        catch (e: Exception) {
            Log.d("exception", e.toString())
        }
    }

    fun completeNote(note: Note) = viewModelScope.launch {
        if (note.completed == 0) {
            try {
                val response = RetrofitInstance.api.completeNote(note.id!!, mapOf("bool" to true))
                if (response.isSuccessful) {
                    response.body()?.let { _ ->
                        notesResponse?.result?.find { it -> it.id == note.id }?.completed = 1
                        notes.postValue(notesResponse?.result)
                    }
                }
            }
            catch (e: Exception) {
                Log.d("exception", e.toString())
            }
        }
        else {
            try {
                val response = RetrofitInstance.api.completeNote(note.id!!, mapOf("bool" to false))
                if (response.isSuccessful) {
                    response.body()?.let { _ ->
                        notesResponse?.result?.find { it -> it.id == note.id }?.completed = 0
                        notes.postValue(notesResponse?.result)
                    }
                }
            }
            catch (e: Exception) {
                Log.d("exception", e.toString())
            }
        }
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.deleteNote(note.id!!)
            if (response.isSuccessful) {
                response.body()?.let { _ ->
                    notesResponse?.result?.find { it -> it.id == note.id }?.deleted = 1
                    notes.postValue(notesResponse?.result)
                }
            }
        }
        catch (e: Exception) {
            Log.d("exception", e.toString())
        }
    }

    fun updateNote(id: Int, title: String, description: String) = viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.updateNote(id, mapOf("title" to title, "description" to description))
            if (response.isSuccessful) {
                notesResponse?.result?.find { it -> it.id == id }?.let {
                    it.title = title
                    it.description = description
                }
                notes.postValue(notesResponse?.result)
            }
        }
        catch (e: Exception) {
            Log.d("exception", e.toString())
        }
    }
}