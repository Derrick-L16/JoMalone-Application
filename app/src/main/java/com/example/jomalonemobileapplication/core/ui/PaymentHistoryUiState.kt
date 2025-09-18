package com.example.jomalonemobileapplication.core.ui

data class PaymentHistoryUiState(
    val orders: List<OrderWithDetails> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedOrder: OrderWithDetails? = null
)