package com.bboost.brainboost

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.bboost.brainboost.ui.screens.HomeScreen
import com.bboost.brainboost.ui.theme.BrainBoostTheme
import com.bboost.brainboost.util.SessionManager
import com.bboost.brainboost.ui.subjects.SubjectsListActivity

class HomeActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager
    private val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Iniciando HomeActivity")

        sessionManager = SessionManager(this)
        val role = sessionManager.getRole()?.lowercase() ?: "alumno"

        setContent {
            BrainBoostTheme {

                HomeScreen(
                    role = role,

                    // 👉 MODO QUIZ
                    onNavigateToContent = {
                        val i = Intent(this, SubjectsListActivity::class.java)
                        i.putExtra("MODE", "quiz")
                        startActivity(i)
                    },

                    onNavigateToUploadStudents = {
                        startActivity(Intent(this, UploadStudentsActivity::class.java))
                    },

                    onNavigateToUsers = {},

                    // 👉 MODO CONCEPTOS
                    onNavigateToConcepts = {
                        val i = Intent(this, SubjectsListActivity::class.java)
                        i.putExtra("MODE", "concepts")
                        startActivity(i)
                    },

                    onLogout = {
                        sessionManager.clearSession()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                )
            }
        }
    }
}
