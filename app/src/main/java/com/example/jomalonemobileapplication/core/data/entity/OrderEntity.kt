package com.example.jomalonemobileapplication.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="orders")
data class OrderEntity(
    @PrimaryKey
    val orderId: String,
    val subTotal: Double,
    val tax: Double,
    val estimatedDelivery: Double,
    val total: Double,
    val orderDate: Long,
    val deliveryStatus: String = "Pending",
    val paymentMethodId: Int,
    val deliveryAddressId: Int,
    val orderItems: List<OrderItem> = emptyList(),
    val userId: String
)
