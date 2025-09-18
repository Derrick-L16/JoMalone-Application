package com.example.jomalonemobileapplication.core.data.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.ui.graphics.vector.ImageVector

data class PaymentMethod(
    val id: Int = 0,
    val paymentType: String = "",
    val details: Map<String, String>? = null,
    val isSelected: Boolean = false,
    val userId: String = "",

) {
    val displayName: String
        get() = when (paymentType) {
            "credit_card" -> "Credit/Debit Card"
            "tng_e_wallet" -> "TNG e-Wallet"
            "cash_on_delivery" -> "Cash on Delivery"
            else -> paymentType
        }

    val icon: ImageVector
        get() = when (paymentType) {
            "credit_card" -> Icons.Default.CreditCard
            "tng_e_wallet" -> Icons.Default.AccountBalanceWallet
            "cash_on_delivery" -> Icons.Default.LocalAtm
            else -> Icons.Default.Payment
        }

    // Extract specific details with fallbacks
    val lastFourDigits: String?
        get() = details?.get("lastFourDigits") ?: details?.get("cardNumber")?.takeLast(4)

    val phoneNumber: String?
        get() = details?.get("phoneNumber")
}