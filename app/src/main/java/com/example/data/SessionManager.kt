package com.example.data

import android.content.Context

class SessionManager private constructor(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun saveSession(userId: String, email: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun clearSession() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_EMAIL)
            .apply()
    }

    fun login(userId: String, email: String) {
        saveSession(userId, email)
    }

    fun logout() {
        clearSession()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun currentUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun currentUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    companion object {
        private const val PREFERENCES_NAME = "hyperlofy_session_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SessionManager(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
