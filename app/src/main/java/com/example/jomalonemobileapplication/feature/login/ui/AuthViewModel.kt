package com.example.jomalonemobileapplication.feature.login.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jomalonemobileapplication.AppDatabase
import com.example.jomalonemobileapplication.feature.login.data.AuthRepositoryImpl
import com.example.jomalonemobileapplication.feature.login.data.UserEntity
import com.example.jomalonemobileapplication.feature.profile.ui.DeleteAccountState
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentType
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize database and repository
    private val database = AppDatabase.getDatabase(application)
    private val repository: AuthRepositoryImpl = AuthRepositoryImpl(database.userDao())

    // SignIn UI State
    private val _signInUiState = MutableStateFlow(SignInUiState())
    val signInUiState: StateFlow<SignInUiState> = _signInUiState.asStateFlow()

    // SignUp UI State
    private val _signUpUiState = MutableStateFlow(SignUpUiState())
    val signUpUiState: StateFlow<SignUpUiState> = _signUpUiState.asStateFlow()

    // Forgot Password UI State
    private val _forgotPasswordUiState = MutableStateFlow(ForgotPasswordUiState())
    val forgotPasswordUiState: StateFlow<ForgotPasswordUiState> =
        _forgotPasswordUiState.asStateFlow()

    // Add user profile state
    private val _userProfileState = MutableStateFlow(UserProfileState())
    val userProfileState: StateFlow<UserProfileState> = _userProfileState.asStateFlow()

    private val _deleteAccountState = MutableStateFlow(DeleteAccountState())
    val deleteAccountState: StateFlow<DeleteAccountState> = _deleteAccountState.asStateFlow()

    // ===================================== COMBINED SIGN IN & SIGNUP FUNCTION(display error message on time) =====================================
    fun updateEmailOnTime(email: String, isSignUp: Boolean = false) {
        val errorMessage = if (email.isBlank()) "Email cannot be empty" else null

        if (isSignUp) {
            _signUpUiState.value = _signUpUiState.value.copy(
                email = email,
                emailError = errorMessage
            )
            return
        } else {
            _signInUiState.value = _signInUiState.value.copy(
                email = email,
                emailError = errorMessage
            )
        }
    }

    fun updatePasswordOnTime(password: String, isSignUp: Boolean = false) {
        val errorMessage = when {
            password.isBlank() -> "Password cannot be empty"
            isSignUp && password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

        if (isSignUp) {
            _signUpUiState.value = _signUpUiState.value.copy(
                password = password,
                passwordError = errorMessage
            )
        } else {
            _signInUiState.value = _signInUiState.value.copy(
                password = password,
                passwordError = errorMessage
            )
        }
    }

    // ===================================== SIGN IN FUNCTION =====================================
    // check whether the confirm password is same as password
    fun updateSignUpConfirmPassword(confirmPassword: String) {
        _signUpUiState.value = _signUpUiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = when {
                confirmPassword.isBlank() -> "Please confirm your password"
                confirmPassword != _signUpUiState.value.password -> "Passwords do not match"
                else -> null
            }
        )
    }

    // ===================================== VALIDATION FUNCTION =====================================
    private fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // validate malaysian phone number
    private fun isValidMalaysianPhoneNumber(phoneNumber: String): Boolean {
        val cleanPhone = phoneNumber.replace("+60", "").replace("\\s".toRegex(), "")
        return cleanPhone.matches("\\d{8,10}".toRegex())
    }

    fun getFormattedPhoneNumber(phoneNumber: String): String {
        val cleanPhone = phoneNumber.replace("+60", "").replace("\\s".toRegex(), "")
        return "+60$cleanPhone"
    }

    //validate the email and password input(AFTER user click sign in button)
    fun validateSignInInput(): Boolean {
        val currentState = _signInUiState.value
        var isValid = true

        if (!isValidEmail(currentState.email)) {
            val errorMessage = when {
                currentState.email.isBlank() -> "Email cannot be empty"
                else -> "Invalid email format"
            }
            _signInUiState.value = currentState.copy(emailError = errorMessage)
            isValid = false
        }

        if (currentState.password.isBlank()) {
            _signInUiState.value = currentState.copy(passwordError = "Password cannot be empty")
            isValid = false
        }

        return isValid // return true if all inputs are valid
    }

    // validate the user input for sign up
    private fun validateSignUpInput(name: String, phoneNumber: String): Boolean {
        val currentState = _signUpUiState.value
        var isValid = true
        var errorMessage = ""

        // check name
        if (name.isBlank()) {
            errorMessage = "Please enter your name"
            isValid = false
        }

        // Validate phone number
        if (phoneNumber.isBlank()) {
            errorMessage = "Please enter your phone number"
            isValid = false
        } else if (!isValidMalaysianPhoneNumber(phoneNumber)) {
            errorMessage = "Malaysian phone number must be 8-10 digits (excluding +60)"
            isValid = false
        }

        // Validate email
        if (!isValidEmail(currentState.email)) {
            _signUpUiState.value = currentState.copy(
                emailError = if (currentState.email.isBlank()) "Email cannot be empty" else "Invalid email format"
            )
            isValid = false
        }

        // check password
        if (currentState.password.isBlank()) {
            _signUpUiState.value = currentState.copy(passwordError = "Password cannot be empty")
            isValid = false
        } else if (currentState.password.length < 6) {
            _signUpUiState.value =
                currentState.copy(passwordError = "Password must be at least 6 characters")
            isValid = false
        }

        // check confirm password
        if (currentState.confirmPassword.isBlank()) {
            _signUpUiState.value =
                currentState.copy(confirmPasswordError = "Please confirm your password")
            isValid = false
        } else if (currentState.confirmPassword != currentState.password) {
            _signUpUiState.value =
                currentState.copy(confirmPasswordError = "Passwords do not match")
            isValid = false
        }

        // Set general error message if any field validation failed
        if (errorMessage.isNotEmpty()) {
            _signUpUiState.value = currentState.copy(errorMessage = errorMessage)
        }

        return isValid
    }

    // ===================================== AUTHENTICATION FUNCTIONS =====================================
    // check is that the email and password is valid
    fun signIn(onSuccess: () -> Unit) {
        if (!validateSignInInput()) return

        _signInUiState.value = _signInUiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = repository.loginUser(
                _signInUiState.value.email,
                _signInUiState.value.password
            )

            _signInUiState.value = _signInUiState.value.copy(isLoading = false)

            result.onSuccess {
                _signInUiState.value = _signInUiState.value.copy(successMessage = it)
                onSuccess()
            }.onFailure { error ->
                _signInUiState.value = _signInUiState.value.copy(
                    errorMessage = getFirebaseAuthError(error, true)
                )
            }
        }
    }

    // check is that the email and password is valid and store the user data into firestore
    fun signUp(name: String, phoneNumber: String, onSuccess: () -> Unit) {
        if (!validateSignUpInput(name, phoneNumber)) return

        _signUpUiState.value = _signUpUiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val registerResult = repository.registerUser(
                _signUpUiState.value.email,
                _signUpUiState.value.password,
                name = name,
                phoneNumber = getFormattedPhoneNumber(phoneNumber)
            )

            registerResult.onSuccess { message ->
                // Get the newly created user ID
                val userId = repository.getCurrentUser()?.uid
                if (userId != null) {
                    // Store additional user data
                    val storeResult = repository.storeUserData(
                        userId,
                        name,
                        getFormattedPhoneNumber(phoneNumber),
                        _signUpUiState.value.email
                    )

                    storeResult.onSuccess {
                        _signUpUiState.value = _signUpUiState.value.copy(
                            successMessage = "$message. $it",
                            isLoading = false
                        )
                        onSuccess()
                    }.onFailure { error ->
                        _signUpUiState.value = _signUpUiState.value.copy(
                            errorMessage = "Registration successful but failed to save profile: ${error.message}",
                            isLoading = false
                        )
                    }
                } else {
                    _signUpUiState.value = _signUpUiState.value.copy(
                        errorMessage = "Registration failed: User ID not found",
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _signUpUiState.value = _signUpUiState.value.copy(
                    errorMessage = getFirebaseAuthError(error, false),
                    isLoading = false
                )
            }
        }
    }

    // ===================================== FORGOT PASSWORD FUNCTION =====================================
    fun updateForgotPasswordEmail(email: String) {
        _forgotPasswordUiState.value = _forgotPasswordUiState.value.copy(
            email = email,
            errorMessage = null
        )
    }

    fun sendPasswordResetEmail() {
        val currentState = _forgotPasswordUiState.value

        if (!isValidEmail(currentState.email)) {
            val errorMessage = when {
                currentState.email.isBlank() -> "Please enter your email address"
                else -> "Invalid email format"
            }
            _forgotPasswordUiState.value = currentState.copy(
                errorMessage = errorMessage
            )
            return
        }

        _forgotPasswordUiState.value = currentState.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null,
            showSuccessDialog = false
        )

        viewModelScope.launch {
            val result = repository.sendPasswordResetEmail(currentState.email)

            _forgotPasswordUiState.value = _forgotPasswordUiState.value.copy(
                isLoading = false
            )

            result.onSuccess { message ->
                _forgotPasswordUiState.value = _forgotPasswordUiState.value.copy(
                    successMessage = message,
                    showSuccessDialog = true
                )
            }.onFailure { error ->
                _forgotPasswordUiState.value = _forgotPasswordUiState.value.copy(
                    errorMessage = getFirebasePasswordResetError(error),
                    showSuccessDialog = false
                )
            }
        }
    }

    // ===================================== USER PROFILE FUNCTION =====================================
    // Load user profile data (Firestore first then local database)
    fun loadUserProfile() {
        val firebaseUid = getCurrentUserId()
        val userEmail = getCurrentUserEmail()

        if (firebaseUid != null && userEmail != null) {
            _userProfileState.value = _userProfileState.value.copy(isLoading = true)

            viewModelScope.launch {
                try {
                    // First try to get the current user data from Firestore
                    val firestoreResult = repository.getUserData(firebaseUid)

                    firestoreResult.onSuccess { data ->
                        val sequentialId = data["userId"] as? String ?: ""
                        val name = data["name"] as? String ?: ""
                        val phoneNumber = data["phoneNumber"] as? String ?: ""
                        val email = data["email"] as? String ?: userEmail

                        // update UI state
                        _userProfileState.value = UserProfileState(
                            sequentialId = sequentialId,
                            name = name,
                            phoneNumber = phoneNumber,
                            email = email,
                            isLoading = false
                        )

                        // synchronize the data with local database (PRESERVE EXISTING PASSWORD)
                        if (sequentialId.isNotEmpty()) {
                            // get existing local user
                            val existingLocalUser = repository.getLocalUser(email)
                            val password = existingLocalUser?.password ?: ""

                            val localUser = UserEntity(
                                id = sequentialId,
                                gmail = email,
                                password = password,
                                name = name,
                                phoneNumber = phoneNumber
                            )
                            repository.updateLocalUser(localUser)
                        }
                    }.onFailure {
                        // If Firestore fetch fails, try to get from local database
                        getLocalUserData(userEmail) { localUser ->
                            if (localUser != null) {
                                _userProfileState.value = UserProfileState(
                                    sequentialId = localUser.id,
                                    name = localUser.name,
                                    phoneNumber = localUser.phoneNumber,
                                    email = localUser.gmail,
                                    isLoading = false
                                )
                            } else {
                                // If no local data, show minimal info (seldom or never happen)
                                _userProfileState.value = UserProfileState(
                                    sequentialId = null,
                                    name = "User",
                                    phoneNumber = "",
                                    email = userEmail,
                                    isLoading = false
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    _userProfileState.value = _userProfileState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load profile: ${e.message}"
                    )
                }
            }
        } else {
            _userProfileState.value = _userProfileState.value.copy(isLoading = false)
        }
    }

    // Update user profile (both firebase and local database) in the profileInformation
    fun updateUserProfile(newName: String, newPhoneNumber: String, onSuccess: () -> Unit) {
        val phoneError =
            if (newPhoneNumber.isNotBlank() && !isValidMalaysianPhoneNumber(newPhoneNumber)) {
                "Malaysian phone number must be 8-10 digits"
            } else {
                null
            }

        if (phoneError != null) {
            _userProfileState.value = _userProfileState.value.copy(
                phoneNumberError = phoneError,
                errorMessage = null
            )
            return
        }

        val userId = getCurrentUserId()
        if (userId != null) {
            _userProfileState.value =
                _userProfileState.value.copy(isLoading = true, errorMessage = null)

            viewModelScope.launch {
                val formattedPhoneNumber = if (newPhoneNumber.isNotBlank()) {
                    getFormattedPhoneNumber(newPhoneNumber)
                } else {
                    null
                }
                // only pass non-blank values to the repository
                val nameToUpdate = if (newName.isNotBlank()) newName.trim() else null

                if (nameToUpdate == null && formattedPhoneNumber == null) {
                    _userProfileState.value = _userProfileState.value.copy(
                        isLoading = false,
                        errorMessage = "Please enter at least name or phone number to update"
                    )
                    return@launch
                }

                val result =
                    repository.updateUserData(userId, nameToUpdate, formattedPhoneNumber)

                result.onSuccess {
                    val currentState = _userProfileState.value
                    _userProfileState.value = currentState.copy(
                        name = nameToUpdate ?: currentState.name,
                        phoneNumber = formattedPhoneNumber ?: currentState.phoneNumber,
                        isLoading = false,
                        errorMessage = null,
                        phoneNumberError = null
                    )
                    onSuccess()
                }.onFailure { error ->
                    _userProfileState.value = _userProfileState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to update profile: ${error.message}"
                    )
                }
            }
        }
    }

    fun updateUserScentPreference(scentType: ScentType, onSuccess: () -> Unit) {
        val firebaseUid = getCurrentUserId()
        if (firebaseUid != null) {
            _userProfileState.value = _userProfileState.value.copy(isLoading = true, errorMessage = null)

            viewModelScope.launch {
                val result = repository.updateUserScentPreference(firebaseUid, scentType.name)
                result.onSuccess {
                    // Update local state
                    _userProfileState.value = _userProfileState.value.copy(
                        scentPreference = scentType.name,
                        isLoading = false,
                        errorMessage = null
                    )
                    onSuccess()
                }.onFailure { error ->
                    _userProfileState.value = _userProfileState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to update scent preference: ${error.message}"
                    )
                }
            }
        }
    }

    fun loadUserScentPreference() {
        val firebaseUid = getCurrentUserId()
        if (firebaseUid != null) {
            viewModelScope.launch {
                val result = repository.getUserScentPreference(firebaseUid)
                result.onSuccess { scentPreference ->
                    if (scentPreference != null) {
                        _userProfileState.value = _userProfileState.value.copy(
                            scentPreference = scentPreference
                        )
                    }
                }
            }
        }
    }

    fun clearUserScentPreference(onSuccess: () -> Unit = {}) {
        val firebaseUid = getCurrentUserId()
        if (firebaseUid != null) {
            _userProfileState.value = _userProfileState.value.copy(isLoading = true, errorMessage = null)

            viewModelScope.launch {
                val result = repository.clearUserScentPreference(firebaseUid)
                result.onSuccess {
                    // Update local state to remove scent preference
                    _userProfileState.value = _userProfileState.value.copy(
                        scentPreference = null,
                        isLoading = false,
                        errorMessage = null
                    )
                    onSuccess()
                }.onFailure { error ->
                    _userProfileState.value = _userProfileState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to clear scent preference: ${error.message}"
                    )
                }
            }
        }
    }

    // ===================================== ACCOUNT DELETION FUNCTION =====================================
    // ===================================== DELETE ACCOUNT FUNCTIONS =====================================
    fun reauthenticateAndDelete(
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val currentUser = repository.getCurrentUser()
        val email = currentUser?.email ?: ""

        if (email.isEmpty()) {
            onFailure("No email found for current user")
            return
        }

        viewModelScope.launch {
            val credential = EmailAuthProvider.getCredential(email, password)

            try {
                // Reauthenticate the user
                currentUser?.reauthenticate(credential)?.await()
                // If reauthentication is successful, proceed to delete account
                val result = repository.deleteUserAccount()

                result.onSuccess {
                    onSuccess()
                }.onFailure { error ->
                    onFailure(error.message ?: "Failed to delete account")
                }
            } catch (e: Exception) {
                onFailure("Reauthentication failed: ${e.message}")
            }
        }
    }

    fun startAccountDeletion() {
        _deleteAccountState.value = _deleteAccountState.value.copy(showDialog = true)
    }

    fun cancelAccountDeletion() {
        _deleteAccountState.value = DeleteAccountState() // 重置为默认状态
    }

    fun deleteAccountWithPassword(password: String) {
        if (password.isBlank()) {
            _deleteAccountState.value = _deleteAccountState.value.copy(
                errorMessage = "Please enter your password"
            )
            return
        }

        _deleteAccountState.value = _deleteAccountState.value.loading()

        viewModelScope.launch {
            reauthenticateAndDelete(
                password = password,
                onSuccess = {
                    _deleteAccountState.value = _deleteAccountState.value.success()
                },
                onFailure = { error ->
                    val errorMessage = if (error.contains("Reauthentication failed")) {
                        "Incorrect password. Please try again."
                    } else {
                        "Account deletion failed: $error"
                    }
                    _deleteAccountState.value = _deleteAccountState.value.error(errorMessage)
                }
            )
        }
    }

    fun clearDeleteAccountError() {
        _deleteAccountState.value = _deleteAccountState.value.copy(errorMessage = null)
    }


    // ===================================== UTILITY FUNCTION =====================================
    // Get user from local database
    fun getLocalUserData(email: String, onResult: (UserEntity?) -> Unit) {
        viewModelScope.launch {
            val user = repository.getLocalUser(email)
            onResult(user)
        }
    }

    // Get current user's sequential ID
    fun getCurrentUserSequentialId(callback: (String?) -> Unit) {
        val firebaseUid = getCurrentUserId()
        if (firebaseUid != null) {
            viewModelScope.launch {
                val sequentialId = repository.getSequentialUserId(firebaseUid)
                callback(sequentialId)
            }
        } else {
            callback(null)
        }
    }

    // ===================================== FIREBASE FUNCTION =====================================
