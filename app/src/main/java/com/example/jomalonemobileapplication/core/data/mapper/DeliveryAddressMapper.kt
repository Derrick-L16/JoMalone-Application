package com.example.jomalonemobileapplication.core.data.mapper

import com.example.jomalonemobileapplication.core.data.entity.DeliveryAddress
import com.example.jomalonemobileapplication.core.data.entity.DeliveryAddressEntity


class DeliveryAddressMapper {

    // database -> ui
    fun entityToDomain(entity: DeliveryAddressEntity): DeliveryAddress {
        return DeliveryAddress(
            id = entity.id,
            name = entity.name,
            addressLine = entity.addressLine,
            city = entity.city,
            postalCode = entity.postalCode,
            state = entity.state,
            isDefault = entity.isDefault,
            userId = entity.userId
        )
    }

    // ui -> database
    fun domainToEntity(
        ui: DeliveryAddress
    ): DeliveryAddressEntity {
        return DeliveryAddressEntity(
            id = ui.id,
            name = ui.name,
            addressLine = ui.addressLine,
            city = ui.city,
            postalCode = ui.postalCode,
            state = ui.state,
            isDefault = ui.isDefault,
            userId = ui.userId
        )
    }
}