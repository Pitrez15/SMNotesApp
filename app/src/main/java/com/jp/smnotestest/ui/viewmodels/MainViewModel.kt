package com.jp.smnotestest.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jp.smnotestest.api.RetrofitInstance
import com.jp.smnotestest.models.Note
import com.jp.smnotestest.models.NotesResponse
import com.jp.smnotestest.utils.ResultHelper
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private var firebaseAuth: FirebaseAuth? = null
    private var uid: String? = null

    var loading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        firebaseAuth = Firebase.auth
        uid = firebaseAuth?.uid
    }

    private val _notes = MutableLiveData<ResultHelper<List<Note>>>()
    val notes: LiveData<ResultHelper<List<Note>>> = _notes
    fun getNotes() {
        loading.postValue(true)
        viewModelScope.launch {
            try {
                uid?.let {
                    val response = RetrofitInstance.api.getNotes(it)
                    if (!response.isSuccessful) {
                        Log.d("notes", "get notes unsuccessful: ${response.code()}")
                        _notes.postValue(ResultHelper.Error("Notes failed"))
                    }
                    else {
                        _notes.postValue(ResultHelper.Success("Notes", response.body()?.result))
                    }
                    loading.postValue(false)
                }
            }
            catch (e: Exception) {
                loading.postValue(false)
                _notes.postValue(ResultHelper.Error("Get notes exception: ${e.message}"))
                Log.d("notes", e.toString())
            }
        }
    }

    private val _createStatus = MutableLiveData<ResultHelper<String>>()
    val createStatus: LiveData<ResultHelper<String>> = _createStatus
    fun createNote(title: String, description: String) {
        loading.postValue(true)
        viewModelScope.launch {
            try {
                uid?.let {
                    val response = RetrofitInstance.api.createNote(
                        mapOf("title" to title, "description" to description, "user_id" to it)
                    )
                    if (!response.isSuccessful) {
                        Log.d("notes", "create note unsuccessful: ${response.code()}")
                        _createStatus.postValue(ResultHelper.Error("Create Note Failed"))
                    }
                    else {
                        _createStatus.postValue(ResultHelper.Success("Note Created"))
                    }
                    loading.postValue(false)
                }
            }
            catch (e: Exception) {
                loading.postValue(false)
                _createStatus.postValue(ResultHelper.Error("Create note exception: ${e.message}"))
                Log.d("notes", e.toString())
            }
        }
    }

    private val _deleteNoteStatus = MutableLiveData<ResultHelper<String>>()
    val deleteNoteStatus: LiveData<ResultHelper<String>> = _deleteNoteStatus
    fun deleteNote(note: Note) {
        loading.postValue(true)
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteNote(note.id!!)
                if (!response.isSuccessful) {
                    Log.d("notes", "delete note unsuccessful: ${response.code()}")
                    _deleteNoteStatus.postValue(ResultHelper.Error("Delete Note Failed"))
                }
                else {
                    _deleteNoteStatus.postValue(ResultHelper.Success("Note Deleted"))
                    _notes.value?.data?.find { it -> it.id == note.id }?.deleted = 1
                    _notes.postValue(ResultHelper.Success(
                        "Notes list updated",
                        _notes.value?.data
                    ))
                }
                loading.postValue(false)
            }
            catch (e: Exception) {
                loading.postValue(false)
                _deleteNoteStatus.postValue(ResultHelper.Error("Delete note exception: ${e.message}"))
                Log.d("notes", e.toString())
            }
        }
    }

    private val _updateNoteStatus = MutableLiveData<ResultHelper<String>>()
    val updateNoteStatus: LiveData<ResultHelper<String>> = _updateNoteStatus
    fun updateNote(id: Int, title: String, description: String) {
        loading.postValue(true)
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.updateNote(
                    id,
                    mapOf("title" to title, "description" to description)
                )
                if (!response.isSuccessful) {
                    Log.d("notes", "update note unsuccessful: ${response.code()}")
                    _updateNoteStatus.postValue(ResultHelper.Error("Delete Note Failed"))
                }
                else {
                    _updateNoteStatus.postValue(ResultHelper.Success("Note Updated"))
                }
                loading.postValue(false)
            }
            catch (e: Exception) {
                loading.postValue(false)
                _updateNoteStatus.postValue(ResultHelper.Error("Update note exception: ${e.message}"))
                Log.d("notes", e.toString())
            }
        }
    }

    /*val notesCompleted: MutableLiveData<List<Note>> = MutableLiveData()
    private var notesCompletedResponse: NotesResponse? = null*/

    /*fun getCompletedNotes() = viewModelScope.launch {
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
    }*/

    /*fun completeNote(note: Note) = viewModelScope.launch {
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
}*/
}