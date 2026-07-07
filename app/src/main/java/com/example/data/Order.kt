package com.example.data

data class Order(
    val id: String,
    val pickupAddress: String,
    val dropAddress: String,
    val itemName: String,
    val category: String,
    val itemValue: Double,
    val specialInstructions: String,
    val status: String,
    val createdAt: Long
)
