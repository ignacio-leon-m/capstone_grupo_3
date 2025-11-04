package com.bboost.brainboost.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SessionManager(context: Context) {
    private val PREFS_NAME = "brainboost_prefs"
    private val KEY_TOKEN = "jwt_token"
    private val KEY_ROLE = "user_role"
    private val KEY_USER_NAME = "user_name"

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    // ✅ MÉTODO ACTUALIZADO - acepta userName
    fun saveAuth(token: String, role: String, userName: String? = null) {
        editor.putString(KEY_TOKEN, token)
        editor.putString(KEY_ROLE, role)
        editor.putString(KEY_USER_NAME, userName ?: "Usuario")
        editor.apply()

        Log.d("SessionManager", "Sesión guardada - Usuario: $userName, Rol: $role")
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getRole(): String? {
        return prefs.getString(KEY_ROLE, null)
    }

    // ✅ NUEVO MÉTODO
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    fun clearSession() {
        editor.remove(KEY_TOKEN)
        editor.remove(KEY_ROLE)
        editor.remove(KEY_USER_NAME)
        editor.apply()
        Log.d("SessionManager", "Sesión limpiada")
    }
}