package com.example.jomalonemobileapplication.core.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.jomalonemobileapplication.core.data.entity.DeliveryAddressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryAddressDao {
    @Query("SELECT * FROM deliveryAddresses WHERE userId = :userId OR userId IS NULL")
    fun getDeliveryAddresses(userId: String?): Flow<List<DeliveryAddressEntity>>

    @Query("SELECT * FROM deliveryAddresses WHERE isDefault = 1 AND (userId = :userId OR userId IS NULL) LIMIT 1")
    suspend fun getDefaultAddress(userId: String?): DeliveryAddressEntity?

    @Query("SELECT * FROM deliveryAddresses WHERE id = :id")
    suspend fun getAddressById(id: Int): DeliveryAddressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: DeliveryAddressEntity): Long

    @Update
    suspend fun updateAddress(address: DeliveryAddressEntity)

    @Delete
    suspend fun deleteAddress(address: DeliveryAddressEntity)

    @Query("UPDATE deliveryAddresses SET isDefault = 0 WHERE userId = :userId OR userId IS NULL")
    suspend fun clearDefaultAddresses(userId: String?)

    @Query("UPDATE deliveryAddresses SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultAddress(id: Int)
}