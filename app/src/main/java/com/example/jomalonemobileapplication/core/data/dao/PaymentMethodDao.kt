package com.example.jomalonemobileapplication.core.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.jomalonemobileapplication.core.data.entity.PaymentMethodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentMethodDao {
    @Query("SELECT * FROM paymentMethods WHERE userId = :userId OR userId IS NULL")
    fun getPaymentMethods(userId: String?): Flow<List<PaymentMethodEntity>>

    @Query("SELECT * FROM paymentMethods WHERE isSelected = 1 AND (userId = :userId OR userId IS NULL) LIMIT 1")
    suspend fun getSelectedPaymentMethod(userId: String?): PaymentMethodEntity?

    @Query("SELECT * FROM paymentMethods WHERE isSelected = 1 LIMIT 1")
    suspend fun getDefaultPaymentMethod(): PaymentMethodEntity?

    @Query("SELECT * FROM paymentMethods WHERE id = :id")
    suspend fun getPaymentMethodById(id: Int): PaymentMethodEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentMethod(paymentMethod: PaymentMethodEntity): Long

    @Update
    suspend fun updatePaymentMethod(paymentMethod: PaymentMethodEntity)

    @Delete
    suspend fun deletePaymentMethod(paymentMethod: PaymentMethodEntity)

    @Query("UPDATE paymentMethods SET isSelected = 0 WHERE userId = :userId OR userId IS NULL")
    suspend fun clearSelectedPaymentMethods(userId: String?)

    @Query("UPDATE paymentMethods SET isSelected = 1 WHERE id = :id")
    suspend fun selectPaymentMethod(id: Int)

}