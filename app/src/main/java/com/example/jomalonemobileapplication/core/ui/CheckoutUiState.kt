package com.example.jomalonemobileapplication.core.ui

import com.example.jomalonemobileapplication.core.data.entity.CartItem
import com.example.jomalonemobileapplication.core.data.entity.DeliveryAddress
import com.example.jomalonemobileapplication.core.data.entity.Order
import com.example.jomalonemobileapplication.core.data.entity.PaymentMethod

data class CheckoutUiState(
    val cartItems: List<CartItem> = emptyList(),
    val order: Order = Order(estimatedDelivery = 0.0, subTotal = 0.0, tax = 0.0, total = 0.0),
    val paymentMethods: List<PaymentMethod>? = emptyList(),
    val selectedPaymentMethod: PaymentMethod? = null,
    val deliveryAddresses: List<DeliveryAddress> = emptyList(),
    val defaultAddress: DeliveryAddress? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val checkoutSuccess: String? = null
)
