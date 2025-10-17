package org.duocuc.capstonebackend.service.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.genai.Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

// Usaremos esta interfaz para que otros componentes puedan llamar al servicio
interface AiService {
    fun resumen(texto: String): String
    fun quiz(texto: String): JsonNode
}

@Service
class GeminiService(
    @Value("\${gemini.api-key:}") private val apiKey: String
) : AiService {
    // Usa la clase Client del SDK de Java.
    private val client: Client =
        if (apiKey.isNotBlank()) Client.builder().apiKey(apiKey).build()
        else Client()

    private val mapper = ObjectMapper()
    // Usamos gemini-2.5-flash ya que es el modelo más reciente y rápido para este tipo de tareas
    private val model = "gemini-2.5-flash"

    override fun resumen(texto: String): String {
        val prompt = """
            Eres un ayudante para cursos universitarios.
            Devuelve un RESUMEN en español (5-7 viñetas) claro y conciso.
            Prohibido usar bloques de código o markdown de cercas (no uses ```).
            Texto:
            ---
            $texto
            ---
        """.trimIndent()

        // El SDK de Java permite la llamada simple con model, prompt y config (null para config simple)
        val resp = client.models.generateContent(model, prompt, null)
        return resp.text().trim()
    }

    /** Devuelve JSON válido con 5 preguntas de opción múltiple. Robusto ante ```json ... ```. */
    override fun quiz(texto: String): JsonNode {
        val prompt = """
            Genera un QUIZ en **JSON** (sin texto extra) para estudiantes, en español.
            - 5 preguntas de opción múltiple
            - 4 opciones por pregunta
            - Solo una correcta (índice 0..3)
            - Devuelve **exclusivamente** JSON, sin usar bloques ``` ni prosa.
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
            $texto
            ---
            Responde solo el JSON.
        """.trimIndent()

        val resp = client.models.generateContent(model, prompt, null)
        val raw = resp.text().trim()

        // Lógica de parsing robusta para asegurar que la salida es JSON válido:

        // 1) intentar parseo directo
        tryParseJson(raw)?.let { return it }

        // 2) quitar cercas ```…``` (```json o ``` a secas)
        val withoutFences = stripCodeFences(raw)
        tryParseJson(withoutFences)?.let { return it }

        // 3) extraer bloque entre { ... }
        val braces = extractBetweenBraces(raw)
        if (braces != null) {
            tryParseJson(braces)?.let { return it }
        }

        // 4) como último intento, extraer de withoutFences
        val braces2 = extractBetweenBraces(withoutFences)
        if (braces2 != null) {
            tryParseJson(braces2)?.let { return it }
        }

        // Si nada funcionó, lanza con muestra (corto) para depurar
        throw IllegalStateException("La respuesta de Gemini no fue JSON válido. Muestra: " + raw.take(200))
    }

    private fun tryParseJson(s: String?): JsonNode? {
        if (s.isNullOrBlank()) return null
        return try { mapper.readTree(s) } catch (_: Exception) { null }
    }

    private fun stripCodeFences(s: String): String {
        // Captura ```json ... ``` o ``` ... ```
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