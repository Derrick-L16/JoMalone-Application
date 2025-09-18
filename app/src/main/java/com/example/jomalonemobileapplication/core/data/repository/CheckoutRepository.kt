package com.example.jomalonemobileapplication.core.data.repository

import com.example.jomalonemobileapplication.core.data.entity.OrderEntity
import com.example.jomalonemobileapplication.core.data.mapper.toOrder
import com.example.jomalonemobileapplication.core.data.mapper.toOrderItem



class CheckoutRepository(
    private val cartRepository: CartRepositoryImpl, // Your existing cart repo
    private val orderRepository: OrderRepository,
) {

    suspend fun checkoutOrder(
        paymentMethodId: Int,
        deliveryAddressId: Int,
        userId: String
    ): Result<String> {
        return try {
            // 1. Get selected items
            val cartItems = cartRepository.getCurrentCartItems()

            // 2. Calculate totals
            val subTotal = cartItems.sumOf { it.totalPrice }
            val tax = subTotal * 0.06
            val deliveryFee = 5.0
            val total = subTotal + tax + deliveryFee

            // 3. Build order entity
            val orderId = generateOrderId()
            val orderEntity = OrderEntity(
                orderId = orderId,
                subTotal = subTotal,
                tax = tax,
                estimatedDelivery = deliveryFee,
                total = total,
                orderDate = System.currentTimeMillis(),
                paymentMethodId = paymentMethodId,
                deliveryAddressId = deliveryAddressId,
                orderItems = cartItems.map { it.toOrderItem() },
                userId = userId
            )

            // 4. Save to Room + Firestore
            orderRepository.insertOrder(orderEntity)
            orderRepository.storeOrder(orderEntity.toOrder())

            // 5. Clear cart
            cartRepository.clearSelectedItems()

            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateOrderId(): String =
        "ORDER_${System.currentTimeMillis()}_${(1000..9999).random()}"

}