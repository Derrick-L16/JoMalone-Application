package com.example.jomalonemobileapplication.feature.profile.ui

data class DeleteAccountState(
    val showDialog: Boolean = false,
    val showSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    //Helper function to show loading state
    fun loading() = copy(isLoading = true, errorMessage = null)

    //Helper function to show error state
    fun error(message: String) = copy(isLoading = false, errorMessage = message)

    //Helper function to show success state
    fun success() = copy(isLoading = false, showSuccess = true, showDialog = true)
}