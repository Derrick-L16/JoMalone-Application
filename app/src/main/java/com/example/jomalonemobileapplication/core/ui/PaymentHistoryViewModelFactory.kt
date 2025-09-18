package com.example.jomalonemobileapplication.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jomalonemobileapplication.core.data.repository.AddressRepository
import com.example.jomalonemobileapplication.core.data.repository.CartRepositoryImpl
import com.example.jomalonemobileapplication.core.data.repository.CheckoutRepository
import com.example.jomalonemobileapplication.core.data.repository.OrderRepository
import com.example.jomalonemobileapplication.core.data.repository.PaymentRepository
import com.example.jomalonemobileapplication.feature.login.ui.AuthViewModel

class PaymentHistoryViewModelFactory(
    private val order: OrderRepository,
    private val authViewModel: AuthViewModel,
    private val payment: PaymentRepository

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentHistoryViewModel::class.java)) {
            return PaymentHistoryViewModel(order, payment, authViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

