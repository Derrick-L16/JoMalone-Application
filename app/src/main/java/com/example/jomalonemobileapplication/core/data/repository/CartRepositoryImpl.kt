package com.example.jomalonemobileapplication.core.data.repository

import com.example.jomalonemobileapplication.core.data.dao.CartItemDao
import com.example.jomalonemobileapplication.core.data.mapper.CartItemMapper
import com.example.jomalonemobileapplication.core.data.entity.CartItem
import com.example.jomalonemobileapplication.core.data.mapper.toMap
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


// Implementation of the CartRepository interface
// As an interface for handling data operations related to the cart items ( take from ui and use dao )
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartItemDao,
    private val cartItemMapper: CartItemMapper
) : CartRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun getAllCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllItems().map { entityList ->
            entityList.map { entity ->
                cartItemMapper.entityToDomain(entity)
            }
        }
    }
    override suspend fun addToCart(item: CartItem) {
        cartDao.insertCartItem(cartItemMapper.domainToEntity(item))
    }

    override suspend fun updateCartItem(item: CartItem) {
        cartDao.updateCartItem(cartItemMapper.domainToEntity(item))
    }

    override suspend fun updateSelection(id: Int, isSelected: Boolean) {
        cartDao.updateSelection(id, isSelected)
    }

    override suspend fun updateAllSelection(isSelected: Boolean) {
        cartDao.updateAllItemsSelection(isSelected)
    }

    override suspend fun removeFromCart(id: Int) {
        cartDao.deleteCartItem(id)
    }

    override suspend fun updateQuantity(id: Int, delta: Int) {
        cartDao.updateQuantity(id, delta)
    }

    override suspend fun getCurrentCartItems(): List<CartItem> {
        return cartDao.getCurrentCartItems().map { entity ->
            cartItemMapper.entityToDomain(entity)
        }
    }

    override suspend fun clearSelectedItems() {
        cartDao.clearSelectedItems()
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }


    // Save cart
    override suspend fun storeCart(userId: String, cartItems: List<CartItem>): Result<String> {
        return try {
            val cartData = hashMapOf(
                "userId" to userId,
                "items" to cartItems.map { it.toMap() },
                "total" to cartItems.sumOf { it.totalPrice },
                "lastUpdated" to com.google.firebase.Timestamp.now()
            )

            firestore.collection("carts")
                .document("cart_$userId")
                .set(cartData)
                .await()

            Result.success("Cart stored successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get cart from firestore
    override suspend fun getCart(userId: String): Result<Map<String, Any>> {
        return try {
            val doc = firestore.collection("carts").document("cart_$userId").get().await()
            if (doc.exists()) Result.success(doc.data ?: emptyMap())
            else Result.failure(Exception("Cart not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}