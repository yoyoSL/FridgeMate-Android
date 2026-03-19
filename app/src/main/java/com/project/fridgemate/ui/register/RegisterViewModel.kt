package com.project.fridgemate.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.fridgemate.data.repository.AuthRepository
import com.project.fridgemate.utils.AuthResult
import com.project.fridgemate.utils.AuthValidator
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _registerResult = MutableLiveData<AuthResult>(AuthResult.Idle)
    val registerResult: LiveData<AuthResult> = _registerResult

    private val _validationResult = MutableLiveData<AuthValidator.ValidationResult>()
    val validationResult: LiveData<AuthValidator.ValidationResult> = _validationResult

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        val validation = AuthValidator.validateRegistration(name, email, password, confirmPassword)
        _validationResult.value = validation

        if (!validation.isValid) return

        _registerResult.value = AuthResult.Loading

        viewModelScope.launch {
            _registerResult.value = repository.register(name, email, password)
        }
    }

    fun resetRegisterResult() {
        _registerResult.value = AuthResult.Idle
    }

    fun clearState() {
        _registerResult.value = AuthResult.Idle
    }
}
