package com.bboost.brainboost.ui.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.bboost.brainboost.ui.theme.BrainBoostTheme
import com.bboost.brainboost.util.SessionManager

class QuizActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SUBJECT_ID = "subjectId"
        const val EXTRA_SUBJECT_NAME = "subjectName"
        const val EXTRA_TOPIC_ID = "topicId"
        const val EXTRA_TOPIC_NAME = "topicName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val session = SessionManager(this)
        val token = session.getToken() ?: ""

        val topicId = intent.getStringExtra(EXTRA_TOPIC_ID) ?: ""
        val topicName = intent.getStringExtra(EXTRA_TOPIC_NAME) ?: ""

        setContent {
            BrainBoostTheme {
                QuizScreen(
                    topicId = topicId,
                    topicName = topicName,
                    token = token,
                    onFinish = { finish() } // volver a temas
                )
            }
        }
    }
}
