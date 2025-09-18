package com.example.jomalonemobileapplication

import androidx.room.TypeConverter
import com.example.jomalonemobileapplication.core.data.entity.OrderItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromOrderItemList(orderItems: List<OrderItem>?): String {
        return gson.toJson(orderItems)
    }

    @TypeConverter
    fun toOrderItemList(data: String): List<OrderItem> {
        if (data.isEmpty()) return emptyList()
        val listType = object : TypeToken<List<OrderItem>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromString(value: String): Map<String, String> {
        return try {
            val type = object : TypeToken<Map<String, String>>() {}.type
            Gson().fromJson(value, type)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    @TypeConverter
    fun fromMap(map: Map<String, String>): String {
        return Gson().toJson(map)
    }

}