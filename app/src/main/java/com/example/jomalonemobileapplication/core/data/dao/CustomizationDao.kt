package com.example.jomalonemobileapplication.core.data.dao

import androidx.room.*
import com.example.jomalonemobileapplication.core.data.entity.CustomizationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomizationDao {

    @Query("SELECT * FROM customizations WHERE userId = :userId ORDER BY createdAt DESC")
    fun getCustomizationsByUser(userId: String): Flow<List<CustomizationEntity>>

    @Query("SELECT * FROM customizations WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestCustomization(userId: String): CustomizationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomization(customization: CustomizationEntity)

    @Update
    suspend fun updateCustomization(customization: CustomizationEntity)

    @Delete
    suspend fun deleteCustomization(customization: CustomizationEntity)

    @Query("DELETE FROM customizations WHERE userId = :userId")
    suspend fun deleteAllUserCustomizations(userId: String)
}