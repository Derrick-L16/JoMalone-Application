package com.example.jomalonemobileapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import java.util.UUID

@Entity(tableName = "perfumes")
data class Perfume(
    @PrimaryKey
    @get:PropertyName("id") @set:PropertyName("id")
    var id: String = UUID.randomUUID().toString(),

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "", // Default value IS PRESENT

    @get:PropertyName("stockQuantity") @set:PropertyName("stockQuantity")
    var stockQuantity: Int = 0, // Default value IS PRESENT

    @get:PropertyName("price") @set:PropertyName("price")
    var price: Double = 0.0, // Default value IS PRESENT

    @get:PropertyName("tastes") @set:PropertyName("tastes")
    var tastes: List<String>? = null,

    @get:PropertyName("imageUrl") @set:PropertyName("imageUrl")
    var imageUrl: String? = null,

    // If you added capacity, it also needs a default:
    @get:PropertyName("capacity") @set:PropertyName("capacity")
    var capacity: Int = 0 // Example if capacity is String and nullable
)