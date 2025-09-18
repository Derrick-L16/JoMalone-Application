package com.example.jomalonemobileapplication.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cartItems")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val cartItemId: Int = 0,    // Unique ID for the cart item with different size
    val productId: Int,              // Unique ID for the product
    val productName: String,            // "Raspberry Ripple Cologne"
    val size: String,            // "100 ml"
    val imageRes: Int,        // Link to product image
    val unitPrice: Double,       // 700.00
    val quantity: Int,           // 1
    val isSelected: Boolean      // Whether the checkbox is ticked
){
    val totalPrice: Double
        get() = unitPrice * quantity
}
