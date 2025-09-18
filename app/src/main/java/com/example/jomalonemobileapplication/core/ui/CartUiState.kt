package com.example.jomalonemobileapplication.core.ui

import com.example.jomalonemobileapplication.core.data.entity.CartItem
data class CartUiState(
    val items: List<CartItem> = emptyList(), //SampleData.cartItems,
    val selectedIds: Set<Int> = emptySet(),
    val error: String? = null,
    val isLoading: Boolean = false,
    val operationInProgress: Set<Int> = emptySet()
) {
    val selectedItems = items.filter { it.cartItemId in selectedIds }
    val total = selectedItems.sumOf { it.quantity * it.unitPrice }
    val isEmpty: Boolean = items.isEmpty()
    val isAllSelected: Boolean = items.isNotEmpty() && selectedIds.size == items.size

    val hasOperationsInProgress: Boolean get() = operationInProgress.isNotEmpty()
}
