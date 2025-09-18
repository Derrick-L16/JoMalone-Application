package com.example.jomalonemobileapplication.auth

import kotlin.jvm.java
import android.util.Log
import com.example.jomalonemobileapplication.data.User // Ensure this is your User data class
import com.google.firebase.firestore.FieldValue // Added for arrayUnion/arrayRemove
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserCloudDataRepository @Inject constructor(
    private val db: FirebaseFirestore // FirebaseFirestore injected by Hilt
) {

    private val usersCollection = db.collection("users")
    private val logTag = "UserCloudRepo"

    suspend fun saveCompleteUserToFirestore(user: User) {
        if (user.uid.isBlank()) {
            val errorMsg = "User UID is blank. Cannot save to Firestore."
            Log.e(logTag, errorMsg)
            throw IllegalArgumentException(errorMsg)
        }
        try {
            usersCollection.document(user.uid).set(user).await()
            Log.d(logTag, "User data completely saved/overwritten for UID: ${user.uid}")
        } catch (e: Exception) {
            Log.e(logTag, "Error saving complete user data for UID ${user.uid}", e)
            throw e // Re-throw to allow ViewModel to handle
        }
    }

    suspend fun updateUserProfile(
        userId: String,
        name: String?,
        phoneNumber: String?,
        // Note: Scent scores update is now separate for clarity, but could be merged
        // if you have a general "updateProfile" screen that also shows/edits preferences.
        // For now, keeping scentScores here in case it's used elsewhere.
        scentScores: Map<String, Double>?
    ) {
        if (userId.isBlank()) {
            Log.e(logTag, "Cannot update user profile: User ID is blank.")
            throw IllegalArgumentException("User ID cannot be blank for profile update.")
        }

        val updates = mutableMapOf<String, Any>()
        if (name != null) updates["name"] = name
        if (phoneNumber != null) updates["phoneNumber"] = phoneNumber
        if (scentScores != null) updates["scentPreferenceScores"] = scentScores // Keep if needed

        if (updates.isNotEmpty()) {
            updates["profileLastUpdated"] = System.currentTimeMillis() // Good practice
        } else {
            Log.d(logTag, "No actual profile updates to perform for user: $userId")
            return // No need to make a Firestore call
        }

        try {
            // Using SetOptions.merge() to only update provided fields
            usersCollection.document(userId).set(updates, SetOptions.merge()).await()
            Log.d(logTag, "User profile updated in Firestore for UID: $userId with updates: $updates")
        } catch (e: Exception) {
            Log.e(logTag, "Error updating user profile for UID $userId", e)
            throw e
        }
    }

    suspend fun getUserFromFirestore(userId: String): User? {
        if (userId.isBlank()) {
            Log.w(logTag, "getUserFromFirestore called with blank userId.")
            return null
        }
        Log.d(logTag, "Attempting to fetch user from Firestore for UID: $userId")
        return try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                Log.d(logTag, "Document found for UID: $userId. Data: ${document.data}")
                val user = document.toObject(User::class.java)
                // Ensure UID is set from document ID, as toObject might not always populate it if it's not a field
                user?.uid = document.id
                if (user == null) {
                    Log.e(logTag, "Failed to map document to User object for UID: $userId. Check User data class and Firestore fields match.")
                } else if (user.uid.isBlank()) { // Should not happen if set above, but good check
                    Log.e(logTag, "[getUser] CRITICAL: User object's UID is BLANK after mapping. DocID: '${document.id}', User: $user")
                } else {
                    Log.d(logTag, "Successfully mapped user: ${user.name}, UID: ${user.uid}, Prefs: ${user.scentPreferenceScores}")
                }
                user
            } else {
                Log.w(logTag, "No user document found for UID: $userId")
                null
            }
        } catch (e: Exception) {
            Log.e(logTag, "Error fetching user $userId from Firestore", e)
            null // Return null on error so ViewModel can handle it
        }
    }

    suspend fun getAllUsersFromFirestore(): List<User> {
        Log.d(logTag, "getAllUsersFromFirestore called")
        return try {
            val snapshot = usersCollection.orderBy("email").get().await() // Example: order by email
            Log.d(logTag, "Fetched ${snapshot.size()} documents from Firestore.")
            snapshot.documents.mapNotNull { document ->
                val user = document.toObject(User::class.java)
                user?.uid = document.id // Important: Set UID from document ID
                if (user?.uid?.isNotBlank() == true) {
                    user
                } else {
                    if (document.id.isBlank()) {
                        Log.e(logTag, "Firestore document found with BLANK/EMPTY ID in getAllUsers: ${document.reference.path}. Skipping.")
                    } else if (user == null){
                        Log.e(logTag, "Failed to map document to User (ID: ${document.id}) in getAllUsers. Skipping.")
                    } else {
                        Log.e(logTag, "CRITICAL MAPPING ERROR (getAllUsers): User UID is BLANK but DocID ('${document.id}') is not. Skipping. User: $user")
                    }
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(logTag, "Error fetching all users from Firestore", e)
            emptyList() // Return empty list on error
        }
    }

    suspend fun deleteUserAccountData(userId: String) {
        if (userId.isBlank()) {
            Log.w(logTag, "deleteUserAccountData called with blank userId.")
            return // Or throw IllegalArgumentException
        }
        try {
            usersCollection.document(userId).delete().await()
            Log.d(logTag, "User data deleted from Firestore for UID: $userId")
        } catch (e: Exception) {
            Log.e(logTag, "Error deleting user data for UID $userId", e)
            throw e
        }
    }

    // --- New Method for Scent Preference Update ---
    suspend fun updateUserScentPreferences(userId: String, newPreferences: Map<String, Double>): Boolean {
        if (userId.isBlank()) {
            Log.e(logTag, "User ID is blank, cannot update scent preferences.")
            return false
        }
        Log.d(logTag, "Attempting to update scent preferences for user $userId with: $newPreferences")
        return try {
            usersCollection.document(userId)
                .update("scentPreferenceScores", newPreferences)
                .await()
            Log.d(logTag, "Scent preferences updated successfully for user $userId")
            true
        } catch (e: Exception) {
            Log.e(logTag, "Error updating scent preferences for user $userId: ", e)
            false
        }
    }

    // --- Optional Methods for Wishlist Management ---
    // Ensure your User data class has a field like:
    // val wishlistedPerfumeIds: List<String>? = null (or emptyList() as default)

    suspend fun addItemToUserWishlist(userId: String, perfumeId: String): Boolean {
        if (userId.isBlank() || perfumeId.isBlank()) {
            Log.w(logTag, "Cannot add to wishlist: User ID or Perfume ID is blank.")
            return false
        }
        Log.d(logTag, "Attempting to add perfume $perfumeId to wishlist for user $userId")
        return try {
            usersCollection.document(userId)
                .update("wishlistedPerfumeIds", FieldValue.arrayUnion(perfumeId))
                .await()
            Log.d(logTag, "Perfume $perfumeId added to wishlist for user $userId")
            true
        } catch (e: Exception) {
            Log.e(logTag, "Error adding $perfumeId to wishlist for user $userId: ", e)
            false
        }
    }

    suspend fun removeItemFromUserWishlist(userId: String, perfumeId: String): Boolean {
        if (userId.isBlank() || perfumeId.isBlank()) {
            Log.w(logTag, "Cannot remove from wishlist: User ID or Perfume ID is blank.")
            return false
        }
        Log.d(logTag, "Attempting to remove perfume $perfumeId from wishlist for user $userId")
        return try {
            usersCollection.document(userId)
                .update("wishlistedPerfumeIds", FieldValue.arrayRemove(perfumeId))
                .await()
            Log.d(logTag, "Perfume $perfumeId removed from wishlist for user $userId")
            true
        } catch (e: Exception) {
            Log.e(logTag, "Error removing $perfumeId from wishlist for user $userId: ", e)
            false
        }
    }
}

