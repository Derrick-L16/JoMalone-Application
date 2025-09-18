package com.example.jomalonemobileapplication.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jomalonemobileapplication.core.data.entity.CartItem
import com.example.jomalonemobileapplication.core.data.entity.DeliveryAddress
import com.example.jomalonemobileapplication.core.data.entity.Order
import com.example.jomalonemobileapplication.core.data.entity.PaymentMethod
import com.example.jomalonemobileapplication.core.data.repository.AddressRepository
import com.example.jomalonemobileapplication.core.data.repository.CartRepositoryImpl
import com.example.jomalonemobileapplication.core.data.repository.CheckoutRepository
import com.example.jomalonemobileapplication.core.data.repository.PaymentRepository
import com.example.jomalonemobileapplication.feature.login.ui.AuthViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel (
    private val cart: CartRepositoryImpl,
    private val checkout: CheckoutRepository,
    private val address: AddressRepository,
    private val payment: PaymentRepository,
    private val authViewModel: AuthViewModel
): ViewModel() {
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()
    init {
        collectDataFlows()
    }

    private fun collectDataFlows() {
        viewModelScope.launch {
            val userId = authViewModel.getCurrentUserId()

            if (userId != null) {
                // Get cart items once (suspend function, called within coroutine)
                val cartItems = cart.getCurrentCartItems()

                // Update UI with cart items immediately
                updateUiStateWithCartItems(cartItems, userId)

                // Collect the flows that can change
                combine(
                    address.getDeliveryAddresses(userId),
                    payment.getPaymentMethods(userId)
                ) { addresses, paymentMethods ->
                    Pair(addresses, paymentMethods)
                }.collect { (addresses, paymentMethods) ->
                    updateUiStateWithAddressesAndPayments(addresses, paymentMethods)
                }
            } else {
                _uiState.update { it.copy(error = "User not authenticated", isLoading = false) }
            }
        }
    }

    private fun updateUiStateWithCartItems(cartItems: List<CartItem>, userId: String) {
        val subTotal = cartItems.sumOf { it.totalPrice }
        val tax = subTotal * 0.06
        val delivery = 5.0
        val total = subTotal + tax + delivery

        val order = Order(
            subTotal = subTotal,
            tax = tax,
            estimatedDelivery = delivery,
            total = total,
            orderItems = emptyList(),
            userId = userId
        )

        _uiState.update { currentState ->
            currentState.copy(
                cartItems = cartItems,
                order = order
            )
        }
    }

    private fun updateUiStateWithAddressesAndPayments(
        addresses: List<DeliveryAddress>,
        paymentMethods: List<PaymentMethod>
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                deliveryAddresses = addresses,
                paymentMethods = paymentMethods,
                isLoading = false
            )
        }
    }

    fun addDeliveryAddress(deliveryAddress: DeliveryAddress) {
        viewModelScope.launch {
            try {
                address.addDeliveryAddress(deliveryAddress)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun removeDeliveryAddress(deliveryAddress: DeliveryAddress) {
        viewModelScope.launch {
            try {
                address.removeDeliveryAddress(deliveryAddress)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun addPaymentMethod(paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            try {
                payment.addPaymentMethod(paymentMethod)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun removePaymentMethod(paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            try {
                payment.removePaymentMethod(paymentMethod)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun selectPaymentMethod(id: Int) {
        viewModelScope.launch {
            try {
                payment.selectPaymentMethod(id)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setDefaultAddress(id: Int) {
        viewModelScope.launch {
            try {
                address.setDefaultAddress(id)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    suspend fun processCheckout(
        paymentMethodId: Int,
        deliveryAddressId: Int,
        userId: String
    ): Result<String> {
        return checkout.checkoutOrder(
            paymentMethodId,
            deliveryAddressId,
            userId
        )
    }

    fun processCheckoutUi(paymentMethodId: Int, deliveryAddressId: Int) {
        viewModelScope.launch {
            val userId = authViewModel.getCurrentUserId() ?: return@launch
            val result = processCheckout(paymentMethodId, deliveryAddressId, userId)

            result.fold(
                onSuccess = { orderId ->
                    _uiState.update { it.copy(checkoutSuccess = orderId) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
            )
        }
    }


    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearCheckoutSuccess() {
        _uiState.update { it.copy(checkoutSuccess = null) }
    }
}