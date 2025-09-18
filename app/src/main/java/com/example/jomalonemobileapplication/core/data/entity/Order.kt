package com.example.jomalonemobileapplication.core.data.entity


data class Order(
    val orderId: String = "",
    val subTotal: Double,
    val tax: Double,
    val estimatedDelivery: Double,
    val total: Double,
    val orderDate: Long = System.currentTimeMillis(),
    val deliveryStatus: String = "Pending",
    val paymentMethodId: Int = 0,
    val deliveryAddressId: Int = 0,
    val orderItems: List<OrderItem> = emptyList(),
    val userId: String? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "orderId" to orderId,
            "subTotal" to subTotal,
            "tax" to tax,
            "estimatedDelivery" to estimatedDelivery,
            "total" to total,
            "orderDate" to orderDate,
            "deliveryStatus" to deliveryStatus,
            "paymentMethodId" to paymentMethodId,
            "deliveryAddressId" to deliveryAddressId,
            "orderItems" to orderItems.map { it.toMap() },
            "userId" to userId
        )
    }
}

