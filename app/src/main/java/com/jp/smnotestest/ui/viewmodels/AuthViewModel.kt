package com.jp.smnotestest.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jp.smnotestest.utils.ResultHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {

    private var firebaseAuth: FirebaseAuth? = null
    var loading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        firebaseAuth = Firebase.auth
    }

    private val _loggedInStatus = MutableLiveData<ResultHelper<String>>()
    val loggedInStatus: LiveData<ResultHelper<String>> = _loggedInStatus
    fun verifyLoggedIn() {
        loading.postValue(true)
        viewModelScope.launch {
            delay(2000L)
            try {
                firebaseAuth?.currentUser?.let {
                    Log.d("auth", "user logged")
                    _loggedInStatus.postValue(ResultHelper.Success("User Logged"))
                }?:run {
                    Log.d("auth", "user not logged")
                    _loggedInStatus.postValue(ResultHelper.Error("User Not Logged"))
                }
                loading.postValue(false)
            }
            catch (e:Exception) {
                loading.postValue(false)
                _signInStatus.postValue(ResultHelper.Error("Verification Failed with ${e.message}"))
                Log.d("auth", "Verification Unsuccessful: ${e.message}")
            }
        }
    }

    private val _signInStatus = MutableLiveData<ResultHelper<String>>()
    val signInStatus: LiveData<ResultHelper<String>> = _signInStatus
    fun login(email: String, password: String) {
        loading.postValue(true)
        viewModelScope.launch() {
            try {
                firebaseAuth?.let { login->
                    login.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener {
                            if (!it.isSuccessful) {
                                Log.d("auth", "login Unsuccessful: ${it.exception}")
                                _signInStatus.postValue(ResultHelper.Error("Login Failed with ${it.exception}"))
                            }
                            else {
                                _signInStatus.postValue(ResultHelper.Success("Login Successful"))
                            }
                            loading.postValue(false)
                        }
                }
            }
            catch (e:Exception) {
                loading.postValue(false)
                _signInStatus.postValue(ResultHelper.Error("Login Failed with ${e.message}"))
                Log.d("auth", "login Unsuccessful: ${e.message}")
            }
        }
    }
    fun resetSignInLiveData(){
        _signInStatus.postValue(ResultHelper.Error("Reset"))
    }

    private val _registrationStatus = MutableLiveData<ResultHelper<String>>()
    val registrationStatus: LiveData<ResultHelper<String>> = _registrationStatus
    fun register(email: String, password: String) {
        loading.postValue(true)
        viewModelScope.launch() {
            try {
                firebaseAuth?.let { authentication ->
                    authentication.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener {
                            if (!it.isSuccessful) {
                                Log.d("auth", "Registration Unsuccessful: ${it.exception}")
                                _registrationStatus.postValue(ResultHelper.Error("Registration Failed with ${it.exception}"))
                            }
                            else {
                                _registrationStatus.postValue(ResultHelper.Success("User Created"))
                            }
                            loading.postValue(false)
                        }
                }
            }
            catch (e:Exception) {
                loading.postValue(false)
                _registrationStatus.postValue(ResultHelper.Error("Registration Failed with ${e.message}"))
                Log.d("auth", "Registration Unsuccessful: ${e.message}")
            }
        }
    }

    private val _signOutStatus = MutableLiveData<ResultHelper<String>>()
    val signOutStatus: LiveData<ResultHelper<String>> = _signOutStatus
    fun signOut() {
        loading.postValue(true)
        viewModelScope.launch() {
            try {
                firebaseAuth?.let {authentication ->
                    authentication.signOut()
                    _signOutStatus.postValue(ResultHelper.Success("SignOut Successful"))
                    loading.postValue(false)
                }

            }
            catch (e:Exception){
                loading.postValue(false)
                _signOutStatus.postValue(ResultHelper.Error("SignOut Failed with ${e.message}"))
                Log.d("auth", "SignOut Unsuccessful: ${e.message}")
            }
        }
    }

}