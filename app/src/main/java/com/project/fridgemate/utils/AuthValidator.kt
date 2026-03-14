package com.project.fridgemate.utils

import android.util.Patterns

object AuthValidator {

    private const val MIN_PASSWORD_LENGTH = 6

    data class ValidationResult(
        val isValid: Boolean,
        val emailError: String? = null,
        val passwordError: String? = null,
        val confirmPasswordError: String? = null,
        val nameError: String? = null
    )

    fun validateLogin(email: String, password: String): ValidationResult {
        val emailErr = validateEmail(email)
        val passwordErr = if (password.isBlank()) "Please enter your password" else null

        return ValidationResult(
            isValid = emailErr == null && passwordErr == null,
            emailError = emailErr,
            passwordError = passwordErr
        )
    }

    fun validateRegistration(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        val nameErr = if (name.isBlank()) "Please enter your full name" else null
        val emailErr = validateEmail(email)
        val passwordErr = validatePassword(password)
        val confirmErr = when {
            confirmPassword.isBlank() -> "Please confirm your password"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }

        return ValidationResult(
            isValid = nameErr == null && emailErr == null && passwordErr == null && confirmErr == null,
            nameError = nameErr,
            emailError = emailErr,
            passwordError = passwordErr,
            confirmPasswordError = confirmErr
        )
    }

    fun validateForgotPassword(email: String): ValidationResult {
        val emailErr = validateEmail(email)
        return ValidationResult(
            isValid = emailErr == null,
            emailError = emailErr
        )
    }

    fun validateResetPassword(code: String, password: String, confirmPassword: String): ValidationResult {
        val passwordErr = validatePassword(password)
        val confirmErr = when {
            confirmPassword.isBlank() -> "Please confirm your password"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
        val codeErr = when {
            code.isBlank() -> "Please enter the reset code"
            code.length != 6 || !code.all { it.isDigit() } -> "Code must be 6 digits"
            else -> null
        }

        return ValidationResult(
            isValid = codeErr == null && passwordErr == null && confirmErr == null,
            nameError = codeErr,
            passwordError = passwordErr,
            confirmPasswordError = confirmErr
        )
    }

    private fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Please enter your email"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email"
        else -> null
    }

    private fun validatePassword(password: String): String? = when {
        password.isBlank() -> "Please enter a password"
        password.length < MIN_PASSWORD_LENGTH -> "Password must be at least $MIN_PASSWORD_LENGTH characters"
        else -> null
    }
}
