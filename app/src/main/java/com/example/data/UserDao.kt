package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<UserEntity>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :credential OR phoneNumber = :credential")
    suspend fun findByEmailOrPhone(credential: String): UserEntity?

    @Query("SELECT * FROM users WHERE (email = :credential OR phoneNumber = :credential) AND password = :password")
    suspend fun login(credential: String, password: String): UserEntity?
}
