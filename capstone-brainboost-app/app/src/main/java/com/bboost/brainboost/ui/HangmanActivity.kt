package com.bboost.brainboost.ui.hangman

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bboost.brainboost.dto.*
import com.bboost.brainboost.network.RetrofitClient
import com.bboost.brainboost.ui.screens.HangmanScreen
import com.bboost.brainboost.ui.theme.BrainBoostTheme
import com.bboost.brainboost.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class HangmanActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SUBJECT_ID = "EXTRA_SUBJECT_ID"
        const val EXTRA_SUBJECT_NAME = "EXTRA_SUBJECT_NAME"
        const val EXTRA_TOPIC_ID = "EXTRA_TOPIC_ID"
        const val EXTRA_TOPIC_NAME = "EXTRA_TOPIC_NAME"
    }

    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)

        val extras = intent.extras
        if (extras == null) {
            showToast("No se recibieron datos del tema.")
            finish()
            return
        }

        val subjectIdStr = extras.getString(EXTRA_SUBJECT_ID)
        val topicIdStr = extras.getString(EXTRA_TOPIC_ID)
        val subjectName = extras.getString(EXTRA_SUBJECT_NAME) ?: "Hangman"

        if (subjectIdStr == null || topicIdStr == null) {
            showToast("Faltan datos obligatorios.")
            finish()
            return
        }

        val token = session.getToken()
        val userIdStr = session.getUserId()

        if (token == null || userIdStr == null) {
            showToast("Sesión inválida.")
            finish()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.instance.startHangman(
                "Bearer $token",
                HangmanGameStartDto(
                    userId = UUID.fromString(userIdStr),
                    subjectId = UUID.fromString(subjectIdStr),
                    topicId = UUID.fromString(topicIdStr)
                )
            )

            withContext(Dispatchers.Main) {
                if (!response.isSuccessful || response.body() == null) {
                    showToast("Error al iniciar el juego.")
                    finish()
                } else {
                    val gameState = response.body()!!
                    setContent {
                        BrainBoostTheme {
                            HangmanScreen(
                                subjectName = subjectName,
                                token = token,
                                gameState = gameState,
                                onExit = { finish() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
