package com.example.jomalonemobileapplication.core.data.repository

import com.example.jomalonemobileapplication.core.data.dao.PaymentMethodDao
import com.example.jomalonemobileapplication.core.data.entity.PaymentMethod
import com.example.jomalonemobileapplication.core.data.mapper.PaymentMethodMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PaymentRepository(
    private val paymentMethodDao: PaymentMethodDao,
    private val paymentMethodMapper: PaymentMethodMapper
) {

    fun getPaymentMethods(userId: String): Flow<List<PaymentMethod>> =
        paymentMethodDao.getPaymentMethods(userId).map { entities ->
            entities.map { paymentMethodMapper.entityToDomain(it) }
        }


    suspend fun getDefaultPaymentMethod(): PaymentMethod? =
        paymentMethodDao.getDefaultPaymentMethod()?.let {
                entity -> paymentMethodMapper.entityToDomain(entity)
        }

    suspend fun getSelectedPaymentMethod(userId: String? = null): PaymentMethod? =
        paymentMethodDao.getSelectedPaymentMethod(userId)?.let {
                entity -> paymentMethodMapper.entityToDomain(entity)
        }

    suspend fun selectPaymentMethod(id: Int, userId: String? = null) {
        paymentMethodDao.clearSelectedPaymentMethods(userId)
        paymentMethodDao.selectPaymentMethod(id)
    }

    suspend fun addPaymentMethod(paymentMethod: PaymentMethod): Long? =
        paymentMethodMapper.domainToEntity(paymentMethod)
            ?.let { paymentMethodDao.insertPaymentMethod(it) }

    suspend fun removePaymentMethod(paymentMethod: PaymentMethod) =
        paymentMethodMapper.domainToEntity(paymentMethod)
            ?.let { paymentMethodDao.deletePaymentMethod(it) }

    suspend fun getPaymentMethodById(id: Int?): PaymentMethod? =
        id?.let { paymentMethodDao.getPaymentMethodById(it) }
            ?.let { paymentMethodMapper.entityToDomain(it) }

}