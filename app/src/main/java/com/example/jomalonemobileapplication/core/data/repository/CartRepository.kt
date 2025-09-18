package com.example.jomalonemobileapplication.core.data.repository

import com.example.jomalonemobileapplication.core.data.entity.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getAllCartItems(): Flow<List<CartItem>>
    suspend fun addToCart(item: CartItem)
    suspend fun removeFromCart(id: Int)
    suspend fun updateQuantity(id: Int, delta: Int)
    suspend fun getCurrentCartItems(): List<CartItem>
    suspend fun clearSelectedItems()
    suspend fun updateCartItem(item: CartItem)
    suspend fun updateSelection(id: Int, isSelected: Boolean)
    suspend fun updateAllSelection(isSelected: Boolean)
    suspend fun storeCart(userId: String, cartItems: List<CartItem>): Result<String>
    suspend fun getCart(userId: String): Result<Map<String, Any>>
}
