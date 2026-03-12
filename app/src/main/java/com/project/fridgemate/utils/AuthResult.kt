package com.project.fridgemate.utils

sealed class AuthResult {
    data object Idle : AuthResult()
    data object Loading : AuthResult()
    data object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}
