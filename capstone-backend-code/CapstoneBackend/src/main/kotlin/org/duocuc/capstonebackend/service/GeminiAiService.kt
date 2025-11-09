package org.duocuc.capstonebackend.service

import com.google.genai.Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service("geminiService")
class GeminiAiService(
    @param:Value("\${gemini.api-key:}") private val apiKeyProp: String
) : AiService {

    private val apiKey: String = when {
        apiKeyProp.isNotBlank() -> apiKeyProp
        System.getenv("GOOGLE_API_KEY")?.isNotBlank() == true -> System.getenv("GOOGLE_API_KEY")
        else -> throw IllegalStateException("Missing Gemini API key: define gemini.api-key or GOOGLE_API_KEY")
    }

    private val client: Client = Client.builder().apiKey(apiKey).build()
    private val model = "gemini-2.0-flash"

    override fun query(text: String, prompt: String): String {
        val fullPrompt = """
            $prompt

            Reference text:
            ---
            $text
            ---
        """.trimIndent()

        val response = client.models.generateContent(model, fullPrompt, null).text()
        return response?.trim() ?: ""
    }
}