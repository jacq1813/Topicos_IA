package com.example.detec

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(id: Int, name: String, email: String) {
        val editor = prefs.edit()
        editor.putInt("USER_ID", id)
        editor.putString("USER_NAME", name)
        editor.putString("USER_EMAIL", email)
        editor.putBoolean("IS_LOGGED_IN", true)
        editor.apply()
    }

    fun getUserId(): Int = prefs.getInt("USER_ID", -1)
    fun getUserName(): String = prefs.getString("USER_NAME", "Usuario") ?: "Usuario"
    fun getUserEmail(): String = prefs.getString("USER_EMAIL", "") ?: ""
    fun isLoggedIn(): Boolean = prefs.getBoolean("IS_LOGGED_IN", false)

    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}