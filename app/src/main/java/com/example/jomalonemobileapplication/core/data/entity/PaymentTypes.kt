package com.example.jomalonemobileapplication.core.data.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Payment
import androidx.compose.ui.graphics.vector.ImageVector

object PaymentTypes {
    const val CREDIT_CARD = "credit_card"
    const val TNG_E_WALLET = "tng_e_wallet"
    const val CASH_ON_DELIVERY = "cash_on_delivery"

    val ALL_TYPES = listOf(CREDIT_CARD, TNG_E_WALLET, CASH_ON_DELIVERY)

    fun getDisplayName(type: String): String {
        return when (type) {
            CREDIT_CARD -> "Credit/Debit Card"
            TNG_E_WALLET -> "TNG e-Wallet"
            CASH_ON_DELIVERY -> "Cash on Delivery"
            else -> type
        }
    }

    fun getIcon(type: String): ImageVector {
        return when (type) {
            CREDIT_CARD -> Icons.Default.CreditCard
            TNG_E_WALLET -> Icons.Default.AccountBalanceWallet
            CASH_ON_DELIVERY -> Icons.Default.LocalAtm
            else -> Icons.Default.Payment
        }
    }

    // Helper to validate payment types
    fun isValidType(type: String): Boolean {
        return ALL_TYPES.contains(type)
    }
}