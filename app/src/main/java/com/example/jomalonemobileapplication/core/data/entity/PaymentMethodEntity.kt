package com.example.jomalonemobileapplication.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PaymentMethods")
data class PaymentMethodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val paymentType: String,
    val details: String,
    val isSelected: Boolean = false,
    val userId: String
)
