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

    // Combine database items with local selection state
    val uiState: StateFlow<CartUiState> = combine(
        repository.getAllCartItems(),
        _selectedIds,
        _error
    ) { items, selectedIds, error ->
        CartUiState(
            items = items,
            selectedIds = selectedIds,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CartUiState()
    )

    fun addItemToCart(item: CartItem) {
        viewModelScope.launch {
            try {
                repository.addToCart(item)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun addCustomisedItemToCart(name: String, size: String, unitPrice: Double, quantity: Int) {
        viewModelScope.launch {
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
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    private fun generateCustomProductId(): Int {
        // More robust custom ID generation
        return "custom_${System.currentTimeMillis()}".hashCode()
    }


    fun updateCartItem(item: CartItem) {
        viewModelScope.launch {
            repository.updateCartItem(item)
        }
    }

    fun updateSelection(id: Int, isSelected: Boolean) {
        viewModelScope.launch {
            repository.updateSelection(id, isSelected)
        }
    }

    fun removeItem(item: CartItem) {
        viewModelScope.launch {
            repository.removeFromCart(id = item.cartItemId)
        }
    }

    fun increaseQuantity(item: CartItem) {
        viewModelScope.launch {
            repository.updateQuantity(id = item.cartItemId, delta = +1)
        }
    }

    fun decreaseQuantity(item: CartItem) {
        viewModelScope.launch {
            repository.updateQuantity(id = item.cartItemId, delta = -1)
        }
    }
    
    fun selectCartItem(id: Int, selected: Boolean) {
        viewModelScope.launch {
            repository.updateSelection(id, selected) // Update database
        }
        // Also update local state for immediate UI response
        val current = _selectedIds.value.toMutableSet()
        if (selected) current.add(id) else current.remove(id)
        _selectedIds.value = current
    }

    fun selectAll(selected: Boolean) {
        viewModelScope.launch {
            repository.updateAllSelection(selected) // Update database
        }
        // Update local state
        val allIds = if (selected) uiState.value.items.map { it.cartItemId }.toSet() else emptySet()
        _selectedIds.value = allIds
    }
}