// for the welcome back purpose, no need to login again if the user already logged in
    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }

    fun getCurrentUserEmail(): String? {
        return repository.getCurrentUser()?.email
    }

    fun getCurrentUserId(): String? {
        return repository.getCurrentUser()?.uid
    }

    fun signOut() {
        repository.signOut()
    }

    // ===================================== FIREBASE ERROR HANDLING =====================================
    private fun getFirebaseAuthError(error: Throwable, isSignIn: Boolean): String {
        return when {
            error.message?.contains(
                "invalid-email",
                ignoreCase = true
            ) == true -> "Invalid email format"

            error.message?.contains(
                "invalid-credential",
                ignoreCase = true
            ) == true -> "Invalid email or password."

            error.message?.contains(
                "user-not-found",
                ignoreCase = true
            ) == true -> "No account found with this email. Please sign up first"

            error.message?.contains(
                "wrong-password",
                ignoreCase = true
            ) == true -> "Incorrect password"

            error.message?.contains(
                "email-already-in-use",
                ignoreCase = true
            ) == true -> "Email is already registered. Please sign in instead"

            error.message?.contains(
                "weak-password",
                ignoreCase = true
            ) == true -> "Password is too weak. Please choose a stronger password"

            else -> error.message
                ?: if (isSignIn) "Login failed. Please try again" else "Registration failed. Please try again"
        }
    }

    private fun getFirebasePasswordResetError(error: Throwable): String {
        return when {
            error.message?.contains("invalid-email", ignoreCase = true) == true ->
                "Invalid email format"

            else -> error.message ?: "Failed to send password reset email. Please try again."
        }
    }

    // ===================================== HELPER FUNCTION =====================================
    fun clearSignInMessages() {
        _signInUiState.value = _signInUiState.value.copy(
            successMessage = null,
            errorMessage = null
        )
    }

    fun clearForgotPasswordMessages() {
        _forgotPasswordUiState.value = _forgotPasswordUiState.value.copy(
            successMessage = null,
            errorMessage = null
        )
    }

    fun dismissSuccessDialog() {
        _forgotPasswordUiState.value = _forgotPasswordUiState.value.copy(
            showSuccessDialog = false
        )
    }

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        if (isUserLoggedIn()) {
            _signInUiState.value = _signInUiState.value.copy(
                successMessage = "Welcome back!",
                email = getCurrentUserEmail() ?: ""
            )
        }
    }
}