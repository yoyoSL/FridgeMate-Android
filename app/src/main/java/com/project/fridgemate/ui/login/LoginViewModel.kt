package com.project.fridgemate.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginStatus = MutableLiveData<String?>()
    val loginStatus: LiveData<String?> = _loginStatus

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginStatus.value = "Please fill in all fields"
            return
        }

        _isLoading.value = true
        // Logic for Firebase Authentication will go here
        // For now, simulating a delay
        _loginStatus.value = null
    }

    fun clearStatus() {
        _loginStatus.value = null
    }
}
