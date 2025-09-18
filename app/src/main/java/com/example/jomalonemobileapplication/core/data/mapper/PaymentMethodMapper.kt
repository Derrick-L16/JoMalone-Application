package com.example.jomalonemobileapplication.core.data.mapper

import com.example.jomalonemobileapplication.core.data.entity.PaymentMethod
import com.example.jomalonemobileapplication.core.data.entity.PaymentMethodEntity
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class PaymentMethodMapper {

    // database -> ui
    fun entityToDomain(entity: PaymentMethodEntity): PaymentMethod {
        val detailsMap = try {
            val type = object : TypeToken<Map<String, String>>() {}.type
            Gson().fromJson(entity.details, type)
        } catch (e: Exception) {
            emptyMap<String, String>()
        }

        return PaymentMethod(

            id = entity.id,
            paymentType = entity.paymentType,
            details = detailsMap,
            isSelected = entity.isSelected,
            userId = entity.userId
        )
    }

    // ui -> database
    fun domainToEntity(ui: PaymentMethod): PaymentMethodEntity? {
        val detailsJson = Gson().toJson(ui.details ?: emptyMap<String, String>())

        return ui.details?.let {
                PaymentMethodEntity(
                    id = ui.id,
                    paymentType = ui.paymentType,
                    details = detailsJson,
                    isSelected = ui.isSelected,
                    userId = ui.userId
                )
        }
    }
}