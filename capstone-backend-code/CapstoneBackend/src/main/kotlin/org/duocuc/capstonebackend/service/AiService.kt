package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.AiQuizDto
import org.duocuc.capstonebackend.dto.AiSummaryDto

interface AiService {
    fun summarizeDocument(bytes: ByteArray, mimeType: String, title: String): AiSummaryDto
    fun generateQuizFromText(text: String, numQuestions: Int = 5): AiQuizDto
}
