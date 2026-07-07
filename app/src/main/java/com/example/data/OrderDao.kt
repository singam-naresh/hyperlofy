package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderEntity)

    @Query("SELECT * FROM orders ORDER BY created_at DESC")
    suspend fun getAll(): List<OrderEntity>
}
