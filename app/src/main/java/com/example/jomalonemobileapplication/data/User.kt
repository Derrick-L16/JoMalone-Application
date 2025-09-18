package com.example.jomalonemobileapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
// No need to import TypeConverters or your specific Converters class here
// if you register them at the @Database level (recommended).
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    @get:PropertyName("uid") @set:PropertyName("uid")
    var uid: String, // Ensure this is non-empty and unique when saving to Room

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("email") @set:PropertyName("email")
    var email: String = "",

    @get:PropertyName("phoneNumber") @set:PropertyName("phoneNumber")
    var phoneNumber: String? = null,

    @get:PropertyName("role") @set:PropertyName("role")
    var role: String = "user",

    // Room will use a TypeConverter for this Map
    @get:PropertyName("scentPreferenceScores") @set:PropertyName("scentPreferenceScores")
    var scentPreferenceScores: Map<String, Double>? = null,

    @get:PropertyName("profileLastUpdated") @set:PropertyName("profileLastUpdated")
    var profileLastUpdated: Long = System.currentTimeMillis(),

    // Room will use a TypeConverter for this Date
    @ServerTimestamp
    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Date? = null,

    // Room will use a TypeConverter for this Date
    @ServerTimestamp
    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt")
    var updatedAt: Date? = null
) {
    // Firestore needs a no-arg constructor.
    // Room can also use this, but ensure 'uid' is valid if so.
    constructor() : this(
        uid = "", // CRITICAL: If Room uses this for inserts, an empty UID for a @PrimaryKey will cause an error.
        // Ensure 'uid' is populated with a real, unique value before saving to Room.
        name = "",
        email = "",
        phoneNumber = null,
        role = "user",
        scentPreferenceScores = null,
        profileLastUpdated = System.currentTimeMillis(),
        createdAt = null,
        updatedAt = null
    )
}
