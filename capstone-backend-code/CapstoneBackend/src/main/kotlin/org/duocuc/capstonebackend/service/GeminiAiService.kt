package org.duocuc.capstonebackend.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.genai.Client
import org.duocuc.capstonebackend.dto.AiQuizDto
import org.duocuc.capstonebackend.dto.AiQuizQuestionDto
import org.duocuc.capstonebackend.dto.AiSummaryDto
import org.duocuc.capstonebackend.util.PdfTextExtractor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service("geminiService")
class GeminiService(
    @param:Value("${gemini.api-key:}") private val apiKeyProp: String
) : AiService {

    private val apiKey: String = when {
        apiKeyProp.isNotBlank() -> apiKeyProp
        System.getenv("GOOGLE_API_KEY")?.isNotBlank() == true -> System.getenv("GOOGLE_API_KEY")
        else -> throw IllegalStateException("Falta la API key de Gemini: define gemini.api-key o GOOGLE_API_KEY")
    }

    private val client: Client = Client.builder().apiKey(apiKey).build()
    private val model = "gemini-2.0-flash"
    private val mapper = ObjectMapper()
    override fun summarizeDocument(
        bytes: ByteArray,
        mimeType: String,
        title: String
    ): AiSummaryDto {
        val text = PdfTextExtractor.safeExtract(bytes, mimeType)
        val prompt = """
            Resume el siguiente texto en no más de 500 palabras. Enfócate en las ideas clave y conceptos principales.
            El resumen debe ser claro, conciso y fácil de entender.

            Texto a resumir:
            ---
            $text
            ---
            Responde ÚNICAMENTE el resumen.
        """.trimIndent()

        val raw = client.models.generateContent(model, prompt, null).text().trim()
        return AiSummaryDto(summary = raw)
    }


    override fun generateQuizFromText(text: String, numQuestions: Int): AiQuizDto {
        val bounded = numQuestions.coerceIn(3, 10)

        val prompt = """
            Genera un QUIZ en formato JSON válido (sin texto extra) en español:
            - $bounded preguntas de opción múltiple
            - 4 opciones por pregunta
            - Solo una correcta (índice 0..3) en la propiedad "respuestaIndex"
            Esquema:
            {
              "preguntas": [
                { "enunciado": "string",
                  "opciones": ["string","string","string","string"],
                  "respuestaIndex": 0
                }
              ]
            }
            
            Texto fuente:
            ---
            $text
            ---
            Responde ÚNICAMENTE el JSON.
        """.trimIndent()

        val raw = client.models.generateContent(model, prompt, null).text().trim()

        val node = tryParseJson(raw)
            ?: tryParseJson(stripCodeFences(raw))
            ?: tryParseJson(extractBetweenBraces(raw) ?: "")
            ?: tryParseJson(extractBetweenBraces(stripCodeFences(raw)) ?: "")
            ?: throw IllegalStateException("La respuesta de Gemini no fue JSON válido. Muestra: ${raw.take(200)}")

        val preguntas = node["preguntas"] ?: throw IllegalStateException("JSON sin 'preguntas'")
        if (!preguntas.isArray || preguntas.size() == 0) throw IllegalStateException("JSON 'preguntas' vacío")

        val list = preguntas.map { q ->
            val enunciado = q["enunciado"]?.asText()?.trim().orEmpty()
            val opcionesNode = q["opciones"]
            val idx = q["respuestaIndex"]?.asInt() ?: 0
            val opciones = if (opcionesNode != null && opcionesNode.isArray)
                opcionesNode.map { it.asText() } else emptyList()

            require(enunciado.isNotBlank() && opciones.size == 4) { "Pregunta mal formada" }
            require(idx in 0..3) { "Índice de respuesta fuera de rango" }

            AiQuizQuestionDto(
                question = enunciado,
                options = opciones,
                answerIndex = idx
            )
        }

        return AiQuizDto(questions = list)
    }

    // --- helpers de parsing ---
    private fun tryParseJson(s: String?): JsonNode? = 
        if (s.isNullOrBlank()) null else try { mapper.readTree(s) } catch (_: Exception) { null }

    private fun stripCodeFences(s: String):
        val re = Regex("(?s)```(?:json)?\s*(.*?)\s*```")
        val m = re.find(s)
        return if (m != null) m.groupValues[1].trim() else s
    }

    private fun extractBetweenBraces(s: String): String? {
        val start = s.indexOf('{')
        val end = s.lastIndexOf('}')
        return if (start >= 0 && end > start) s.substring(start, end + 1) else null
    }
}