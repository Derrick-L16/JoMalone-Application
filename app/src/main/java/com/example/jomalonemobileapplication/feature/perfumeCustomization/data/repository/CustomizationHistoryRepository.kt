package com.example.jomalonemobileapplication.feature.perfumeCustomization.data.repository

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jomalonemobileapplication.core.data.dao.CustomizationDao
import com.example.jomalonemobileapplication.core.data.entity.CustomizationEntity
import com.example.jomalonemobileapplication.feature.login.ui.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.first


class CustomizationHistoryRepository(
    private val customizationDao: CustomizationDao
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val customizationsCollection = firestore.collection("customizations")
    

    // Local Database Operations
    fun getCustomizationsByUserLocal(userId: String): Flow<List<CustomizationEntity>> {
        return customizationDao.getCustomizationsByUser(userId)
    }

    fun getCustomizationsByUser(userId: String): Flow<List<CustomizationEntity>> = callbackFlow {
        val listenerRegistration = customizationsCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error fetching customizations: ${error.message}")
                    // Fallback to local data if Firebase fails
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val customizations = snapshot.documents.mapNotNull { document ->
                        try {
                            CustomizationEntity(
                                id = 0, // Room will auto-generate this
                                userId = document.getString("userId") ?: "",
                                perfumeName = document.getString("perfumeName") ?: "",
                                baseNote = document.getString("baseNote") ?: "",
                                essence = document.getString("essence") ?: "",
                                experience = document.getString("experience") ?: "",
                                createdAt = document.getLong("createdAt") ?: 0L,
                                isAddedToCart = document.getBoolean("isAddedToCart") ?: false
                            )
                        } catch (e: Exception) {
                            println("Error parsing document ${document.id}: ${e.message}")
                            null
                        }
                    }
                    trySend(customizations)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    suspend fun saveCustomization(customization: CustomizationEntity) {
        // Save to local database
        customizationDao.insertCustomization(customization)

        // Save to Firebase
        try {
            val customizationData = mapOf(
                "userId" to customization.userId,
                "perfumeName" to customization.perfumeName,
                "baseNote" to customization.baseNote,
                "essence" to customization.essence,
                "experience" to customization.experience,
                "createdAt" to customization.createdAt,
                "isAddedToCart" to customization.isAddedToCart
            )

            customizationsCollection
                .document("${customization.userId}_${customization.createdAt}")
                .set(customizationData)
                .await()
        } catch (e: Exception) {
            // If Firebase fails, at least we have local copy
            println("Failed to save to Firebase: ${e.message}")
        }
    }

    suspend fun getLatestCustomization(userId: String): CustomizationEntity? {
        return customizationDao.getLatestCustomization(userId)
    }


    suspend fun updateCustomization(customization: CustomizationEntity) {
        customizationDao.updateCustomization(customization)

        try {
            val updateData = mapOf(
                "perfumeName" to customization.perfumeName,
                "baseNote" to customization.baseNote,
                "essence" to customization.essence,
                "experience" to customization.experience,
                "isAddedToCart" to customization.isAddedToCart
            )

            customizationsCollection
                .document("${customization.userId}_${customization.createdAt}")
                .update(updateData)
                .await()
        } catch (e: Exception) {
            println("Failed to update in Firebase: ${e.message}")
        }
    }

    suspend fun deleteCustomization(customization: CustomizationEntity) {
        customizationDao.deleteCustomization(customization)

        // Delete from Firebase
        try {
            customizationsCollection
                .document("${customization.userId}_${customization.createdAt}")
                .delete()
                .await()
        } catch (e: Exception) {
            println("Failed to delete from Firebase: ${e.message}")
        }
    }
}