package com.example.jomalonemobileapplication.core.data.entity

data class DeliveryAddress(
    val id: Int,
    val name: String,
    val addressLine: String,
    val city: String,
    val postalCode: String,
    val state: String,
    val isDefault: Boolean = false,
    val userId: String
)