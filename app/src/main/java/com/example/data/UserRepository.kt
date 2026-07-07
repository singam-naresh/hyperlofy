package com.example.data

import android.content.Context
import java.util.Locale

sealed interface RegistrationResult {
    data class Success(val userId: String) : RegistrationResult
    data class Error(val message: String) : RegistrationResult
}

class UserRepository private constructor(private val dao: UserDao) {

    suspend fun insert(user: UserEntity) {
        dao.insert(user)
    }

    suspend fun update(user: UserEntity) {
        dao.update(user)
    }

    suspend fun getById(id: String): UserEntity? {
        return dao.getById(id)
    }

    suspend fun getByEmail(email: String): UserEntity? {
        return dao.getByEmail(email.trim())
    }

    suspend fun getByPhone(phone: String): UserEntity? {
        return dao.getByPhone(phone.trim())
    }

    suspend fun findByEmailOrPhone(credential: String): UserEntity? {
        return dao.findByEmailOrPhone(credential.trim())
    }

    suspend fun login(credential: String, password: String): UserEntity? {
        return dao.login(credential.trim().lowercase(Locale.getDefault()), password)
    }

    suspend fun register(user: UserEntity): RegistrationResult {
        val existingByEmail = dao.getByEmail(user.email.trim())
        if (existingByEmail != null) {
            return RegistrationResult.Error("Email already registered")
        }

        val existingByPhone = dao.getByPhone(user.phoneNumber.trim())
        if (existingByPhone != null) {
            return RegistrationResult.Error("Phone number already registered")
        }

        return try {
            dao.insert(user)
            RegistrationResult.Success(user.id)
        } catch (e: Exception) {
            RegistrationResult.Error("Registration failed")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(context: Context): UserRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val repo = UserRepository(db.userDao())
                INSTANCE = repo
                repo
            }
        }
    }
}
