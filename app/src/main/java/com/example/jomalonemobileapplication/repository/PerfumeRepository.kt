package com.example.jomalonemobileapplication.repository

import android.util.Log
import com.example.jomalonemobileapplication.data.Perfume // Your existing Perfume model from admin.data
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.java

@Singleton
class PerfumeRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val perfumesCollection = firestore.collection("perfumes")
    private val logTag = "PerfumeRepository" // Or "PerfumeRepositoryTAG" for consistency

    // This helper should already exist in your repository.
    // Ensure it correctly deserializes your Perfume object and sets the ID.
    private fun mapDocumentToPerfume(document: DocumentSnapshot): Perfume? {
        return try {
            val perfume = document.toObject(Perfume::class.java)
            // Crucially, set the perfume's ID from the document ID after deserialization
            perfume?.apply { id = document.id }
        } catch (e: Exception) {
            Log.e(logTag, "Error mapping document ${document.id} to Perfume: ${e.message}", e)
            null
        }
    }

    // This method should already exist for your MainPageViewModel.
    suspend fun getAllPerfumes(): List<Perfume> {
        Log.d(logTag, "getAllPerfumes called")
        return try {
            val snapshot = perfumesCollection.orderBy("name", Query.Direction.ASCENDING).get().await()
            snapshot.documents.mapNotNull { mapDocumentToPerfume(it) }
        } catch (e: Exception) {
            Log.e(logTag, "Error in getAllPerfumes", e)
            emptyList()
        }
    }

    // This method might exist if you have a product detail page.
    suspend fun getPerfumeById(perfumeId: String): Perfume? {
        if (perfumeId.isBlank()) {
            Log.w(logTag, "getPerfumeById called with blank ID.")
            return null
        }
        Log.d(logTag, "getPerfumeById called for ID: $perfumeId")
        return try {
            val document = perfumesCollection.document(perfumeId).get().await()
            if (document.exists()) {
                mapDocumentToPerfume(document)
            } else {
                Log.d(logTag, "No perfume document found for ID: $perfumeId")
                null
            }
        } catch (e: Exception) {
            Log.e(logTag, "Error fetching perfume by ID '$perfumeId'", e)
            null
        }
    }

    // --- THIS IS THE KEY METHOD TO ADD OR ENSURE IS CORRECT ---
    // It fetches perfumes where the 'tastes' array field in Firestore
    // contains the specified 'tag' string.
    suspend fun getPerfumesByTag(tag: String): List<Perfume> {
        Log.d(logTag, "getPerfumesByTag called for tag: \"$tag\"")
        if (tag.isBlank()) {
            Log.w(logTag, "getPerfumesByTag called with a blank tag. Returning empty list.")
            return emptyList()
        }
        return try {
            // Ensure 'tastes' is the EXACT name of the array field in your Firestore perfume documents
            // that holds the list of tag strings.
            val snapshot = perfumesCollection
                .whereArrayContains("tastes", tag) // This is the Firestore query for array membership
                .orderBy("name", Query.Direction.ASCENDING)   // Optional: order results by name
                .get()
                .await()
            Log.d(logTag, "Fetched ${snapshot.size()} documents for tag '$tag'.")
            snapshot.documents.mapNotNull { mapDocumentToPerfume(it) }
        } catch (e: Exception) {
            Log.e(logTag, "Error fetching perfumes for tag '$tag': ${e.message}", e)
            emptyList()
        }
    }
}

