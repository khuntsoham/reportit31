package com.example.reportit31

import android.content.Context
import android.content.SharedPreferences

object MockAuth {
    private const val PREF_NAME = "MockAuthPrefs"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    private const val KEY_USER_EMAIL = "userEmail"
    private const val KEY_USER_PASSWORD = "userPassword"
    private const val KEY_USER_NAME = "userName"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun saveUser(context: Context, name: String, email: String, password: String) {
        getPrefs(context).edit().apply {
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_PASSWORD, password)
            apply()
        }
    }

    fun verifyUser(context: Context, email: String, password: String): Boolean {
        val savedEmail = getPrefs(context).getString(KEY_USER_EMAIL, "")
        val savedPassword = getPrefs(context).getString(KEY_USER_PASSWORD, "")
        
        // If no user is saved yet, allow any login to succeed for "proper" home access as requested
        if (savedEmail.isNullOrEmpty()) return true
        
        return savedEmail == email && savedPassword == password
    }

    fun getUserName(context: Context): String {
        return getPrefs(context).getString(KEY_USER_NAME, "User") ?: "User"
    }

    fun logout(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
