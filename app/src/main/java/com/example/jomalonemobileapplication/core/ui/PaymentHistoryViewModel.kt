package com.example.jomalonemobileapplication.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jomalonemobileapplication.core.data.entity.CartItem
import com.example.jomalonemobileapplication.core.data.entity.PaymentMethod
import com.example.jomalonemobileapplication.core.data.repository.OrderRepository
import com.example.jomalonemobileapplication.core.data.repository.PaymentRepository
import com.example.jomalonemobileapplication.feature.login.ui.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PaymentHistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentHistoryUiState())
    val uiState: StateFlow<PaymentHistoryUiState> = _uiState.asStateFlow()

    init {
        loadUserOrders()
    }

    private fun loadUserOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val userId = authViewModel.getCurrentUserId()
                orderRepository.getUserOrders(userId).collect { orderEntities ->
                    val ordersWithDetails = orderEntities.mapNotNull { orderEntity ->
                        // Get payment method and items for each order
                        val paymentMethod = getPaymentMethodById(orderEntity.paymentMethodId)
                        val items = getOrderItems(orderEntity.orderId)

                        if (paymentMethod != null) {
                            OrderWithDetails(
                                order = orderEntity,
                                paymentMethod = paymentMethod,
                                items = items
                            )
                        } else {
                            null
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        orders = ordersWithDetails,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to load orders",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private suspend fun getPaymentMethodById(id: Int?): PaymentMethod? {
        // Implement based on your PaymentRepository
        return paymentRepository.getPaymentMethodById(id) ?: paymentRepository.getDefaultPaymentMethod()
    }

    private fun getOrderItems(orderId: String): List<CartItem> {
        // You'll need to implement this based on how you store order items
        return emptyList() // Placeholder
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            try {
                orderRepository.cancelOrder(orderId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to cancel order"
                )
            }
        }
    }


}