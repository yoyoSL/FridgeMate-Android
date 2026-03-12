package com.project.fridgemate.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.fridgemate.data.repository.AuthRepository
import com.project.fridgemate.utils.AuthResult
import com.project.fridgemate.utils.AuthValidator
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _sendCodeResult = MutableLiveData<AuthResult>(AuthResult.Idle)
    val sendCodeResult: LiveData<AuthResult> = _sendCodeResult

    private val _resetResult = MutableLiveData<AuthResult>(AuthResult.Idle)
    val resetResult: LiveData<AuthResult> = _resetResult

    private val _validationResult = MutableLiveData<AuthValidator.ValidationResult>()
    val validationResult: LiveData<AuthValidator.ValidationResult> = _validationResult

    private var _email: String = ""
    val email: String get() = _email

    private val _isCodeSent = MutableLiveData(false)
    val isCodeSent: LiveData<Boolean> = _isCodeSent

    fun sendResetCode(email: String) {
        val validation = AuthValidator.validateForgotPassword(email)
        _validationResult.value = validation

        if (!validation.isValid) return

        _email = email.trim()
        _sendCodeResult.value = AuthResult.Loading

        viewModelScope.launch {
            val result = repository.forgotPassword(_email)
            _sendCodeResult.value = result
            if (result is AuthResult.Success) {
                _isCodeSent.value = true
            }
        }
    }

    fun resetPassword(code: String, newPassword: String, confirmPassword: String) {
        val validation = AuthValidator.validateResetPassword(code, newPassword, confirmPassword)
        _validationResult.value = validation

        if (!validation.isValid) return

        _resetResult.value = AuthResult.Loading

        viewModelScope.launch {
            _resetResult.value = repository.resetPassword(_email, code.trim(), newPassword)
        }
    }

    fun resetSendCodeResult() {
        _sendCodeResult.value = AuthResult.Idle
    }

    fun resetResetResult() {
        _resetResult.value = AuthResult.Idle
    }
}
