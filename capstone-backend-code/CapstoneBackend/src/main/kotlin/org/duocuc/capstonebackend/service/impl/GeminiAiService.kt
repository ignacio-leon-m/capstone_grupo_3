package org.duocuc.capstonebackend.service.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.genai.Client
import org.duocuc.capstonebackend.dto.AiQuizDto
import org.duocuc.capstonebackend.dto.AiQuizQuestionDto   // ⬅️ FALTABA ESTE
import org.duocuc.capstonebackend.dto.AiSummaryDto
import org.duocuc.capstonebackend.service.AiService
import org.duocuc.capstonebackend.util.PdfTextExtractor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GeminiService(
    @param:Value("\${gemini.api-key:}") private val apiKeyProp: String
) : AiService {

    private val apiKey: String = when {
        apiKeyProp.isNotBlank() -> apiKeyProp
        System.getenv("GOOGLE_API_KEY")?.isNotBlank() == true -> System.getenv("GOOGLE_API_KEY")
        else -> throw IllegalStateException("Falta la API key de Gemini: define gemini.api-key o GOOGLE_API_KEY")
    }

    private val client: Client = Client.builder().apiKey(apiKey).build()
    private val model = "gemini-2.0-flash"
    private val mapper = ObjectMapper()

    override fun summarizeDocument(bytes: ByteArray, mimeType: String, title: String): AiSummaryDto {
        val text = PdfTextExtractor.safeExtract(bytes, mimeType).ifBlank {
            return AiSummaryDto(title, "No se pudo extraer texto del documento.")
        }

        val prompt = """
            Eres un ayudante académico. Resume en español el siguiente contenido en 3 líneas claras, sin viñetas, sin markdown.
            Texto:
            ---
            $text
            ---
        """.trimIndent()

        val resp = client.models.generateContent(model, prompt, null)
        val summary = resp.text().trim()
        return AiSummaryDto(title = title, summary = summary)
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

    private fun stripCodeFences(s: String): String {
        val re = Regex("(?s)```(?:json)?\\s*(.*?)\\s*```")
        val m = re.find(s)
        return if (m != null) m.groupValues[1].trim() else s
    }

    private fun extractBetweenBraces(s: String): String? {
        val start = s.indexOf('{')
        val end = s.lastIndexOf('}')
        return if (start >= 0 && end > start) s.substring(start, end + 1) else null
    }
}
