package com.example.jomalonemobileapplication.auth

import com.google.firebase.Firebase // For Firebase.firestore and Firebase.storage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore // Extension for FirebaseFirestore
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.storage // Extension for FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Provides these at the application level
object FirebaseModule {

    @Provides
    @Singleton // Ensures only one instance of FirebaseFirestore is created
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore // Gets the default FirebaseFirestore instance
    }

    @Provides
    @Singleton // Ensures only one instance of FirebaseAuth is created and reused
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance() // Gets the default FirebaseAuth instance
    }

    // --- Example: Adding Firebase Storage ---
//    @Provides
//    @Singleton // Ensures only one instance of FirebaseStorage is created and reused
//    fun provideFirebaseStorage(): FirebaseStorage {
//        return Firebase.storage // Gets the default FirebaseStorage instance
//    }
    // ----------------------------------------

    // You could also provide a specific StorageReference if you have a common bucket/path
    // @Provides
    // @Singleton
    // fun provideUserProfileImagesReference(storage: FirebaseStorage): StorageReference {
    //     return storage.reference.child("user_profile_images")
    // }
}

