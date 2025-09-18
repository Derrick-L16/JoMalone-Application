package com.example.jomalonemobileapplication.core.data.repository

import com.example.jomalonemobileapplication.core.data.dao.DeliveryAddressDao
import com.example.jomalonemobileapplication.core.data.entity.DeliveryAddress
import com.example.jomalonemobileapplication.core.data.mapper.DeliveryAddressMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AddressRepository (
    private val deliveryAddressDao: DeliveryAddressDao,
    private val deliveryAddressMapper: DeliveryAddressMapper


){
    fun getDeliveryAddresses(userId: String? = null): Flow<List<DeliveryAddress>> =
        deliveryAddressDao.getDeliveryAddresses(userId).map { entities ->
            entities.map { deliveryAddressMapper.entityToDomain(it) }
        }

    suspend fun getDefaultAddress(userId: String? = null): DeliveryAddress? =
        deliveryAddressDao.getDefaultAddress(userId)?.let {
                entity -> deliveryAddressMapper.entityToDomain(entity)
        }

    suspend fun setDefaultAddress(id: Int, userId: String? = null) {
        deliveryAddressDao.clearDefaultAddresses(userId)
        deliveryAddressDao.setDefaultAddress(id)
    }

    suspend fun addDeliveryAddress(address: DeliveryAddress): Long =
        deliveryAddressDao.insertAddress( deliveryAddressMapper.domainToEntity(address) )

    suspend fun removeDeliveryAddress(address: DeliveryAddress) =
        deliveryAddressDao.deleteAddress(deliveryAddressMapper.domainToEntity(address))

}