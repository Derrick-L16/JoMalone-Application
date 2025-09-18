package com.example.jomalonemobileapplication.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.jomalonemobileapplication.core.data.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {

    @Insert
    suspend fun insertCartItem(item: CartItemEntity)

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Query("UPDATE cartItems SET isSelected = :isSelected WHERE cartItemId = :id")
    suspend fun updateSelection(id: Int, isSelected: Boolean)

    @Query("UPDATE cartItems SET isSelected = :isSelected")
    suspend fun updateAllItemsSelection(isSelected: Boolean)

    @Query("DELETE FROM cartItems WHERE cartItemId = :id")
    suspend fun deleteCartItem(id: Int)

    @Query("SELECT * FROM cartItems WHERE cartItemId = :id")
    fun getCartItem(id: Int): Flow<CartItemEntity>

    @Query("SELECT * FROM cartItems")
    fun getAllItems(): Flow<List<CartItemEntity>>

    @Query("UPDATE cartItems SET quantity = quantity + :delta WHERE cartItemId = :id")
    suspend fun updateQuantity(id: Int, delta: Int)

    @Query("DELETE FROM cartItems")
    suspend fun clearCart()

    @Query("SELECT * FROM cartItems WHERE isSelected = 1")
    suspend fun getCurrentCartItems(): List<CartItemEntity>

    @Query("DELETE FROM cartItems WHERE isSelected = 1")
    suspend fun clearSelectedItems()


}