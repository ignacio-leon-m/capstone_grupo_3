package com.bboost.brainboost.util

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("brainboost_prefs", Context.MODE_PRIVATE)

    fun saveAuth(token: String, role: String) {
        prefs.edit()
            .putString("TOKEN", token)
            .putString("ROLE", role)
            .apply()
    }

    fun getToken(): String? = prefs.getString("TOKEN", null)

    /** Nombre estándar usado en todas las Activities */
    fun getUserRole(): String? = prefs.getString("ROLE", null)

    /** Método antiguo (mantener por compatibilidad) */
    fun getRole(): String? = prefs.getString("ROLE", null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    // ===== USER ID =====
    fun saveUserId(id: String) {
        prefs.edit().putString("USER_ID", id).apply()
    }

    fun getUserId(): String? = prefs.getString("USER_ID", null)
}
