package com.example.data

import android.content.Context

class AddressRepository private constructor(private val dao: AddressDao) {

    companion object {
        @Volatile
        private var INSTANCE: AddressRepository? = null

        fun getInstance(context: Context): AddressRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val instance = AddressRepository(db.addressDao())
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun getAddressesForUser(userId: String): List<AddressEntity> {
        return dao.getByUser(userId)
    }

    suspend fun addAddress(address: AddressEntity) {
        if (address.isDefault) {
            dao.clearDefaultForUser(address.userId)
        }
        dao.insert(address)
    }

    suspend fun updateAddress(address: AddressEntity) {
        if (address.isDefault) {
            dao.clearDefaultForUser(address.userId)
        }
        dao.update(address)
    }

    suspend fun deleteAddress(address: AddressEntity) {
        dao.delete(address)
    }

    suspend fun setDefault(addressId: String) {
        val addr = dao.getById(addressId) ?: return
        dao.clearDefaultForUser(addr.userId)
        dao.update(addr.copy(isDefault = true))
    }

    suspend fun getDefaultForUser(userId: String): AddressEntity? {
        return dao.getDefaultForUser(userId)
    }
}
