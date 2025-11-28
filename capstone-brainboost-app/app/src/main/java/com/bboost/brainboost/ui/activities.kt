package com.bboost.brainboost.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.bboost.brainboost.dto.HangmanGameStateDto
import com.bboost.brainboost.ui.screens.HangmanScreen
import com.google.gson.Gson

class HangmanScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = intent.getStringExtra("TOKEN") ?: return finish()
        val subjectName = intent.getStringExtra("SUBJECT_NAME") ?: "Sin nombre"
        val gameStateJson = intent.getStringExtra("GAME_STATE") ?: return finish()
        val gameState = Gson().fromJson(gameStateJson, HangmanGameStateDto::class.java)

        setContent {
            HangmanScreen(
                subjectName = subjectName,
                token = token,
                gameState = gameState,
                onExit = { finish() }
            )
        }
    }
}
