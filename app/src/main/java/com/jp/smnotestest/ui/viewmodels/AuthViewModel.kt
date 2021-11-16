package com.jp.smnotestest.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.jp.smnotestest.api.RetrofitInstance
import com.jp.smnotestest.utils.ResultHelper
import com.jp.smnotestest.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel(application: Application): AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    var loading: MutableLiveData<Boolean> = MutableLiveData()

    private val _loggedInStatus = MutableLiveData<ResultHelper<String>>()
    val loggedInStatus: LiveData<ResultHelper<String>> = _loggedInStatus
    fun verifyLoggedIn() {
        loading.postValue(true)
        viewModelScope.launch {
            delay(2000L)
            try {
                val token = SessionManager(context).getToken()
                if (token.isNullOrEmpty()) {
                    Log.d("auth", "user not logged")
                    _loggedInStatus.postValue(ResultHelper.Error("User Not Logged"))
                }
                else {
                    val expiresIn = SessionManager(context).getExpiresIn()
                    if (expiresIn == 0L || expiresIn <= System.currentTimeMillis()) {
                        Log.d("auth", "user not logged")
                        _loggedInStatus.postValue(ResultHelper.Error("User Not Logged"))
                    }
                    else {
                        Log.d("auth", "user logged")
                        _loggedInStatus.postValue(ResultHelper.Success("User Logged"))
                    }
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
                val response = RetrofitInstance().getAuthApiInstance(context).login(
                    mapOf("email" to email, "password" to password)
                )
                if (!response.isSuccessful) {
                    Log.d("auth", "login Unsuccessful: ${response.body()?.errorCode}")
                    _signInStatus.postValue(ResultHelper.Error("Login Failed with ${response.body()?.errorCode}"))
                }
                else {
                    _signInStatus.postValue(ResultHelper.Success("Login Successful"))
                    SessionManager(context).saveSession(
                        response.body()?.extra!!
                    )
                }
                loading.postValue(false)
            }
            catch (e:Exception) {
                loading.postValue(false)
                _signInStatus.postValue(ResultHelper.Error("Login Failed with ${e.message}"))
                Log.d("auth", "login Unsuccessful: $e")
            }
        }
    }
    fun resetSignInLiveData(){
        _signInStatus.postValue(ResultHelper.Error("Reset"))
    }

    private val _registrationStatus = MutableLiveData<ResultHelper<String>>()
    val registrationStatus: LiveData<ResultHelper<String>> = _registrationStatus
    fun register(email: String, password: String, name: String) {
        loading.postValue(true)
        viewModelScope.launch() {
            try {
                val response = RetrofitInstance().getAuthApiInstance(context).register(
                    mapOf("email" to email, "password" to password, "name" to name)
                )
                if (!response.isSuccessful) {
                    Log.d("auth", "Registration Unsuccessful: ${response.body()?.errorCode}")
                    _registrationStatus.postValue(ResultHelper.Error("Registration Failed with ${response.body()?.errorCode}"))
                }
                else {
                    _registrationStatus.postValue(ResultHelper.Success("User Created"))
                }
                loading.postValue(false)
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
                SessionManager(context).finishSession()
                _signOutStatus.postValue(ResultHelper.Success("SignOut Successful"))
                loading.postValue(false)
            }
            catch (e:Exception){
                loading.postValue(false)
                _signOutStatus.postValue(ResultHelper.Error("SignOut Failed with ${e.message}"))
                Log.d("auth", "SignOut Unsuccessful: ${e.message}")
            }
        }
    }

}