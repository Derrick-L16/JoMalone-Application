package com.example.jomalonemobileapplication.core.data.mapper

import com.example.jomalonemobileapplication.core.data.entity.CartItemEntity
import com.example.jomalonemobileapplication.core.data.entity.CartItem

class CartItemMapper {

    // database -> ui
    fun entityToDomain(entity: CartItemEntity): CartItem {
        return CartItem(
            cartItemId = entity.cartItemId,
            productId = entity.productId,
            name = entity.productName,            // "Raspberry Ripple Cologne"
            size = entity.size,            // "100 ml"
            imageRes = entity.imageRes,       // Link to product image
            unitPrice= entity.unitPrice,       // 700.00
            quantity = entity.quantity,           // 1
            isSelected = entity.isSelected      // Whether the checkbox is ticked
        )

    }

    // ui -> database
    fun domainToEntity(ui: CartItem): CartItemEntity {
        return CartItemEntity(
            cartItemId = ui.cartItemId,
            productId = ui.productId,
            productName = ui.name,
            size = ui.size,
            imageRes = ui.imageRes,
            unitPrice = ui.unitPrice,
            quantity = ui.quantity,
            isSelected = ui.isSelected
        )
    }

    fun domainListToEntityList(uiList: List<CartItem>): List<CartItemEntity> {
        return uiList.map { domainToEntity(it) }
    }
}