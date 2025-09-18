package com.example.jomalonemobileapplication.core.data.mapper

import com.example.jomalonemobileapplication.core.data.entity.CartItem
import com.example.jomalonemobileapplication.core.data.entity.Order
import com.example.jomalonemobileapplication.core.data.entity.OrderItem

fun CartItem.toOrderItem(): OrderItem {
    return OrderItem(
        id = productId,   // or use cartItemId if sizes must be unique
        name = name,
        size = size,
        quantity = quantity,
        price = unitPrice,
        imageRes = imageRes
    )
}

fun List<CartItem>.toOrderItems(): List<OrderItem> {
    return this.filter { it.isSelected }.map { it.toOrderItem() }
}


fun buildOrder(
    userId: String,
    cartItems: List<CartItem>,
    paymentMethodId: Int,
    deliveryAddressId: Int
): Order {
    val selectedItems = cartItems.toOrderItems()
    val subTotal = selectedItems.sumOf { it.totalPrice }
    val tax = subTotal * 0.06 // Example: 6% GST
    val deliveryFee = 10.0    // Example: flat delivery fee
    val total = subTotal + tax + deliveryFee

    return Order(
        orderId = "", // Firebase will generate ID
        subTotal = subTotal,
        tax = tax,
        estimatedDelivery = deliveryFee,
        total = total,
        orderItems = selectedItems,
        userId = userId,
        paymentMethodId = paymentMethodId,
        deliveryAddressId = deliveryAddressId
    )
}