package com.example.jomalonemobileapplication.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DeliveryAddresses")
data class DeliveryAddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val addressLine: String,
    val city: String,
    val postalCode: String,
    val state: String,
    val isDefault: Boolean = false,
    val userId: String
)
