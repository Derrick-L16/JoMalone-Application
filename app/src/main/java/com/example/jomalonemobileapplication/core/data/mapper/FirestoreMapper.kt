package com.example.jomalonemobileapplication.core.data.mapper

import com.example.jomalonemobileapplication.core.data.entity.CartItem
import com.example.jomalonemobileapplication.core.data.entity.Order
import com.example.jomalonemobileapplication.core.data.entity.OrderEntity
import com.example.jomalonemobileapplication.core.data.entity.OrderItem

// --- CartItem ---
fun CartItem.toMap(): Map<String, Any> = mapOf(
    "cartItemId" to cartItemId,
    "productId" to productId,
    "name" to name,
    "size" to size,
    "imageRes" to imageRes,
    "unitPrice" to unitPrice,
    "quantity" to quantity,
    "isSelected" to isSelected,
    "totalPrice" to totalPrice
)

// --- OrderEntity ---
fun OrderEntity.toMap(): Map<String, Any> = mapOf(
    "orderId" to orderId,
    "subTotal" to subTotal,
    "tax" to tax,
    "estimatedDelivery" to estimatedDelivery,
    "total" to total,
    "orderDate" to orderDate,
    "paymentMethodId" to paymentMethodId,
    "deliveryAddressId" to deliveryAddressId,
    "userId" to userId,
    "orderItems" to orderItems.map { it.toMap() }
)

// --- OrderItem ---
fun OrderItem.toMap(): Map<String, Any> = mapOf(
    "id" to id,
    "name" to name,
    "size" to size,
    "quantity" to quantity,
    "price" to price,
    "imageRes" to imageRes
)

fun OrderEntity.toOrder(): Order = Order(
    orderId = orderId,
    subTotal = subTotal,
    tax = tax,
    estimatedDelivery = estimatedDelivery,
    total = total,
    orderDate = orderDate,
    deliveryStatus = deliveryStatus,
    paymentMethodId = paymentMethodId,
    deliveryAddressId = deliveryAddressId,
    userId = userId,
    orderItems = orderItems
)


fun Order.toEntity(): OrderEntity? = userId?.let {
    OrderEntity(
        orderId = orderId,
        subTotal = subTotal,
        tax = tax,
        estimatedDelivery = estimatedDelivery,
        total = total,
        orderDate = orderDate,
        deliveryStatus = deliveryStatus,
        paymentMethodId = paymentMethodId,
        deliveryAddressId = deliveryAddressId,
        userId = it,
        orderItems = orderItems
    )
}


