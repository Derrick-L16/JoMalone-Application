package com.example.jomalonemobileapplication.core.data.repository

import com.example.jomalonemobileapplication.core.data.dao.OrderDao
import com.example.jomalonemobileapplication.core.data.entity.Order
import com.example.jomalonemobileapplication.core.data.entity.OrderEntity
import com.example.jomalonemobileapplication.core.data.mapper.OrderMapper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val orderDao: OrderDao,
    private val orderMapper: OrderMapper = OrderMapper()
){
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Save order in local database
    suspend fun insertOrder(order: OrderEntity) = orderDao.insertOrder(order)

    private suspend fun deleteOrder(orderId: String) = orderDao.getOrderById(orderId)
        ?.let { orderDao.deleteOrder(it) }

    // Get order history
    fun getUserOrders(userId: String? = null): Flow<List<OrderEntity>> =
        orderDao.getOrdersByUser(userId)

    suspend fun cancelOrder(orderId: String) {
        orderDao.updateOrderStatus(orderId, "Cancelled")
    }

    // Save order in firestore
    suspend fun storeOrder(order: Order): Result<String> {
        return try {
            val orderId = firestore.collection("orders").document().id

            firestore.collection("orders")
                .document(orderId)
                .set(order.copy(orderId = orderId).toMap())
                .await()

            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderById(orderId: String): Order? {
        val entity = orderDao.getOrderById(orderId)
        return entity?.let { orderMapper.entityToDomain(it) }
    }

    // Get order history from firestore
    suspend fun getUserOrdersFromFirestore(userId: String): Result<List<Map<String, Any>>> {
        return try {
            val snapshot = firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val orders = snapshot.documents.map { it.data ?: emptyMap() }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}