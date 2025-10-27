package com.bboost.brainboost.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val PREFS_NAME = "brainboost_prefs"
    private val KEY_TOKEN = "jwt_token"
    private val KEY_ROLE = "user_role"

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    fun saveAuth(token: String, role: String) {
        editor.putString(KEY_TOKEN, token)
        editor.putString(KEY_ROLE, role)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getRole(): String? {
        return prefs.getString(KEY_ROLE, null)
    }

    fun clearSession() {
        editor.remove(KEY_TOKEN)
        editor.remove(KEY_ROLE)
        editor.apply()
    }
}