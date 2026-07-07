package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "pickup_address") val pickupAddress: String,
    @ColumnInfo(name = "drop_address") val dropAddress: String,
    @ColumnInfo(name = "item_name") val itemName: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "item_value") val itemValue: Double,
    @ColumnInfo(name = "special_instructions") val specialInstructions: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "created_at") val createdAt: Long
)

fun OrderEntity.toOrder(): Order = Order(
    id = id,
    pickupAddress = pickupAddress,
    dropAddress = dropAddress,
    itemName = itemName,
    category = category,
    itemValue = itemValue,
    specialInstructions = specialInstructions,
    status = status,
    createdAt = createdAt
)

fun Order.toEntity(): OrderEntity = OrderEntity(
    id = id,
    pickupAddress = pickupAddress,
    dropAddress = dropAddress,
    itemName = itemName,
    category = category,
    itemValue = itemValue,
    specialInstructions = specialInstructions,
    status = status,
    createdAt = createdAt
)
