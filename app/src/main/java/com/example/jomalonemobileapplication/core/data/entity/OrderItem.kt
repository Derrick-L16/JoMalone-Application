package com.example.jomalonemobileapplication.core.data.entity

data class OrderItem(
    val id: Int,
    val name: String,
    val size: String,
    val quantity: Int,
    val price: Double,
    val imageRes: Int
) {
    val totalPrice: Double
        get() = price * quantity

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "size" to size,
            "quantity" to quantity,
            "price" to price,
            "totalPrice" to totalPrice,
            "imageRes" to imageRes
        )
    }

}
