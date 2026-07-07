package com.example.data

import android.content.Context

class OrderRepository private constructor(private val dao: OrderDao) {

    suspend fun insert(order: Order) {
        dao.insert(order.toEntity())
    }

    suspend fun update(order: Order) {
        dao.insert(order.toEntity())
    }

    suspend fun getAll(): List<Order> {
        return dao.getAll().map { it.toOrder() }
    }

    suspend fun getById(id: String): Order? {
        return dao.getAll().map { it.toOrder() }.firstOrNull { it.id == id }
    }

    companion object {
        @Volatile
        private var INSTANCE: OrderRepository? = null

        fun getInstance(context: Context): OrderRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val repo = OrderRepository(db.orderDao())
                INSTANCE = repo
                repo
            }
        }
    }
}
