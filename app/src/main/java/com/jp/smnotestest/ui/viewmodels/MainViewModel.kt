package com.jp.smnotestest.ui.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.jp.smnotestest.api.RetrofitInstance
import com.jp.smnotestest.db.NoteDatabase
import com.jp.smnotestest.models.Note
import com.jp.smnotestest.utils.ResultHelper
import com.jp.smnotestest.utils.SessionManager
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    var loading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        storage = Firebase.storage
        storageReference = storage?.reference
    }

    private val _notes = MutableLiveData<ResultHelper<List<Note>>>()
    val notes: LiveData<ResultHelper<List<Note>>> = _notes
    fun getNotes() {
        loading.postValue(true)
        viewModelScope.launch {
            try {
                val userId = SessionManager(context).getUserId()
                if (userId != 0) {
                    getLocalNotes(userId)
                    getRemoteNotes(userId)
                }
            }
            catch (e: Exception) {
                loading.postValue(false)
                //_notes.postValue(ResultHelper.Error("Get notes exception: ${e.message}"))
                Log.d("notes", e.toString())
            }
        }
    }

    private suspend fun getLocalNotes(id: Int) {
        val result = NoteDatabase.invoke(context).getNoteDao().getNotesByUser(id)
        result?.let {
            Log.d("notes", it.toString())
            _notes.postValue(ResultHelper.Success("Cached notes", it))
        }
    }

    private suspend fun getRemoteNotes(id: Int) {
        val response = RetrofitInstance().getNotesApiInstance(context).getNotes(id)
        if (!response.isSuccessful) {
            Log.d("notes", "get notes unsuccessful: ${response.code()}")
            //_notes.postValue(ResultHelper.Error("Notes failed"))
        }
        else {
            NoteDatabase.invoke(context).getNoteDao().addAllNotes(response.body()?.result!!.toList())
            _notes.postValue(ResultHelper.Success("Notes", response.body()?.result))
        }
        loading.postValue(false)
    }

    private val _uploadFileStatus = MutableLiveData<ResultHelper<String>>()
    val uploadFileStatus: LiveData<ResultHelper<String>> = _uploadFileStatus
    fun uploadFile(uri: Uri) {
        loading.postValue(true)
        viewModelScope.launch {
            try {
                val fileRef = storageReference?.child("images/${uri.lastPathSegment}_${System.currentTimeMillis()}")
                val uploadTask = fileRef?.putFile(uri)
                val urlTask = uploadTask?.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        Log.d("notes", "file upload failed: ${task.exception}")
                        _createStatus.postValue(ResultHelper.Error("File upload failed"))
                    }
                    fileRef.downloadUrl
                }?.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.d("notes", "file upload failed: ${task.exception}")
                        _createStatus.postValue(ResultHelper.Error("File upload failed"))
                    }
                    else {
                        Log.d("notes", "file upload: ${task.result}")
                        _uploadFileStatus.postValue(ResultHelper.Success("${task.result}"))
                    }
                    loading.postValue(false)
                }
            }
            catch (e: Exception) {
                loading.postValue(false)
                _createStatus.postValue(ResultHelper.Error("upload file exception: ${e.message}"))
                Log.d("notes", e.toString())
            }
        }
    }
    fun resetUploadFileLiveData(){
        _uploadFileStatus.postValue(ResultHelper.Error("Reset"))
    }

    private val _createStatus = MutableLiveData<ResultHelper<String>>()
    val createStatus: LiveData<ResultHelper<String>> = _createStatus
    fun createNote(title: String, description: String, url: String? = null) {
        loading.postValue(true)
        viewModelScope.launch {
            try {
                val userId = SessionManager(context).getUserId()
                if (userId != 0) {
                    val response = RetrofitInstance().getNotesApiInstance(context).createNote(
                        mapOf("title" to title, "description" to description, "user_id" to userId.toString(), "attachment" to url)
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
                val response = RetrofitInstance().getNotesApiInstance(context).deleteNote(note.id!!)
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
    fun resetDeleteNoteLiveData(){
        _deleteNoteStatus.postValue(ResultHelper.Error("Reset"))
    }

    private suspend fun deleteLocal(id: Int) {
        NoteDatabase.invoke(context).getNoteDao().softDeleteNote(id)
        _notes.value?.data?.find { it -> it.id == id }?.deleted = 1
        _notes.postValue(ResultHelper.Success(
            "Notes list updated",
            _notes.value?.data
        ))
        Log.d("notes", "deleted")
    }

    private val _updateNoteStatus = MutableLiveData<ResultHelper<String>>()
    val updateNoteStatus: LiveData<ResultHelper<String>> = _updateNoteStatus
    fun updateNote(id: Int, title: String, description: String, url: String? = null) {
        loading.postValue(true)
        viewModelScope.launch {
            try {
                val response = RetrofitInstance().getNotesApiInstance(context).updateNote(
                    id,
                    mapOf("title" to title, "description" to description, "attachment" to url)
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
    fun resetUpdateNoteLiveData(){
        _updateNoteStatus.postValue(ResultHelper.Error("Reset"))
    }
}