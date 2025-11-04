package com.bboost.brainboost.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import com.bboost.brainboost.util.SessionManager

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val sessionManager = SessionManager(context)
        val token = sessionManager.getToken()

        // Si no hay token, procedemos sin header de autorización
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        // Agregar el token al header
        val requestWithToken = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(requestWithToken)
    }
}