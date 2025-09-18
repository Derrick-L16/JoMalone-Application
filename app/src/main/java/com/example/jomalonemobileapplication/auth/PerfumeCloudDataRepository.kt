package com.example.jomalonemobileapplication.auth

import android.util.Log
import com.example.jomalonemobileapplication.data.Perfume
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PerfumeCloudDataRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val perfumesCollection = firestore.collection("perfumes") // Ensure this collection name is correct

    fun getPerfumesFlow(): Flow<List<Perfume>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = perfumesCollection
            .orderBy("name", Query.Direction.ASCENDING) // Optional: order as needed
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirestorePerfumeRepo", "Listen error in getPerfumesFlow", error)
                    close(error) // Close the flow with an error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val perfumes = snapshot.documents.mapNotNull { document ->
                        val perfume = document.toObject<Perfume>()
                        perfume?.apply { id = document.id } // Ensure ID from document is used
                    }
                    Log.d("FirestorePerfumeRepo", "Emitting ${perfumes.size} perfumes from Flow")
                    trySend(perfumes).isSuccess
                }
            }
        awaitClose {
            Log.d("FirestorePerfumeRepo", "Closing getPerfumesFlow listener")
            listenerRegistration.remove()
        }
    }

    suspend fun addPerfumeToFirestore(perfume: Perfume) {
        try {
            perfumesCollection.document(perfume.id).set(perfume).await()
            Log.d("FirestorePerfumeRepo", "Perfume added to Firestore with ID: ${perfume.id}")
        } catch (e: Exception) {
            Log.e("FirestorePerfumeRepo", "Error adding perfume to Firestore with ID ${perfume.id}", e)
            throw e // Let ViewModel catch it
        }
    }

    suspend fun updatePerfumeInFirestore(perfume: Perfume) {
        try {
            perfumesCollection.document(perfume.id).set(perfume, SetOptions.merge()).await()
            Log.d("FirestorePerfumeRepo", "Perfume updated in Firestore with ID: ${perfume.id}")
        } catch (e: Exception) {
            Log.e("FirestorePerfumeRepo", "Error updating perfume in Firestore with ID ${perfume.id}", e)
            throw e // Let ViewModel catch it
        }
    }

    suspend fun deletePerfumeFromFirestore(perfumeId: String) {
        try {
            perfumesCollection.document(perfumeId).delete().await()
            Log.d("FirestorePerfumeRepo", "Perfume deleted from Firestore with ID: $perfumeId")
        } catch (e: Exception) {
            Log.e("FirestorePerfumeRepo", "Error deleting perfume from Firestore with ID $perfumeId", e)
            throw e // Let ViewModel catch it
        }
    }

    // You can keep other specific, non-CRUD methods if needed (e.g., search, getById for one-time)
    // For example, if you need a one-time fetch:
    suspend fun getPerfumeByIdFromFirestore(perfumeId: String): Perfume? {
        return try {
            val documentSnapshot = perfumesCollection.document(perfumeId).get().await()
            if (documentSnapshot.exists()) {
                documentSnapshot.toObject<Perfume>()?.apply { id = documentSnapshot.id }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirestorePerfumeRepo", "Error fetching perfume by ID $perfumeId from Firestore", e)
            null // Or throw
        }
    }
}
