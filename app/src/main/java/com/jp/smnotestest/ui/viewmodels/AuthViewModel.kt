package com.jp.smnotestest.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    var currentUser: MutableLiveData<FirebaseUser> = MutableLiveData()
    private val firebaseAuth = Firebase.auth

    init {
        verifyLoggedIn()
    }

    private fun verifyLoggedIn() = viewModelScope.launch {
        delay(2000L)
        currentUser.postValue(firebaseAuth.currentUser)
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("firebase", "login Successful")
                currentUser.postValue(firebaseAuth.currentUser)
            }
            else {
                Log.d("firebase", "login Unsuccessful")
            }
        }
    }

    fun register(email: String, password: String) = viewModelScope.launch {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("firebase", "register Successful")
                currentUser.postValue(firebaseAuth.currentUser)
            }
            else {
                Log.d("firebase", "register Unsuccessful")
            }
        }
    }

    fun logout() = viewModelScope.launch {
        firebaseAuth.signOut()
        currentUser.postValue(null)
    }
}