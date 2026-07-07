package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["phoneNumber"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val phoneNumber: String,
    val email: String,
    val password: String,
    val isVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
