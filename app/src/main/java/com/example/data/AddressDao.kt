package com.example.data

import androidx.room.*

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses WHERE userId = :userId ORDER BY isDefault DESC, createdAt DESC")
    suspend fun getByUser(userId: String): List<AddressEntity>

    @Query("SELECT * FROM addresses WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): AddressEntity?

    @Insert
    suspend fun insert(address: AddressEntity)

    @Update
    suspend fun update(address: AddressEntity)

    @Delete
    suspend fun delete(address: AddressEntity)

    @Query("UPDATE addresses SET isDefault = 0 WHERE userId = :userId")
    suspend fun clearDefaultForUser(userId: String)

    @Query("SELECT * FROM addresses WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    suspend fun getDefaultForUser(userId: String): AddressEntity?
}
