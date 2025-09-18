package com.example.jomalonemobileapplication.core.data.entity

import com.example.jomalonemobileapplication.R

data class CartItem(
    val cartItemId: Int,    // Unique ID for the cart item with different size
    val productId: Int,              // Unique ID for the product
    val name: String,            // "Raspberry Ripple Cologne"
    val size: String,            // "100 ml"
    val imageRes: Int,        // Link to product image
    val unitPrice: Double,       // 700.00
    val quantity: Int,           // 1
    val isSelected: Boolean      // Whether the checkbox is ticked
){
    val totalPrice: Double
        get() = unitPrice * quantity
}
