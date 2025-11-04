package com.bboost.brainboost

import android.app.Application
import com.bboost.brainboost.network.ApiClient

class BrainBoostApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializar ApiClient con el contexto de la aplicación
        ApiClient.initialize(this)
    }
}