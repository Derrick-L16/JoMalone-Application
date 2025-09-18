package com.example.jomalonemobileapplication.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customizations")
data class CustomizationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String, // Firebase user ID
    val perfumeName: String,
    val baseNote: String,
    val essence: String,
    val experience: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isAddedToCart: Boolean = false
)