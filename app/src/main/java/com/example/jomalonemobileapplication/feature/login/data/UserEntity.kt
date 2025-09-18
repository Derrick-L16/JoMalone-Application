package com.example.jomalonemobileapplication.feature.login.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val gmail: String,
    val password: String,
    val name: String,
    val phoneNumber: String
)