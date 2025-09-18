package com.example.jomalonemobileapplication.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jomalonemobileapplication.R
import com.example.jomalonemobileapplication.core.data.repository.CartRepositoryImpl
import com.example.jomalonemobileapplication.core.data.entity.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepositoryImpl
) : ViewModel() {

    private val _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    private val _error = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _operationInProgress = MutableStateFlow<Set<Int>>(emptySet())

    // Combine database items with local selection state
    val uiState: StateFlow<CartUiState> = combine(
        repository.getAllCartItems(),
        _selectedIds,
        _error,
        _isLoading,
        _operationInProgress
    ) { items, selectedIds, error, isLoading, operationInProgress ->
        CartUiState(
            items = items,
            selectedIds = selectedIds,
            error = error,
            isLoading = isLoading,
            operationInProgress = operationInProgress
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CartUiState()
    )

    fun addItemToCart(item: CartItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.addToCart(item)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to add item to cart: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun addCustomisedItemToCart(name: String, size: String, unitPrice: Double, quantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val customProductId = generateCustomProductId()
                repository.addToCart(
                    CartItem(
                        cartItemId = 0,
                        productId = customProductId,
                        name = name,
                        size = size,
                        imageRes = R.drawable.fragrancequiz,
                        unitPrice = unitPrice,
                        quantity = quantity,
                        isSelected = false
                    )
                )
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to add custom item: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun generateCustomProductId(): Int {
        return "custom_${System.currentTimeMillis()}".hashCode()
    }


    fun updateCartItem(item: CartItem) {
        viewModelScope.launch {
            setOperationInProgress(item.cartItemId)
            try {
                repository.updateCartItem(item)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to update item: ${e.message}"
            } finally {
                clearOperationInProgress(item.cartItemId)
            }
        }
    }

    fun updateSelection(id: Int, isSelected: Boolean) {
        viewModelScope.launch {
            repository.updateSelection(id, isSelected)
        }
    }

    fun removeItem(item: CartItem) {
        viewModelScope.launch {
            setOperationInProgress(item.cartItemId)
            try {
                repository.removeFromCart(id = item.cartItemId)
                // Remove from selection if it was selected
                _selectedIds.update { ids -> ids - item.cartItemId }
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to remove item: ${e.message}"
            } finally {
                clearOperationInProgress(item.cartItemId)
            }
        }
    }

    fun increaseQuantity(item: CartItem) {
        viewModelScope.launch {
            setOperationInProgress(item.cartItemId)
            try {
                repository.updateQuantity(id = item.cartItemId, delta = +1)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to increase quantity: ${e.message}"
            } finally {
                clearOperationInProgress(item.cartItemId)
            }
        }
    }

    fun decreaseQuantity(item: CartItem) {
        viewModelScope.launch {
            // Don't allow quantity to go below 1
            if (item.quantity <= 1) {
                _error.value = "Quantity cannot be less than 1"
                return@launch
            }

            setOperationInProgress(item.cartItemId)
            try {
                repository.updateQuantity(id = item.cartItemId, delta = -1)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to decrease quantity: ${e.message}"
            } finally {
                clearOperationInProgress(item.cartItemId)
            }
        }
    }

    fun selectCartItem(id: Int, selected: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateSelection(id, selected) // Update database

                // Update local state for immediate UI response
                _selectedIds.update { currentIds ->
                    if (selected) currentIds + id else currentIds - id
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to update selection: ${e.message}"
                // Revert local state on error
                _selectedIds.update { currentIds ->
                    if (selected) currentIds - id else currentIds + id
                }
            }
        }
    }

    fun selectAll(selected: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateAllSelection(selected) // Update database

                // Update local state
                val allIds = if (selected) {
                    uiState.value.items.map { it.cartItemId }.toSet()
                } else {
                    emptySet()
                }
                _selectedIds.value = allIds
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to update all selections: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun handleError(error: Throwable) {
        _error.value = error.message ?: "An unknown error occurred"
        _isLoading.value = false
    }

    fun dismissError() {
        _error.value = null
    }

    // Operation progress tracking
    fun setOperationInProgress(itemId: Int) {
        _operationInProgress.update { current ->
            current + itemId
        }
    }

    fun clearOperationInProgress(itemId: Int) {
        _operationInProgress.update { current ->
            current - itemId
        }
    }

    // Retry function for failed operations
    fun retryLastOperation() {
        // Clear current error and let user try again
        _error.value = null
        _isLoading.value = false
        _operationInProgress.value = emptySet()
    }

    fun clearCart() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.clearCart()
                _selectedIds.value = emptySet()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to clear cart: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}