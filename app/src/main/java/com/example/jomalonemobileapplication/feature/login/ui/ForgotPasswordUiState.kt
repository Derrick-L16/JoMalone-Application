package com.example.jomalonemobileapplication.feature.login.ui

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val showSuccessDialog: Boolean = false // for pop up dialog after press send reset link
)