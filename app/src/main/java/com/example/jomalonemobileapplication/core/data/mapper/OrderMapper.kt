package com.example.jomalonemobileapplication.core.data.mapper

import com.example.jomalonemobileapplication.core.data.entity.Order
import com.example.jomalonemobileapplication.core.data.entity.OrderEntity

class OrderMapper {

    // database -> ui
    fun entityToDomain(entity: OrderEntity): Order {
        return Order(
            orderId = entity.orderId,
            subTotal = entity.subTotal,
            tax = entity.tax,
            estimatedDelivery = entity.estimatedDelivery,
            total = entity.total,
            orderDate = entity.orderDate,
            deliveryStatus = entity.deliveryStatus,
            paymentMethodId = entity.paymentMethodId,
            deliveryAddressId = entity.deliveryAddressId,
            userId = entity.userId
        )
    }

    // ui -> database
    fun domainToEntity(order: Order): OrderEntity? {
        return order.userId?.let {
            OrderEntity(
                orderId = order.orderId,
                subTotal = order.subTotal,
                tax = order.tax,
                estimatedDelivery = order.estimatedDelivery,
                total = order.total,
                orderDate = System.currentTimeMillis(),
                deliveryStatus = order.deliveryStatus,
                paymentMethodId = order.paymentMethodId,
                deliveryAddressId = order.deliveryAddressId,
                orderItems = order.orderItems,
                userId = it
            )
        }
    }
}