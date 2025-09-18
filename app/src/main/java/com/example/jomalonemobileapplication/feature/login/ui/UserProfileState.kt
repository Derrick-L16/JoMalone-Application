package com.example.jomalonemobileapplication.feature.login.ui

data class UserProfileState(
    val sequentialId: String? = "", // Add sequential ID here
    val name: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val scentPreference: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val phoneNumberError: String? = null
)