package com.example.jomalonemobileapplication.feature.login.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao? = null
) {
    // ========================= FIREBASE INSTANCES =========================
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // ========================= AUTHENTICATION FUNCTIONS =========================
    // register user with Firebase Auth and and store in both Firebase and local database
    suspend fun registerUser(
        email: String,
        password: String,
        name: String,
        phoneNumber: String
    ): Result<String> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUid = result.user?.uid
            if (result.user != null) {
                val sequentialId = getNextSequentialId()

                // Store user credentials in local database
                userDao?.let { dao ->
                    val userEntity = UserEntity(
                        id = sequentialId,
                        gmail = email,
                        password = password,
                        name = name,
                        phoneNumber = phoneNumber
                    )
                    dao.insertUser(userEntity)
                }

                // Store in Firestore with sequential ID as document ID
                val userData = hashMapOf(
                    "userId" to sequentialId,
                    "firebaseUid" to firebaseUid,
                    "name" to name,
                    "phoneNumber" to phoneNumber,
                    "email" to email,
                    "createdAt" to com.google.firebase.Timestamp.now()
                )

                // Use sequential ID as document ID
                firestore.collection("users")
                    .document(sequentialId.toString())
                    .set(userData)
                    .await()

                Result.success("User registered successfully")
            } else {
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // login user with Firebase Auth
    suspend fun loginUser(email: String, password: String): Result<String> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                Result.success("Login successful")
            } else { // this will rarely happen as exceptions are thrown for errors
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            val errorMessage = getFirebaseSignInError(e)
            Result.failure(Exception(errorMessage))
        }
    }

    // Send password reset email
    suspend fun sendPasswordResetEmail(email: String): Result<String> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success("Password reset email sent")
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("user-not-found", ignoreCase = true) == true ->
                    "No account found with this email"

                e.message?.contains("invalid-email", ignoreCase = true) == true ->
                    "Invalid email format"

                else -> e.message ?: "Failed to send password reset email"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    // ========================= USER DATA MANAGEMENT =========================
    // Get user data from Firestore by firebase UID
    suspend fun getUserData(firebaseUid: String): Result<Map<String, Any>> {
        return try {
            val document = firestore.collection("users")
//                .document(userId)
                .whereEqualTo("firebaseUid", firebaseUid)
                .get()
                .await()

            if (!document.isEmpty) {
                val document = document.documents.first()
                Result.success(document.data ?: emptyMap())
            } else {
                Result.failure(Exception("User data not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update user data in both Firestore and local database
    suspend fun updateUserData(
        firebaseUid: String,
        name: String?,
        phoneNumber: String?
    ): Result<String> {
        return try {
            val userData = getUserData(firebaseUid)
            userData.onSuccess { data ->
                val sequentialId = when ( val userIdValue = data["userId"]){
                    is String -> userIdValue // this will catch "User_23"
                    is Long -> userIdValue.toString() // this will catch 23
                    is Int -> userIdValue.toString()
                    else -> return Result.failure(Exception("Invalid user ID format"))
                }

                val updates = hashMapOf<String, Any>()

                // only add fields that need to be updated
                if (name != null && name.isNotBlank()) {
                    updates["name"] = name
                }

                if (phoneNumber != null && phoneNumber.isNotBlank()) {
                    updates["phoneNumber"] = phoneNumber
                }

                updates["updatedAt"] = com.google.firebase.Timestamp.now()

                firestore.collection("users")
                    .document(sequentialId.toString())
                    .set(updates, SetOptions.merge())
                    .await()

                // update in local database
                updateLocalDatabase(name, phoneNumber)
            }
            Result.success("User data updated successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Store additional user data in Firestore
    suspend fun storeUserData(
        userId: String,
        name: String,
        phoneNumber: String,
        email: String
    ): Result<String> {
        return Result.success("User data stored successfully")
    }

suspend fun updateUserScentPreference(firebaseUid: String, scentType: String): Result<String> {
    return try {
        // Find the user document by firebaseUid
        val querySnapshot = firestore.collection("users")
            .whereEqualTo("firebaseUid", firebaseUid)
            .get()
            .await()

        if (querySnapshot.isEmpty) {
            return Result.failure(Exception("User not found"))
        }

        val userDocument = querySnapshot.documents.first()
        val documentId = userDocument.id // This is your sequential ID

        val updates = hashMapOf<String, Any>(
            "scentPreference" to scentType,
            "scentPreferenceUpdatedAt" to com.google.firebase.Timestamp.now()
        )

        // Update the existing user document with sequential ID
        firestore.collection("users")
            .document(documentId)
            .set(updates, SetOptions.merge())
            .await()

        Result.success("Scent preference updated successfully")
    } catch (e: Exception) {
        Result.failure(e)
    }
}

    suspend fun getUserScentPreference(firebaseUid: String): Result<String?> {
        return try {
            // Find the user document by firebaseUid
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("firebaseUid", firebaseUid)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                return Result.failure(Exception("User not found"))
            }

            val userDocument = querySnapshot.documents.first()
            val scentPreference = userDocument.getString("scentPreference")
            Result.success(scentPreference)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearUserScentPreference(firebaseUid: String): Result<String> {
        return try {
            // Find the user document by firebaseUid
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("firebaseUid", firebaseUid)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                return Result.failure(Exception("User not found"))
            }

            val userDocument = querySnapshot.documents.first()
            val documentId = userDocument.id

            val updates = hashMapOf<String, Any>(
                "scentPreference" to FieldValue.delete(),
                "scentPreferenceUpdatedAt" to FieldValue.delete()
            )

            firestore.collection("users")
                .document(documentId)
                .update(updates)
                .await()

            Result.success("Scent preference cleared successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========================= ACCOUNT DELECTION FUNCTION =========================
    suspend fun deleteUserAccount(): Result<String> {
        return try {
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                // Delete from Firebase Auth
                currentUser.delete().await()

                // Delete from Firestore
                val sequentialId = getSequentialUserId(currentUser.uid)
                if (sequentialId != null) {
                    firestore.collection("users")
                        .document(sequentialId)
                        .delete()
                        .await()
                }

                // Delete from local database
                userDao?.let { dao ->
                    val localUser = dao.getUserByGmail(currentUser.email ?: "")
                    if (localUser != null) {
                        dao.deleteUserById(localUser.id)
                    }
                }

                // choose want to delete what data
                // deleteUserCustomizations(currentUser.uid)
                //OR
                /*
                * if (sequentialId != null) {
                deleteUserCustomizations(sequentialId)
            }
                * */

                Result.success("Account deleted successfully")
            } else {
                Result.failure(Exception("No user logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // delete user specific data from other collections if needed
    /*
    * private suspend fun deleteUserCustomizations(firebaseUid: String) {
    try {
        val sequentialId = getSequentialUserId(firebaseUid)
        if (sequentialId != null) {
            // Query and delete all customizations for the user
            val querySnapshot = firestore.collection("customizations")
                .whereEqualTo("userId", sequentialId)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                document.reference.delete().await()
            }
        }
    } catch (e: Exception) {
        println("Failed to delete customizations: ${e.message}")
    }
}
    * */

    // ========================= LOCAL DATABASE FUNCTIONS =========================
    // Get user from local database
    suspend fun getLocalUser(email: String): UserEntity? {
        return userDao?.getUserByGmail(email)
    }

    // Update local user data
    suspend fun updateLocalUser(userEntity: UserEntity): Result<String> {
        return try {
            userDao?.insertUser(userEntity) // Using REPLACE strategy
            Result.success("Local user data updated successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper function to update local database during profile updates
    private suspend fun updateLocalDatabase(name: String?, phoneNumber: String?) {
        val currentUser = getCurrentUser()
        if (currentUser?.email != null) {
            val existingLocalUser = userDao?.getUserByGmail(currentUser.email!!)
            if (existingLocalUser != null) {
                val updatedUser = existingLocalUser.copy(
                    name = name?.takeIf { it.isNotBlank() } ?: existingLocalUser.name,
                    phoneNumber = phoneNumber?.takeIf { it.isNotBlank() }
                        ?: existingLocalUser.phoneNumber,
                    password = existingLocalUser.password // keep existing password
                )
                updateLocalUser(updatedUser)
//                userDao?.insertUser(updatedUser)
            }
        }
    }

    // ========================= SEQUENTIAL ID MANAGEMENT =========================
    // Get next sequential ID from Firestore counter
    private suspend fun getNextSequentialId(): String {
        return try {
            val counterDoc = firestore.collection("counters").document("userCounter")
            val result = firestore.runTransaction { transaction ->
                val snapshot = transaction.get(counterDoc)
                val currentCount = if (snapshot.exists()) {
                    snapshot.getLong("count")?.toInt() ?: 0
                } else {
                    0
                }
                val newCount = currentCount + 1
                transaction.set(counterDoc, mapOf("count" to newCount))
                newCount
            }.await()
            "User_$result"
        } catch (e: Exception) {
            // Fallback: get count from local database + 1
            val localCount = userDao?.getUserCount() ?: 0
            "User_${localCount + 1}"
        }
    }

    // Get sequential user ID by Firebase UID
    suspend fun getSequentialUserId(firebaseUid: String): String? {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("firebaseUid", firebaseUid)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                document.getString("userId")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // ======================== FIREBASE UTILITY FUNCTION ========================
    // Get current Firebase user
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return getCurrentUser() != null
    }

    // Sign out current user
    fun signOut() {
        firebaseAuth.signOut()
    }

    // ========================= ERROR HANDLING =========================
    // Handle Firebase authentication errors
    private fun getFirebaseSignInError(error: Throwable): String {
        return when {
            error is FirebaseAuthException -> {
                when (error.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Invalid email format"
                    "ERROR_INVALID_CREDENTIAL" -> "Invalid email or password."
                    else -> error.message ?: "Login failed. Please try again"
                }
            }

            error.message?.contains("invalid-email", ignoreCase = true) == true ->
                "Invalid email format"

            error.message?.contains("invalid-credential", ignoreCase = true) == true ->
                "Invalid email or password."

            else -> error.message ?: "Login failed. Please try again"
        }
    }
}