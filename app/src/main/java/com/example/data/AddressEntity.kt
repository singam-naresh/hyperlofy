package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val fullAddress: String,
    val landmark: String?,
    val city: String,
    val state: String,
    val pincode: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
