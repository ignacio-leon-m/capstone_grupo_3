
package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.SaveQuizRequestDto
import org.duocuc.capstonebackend.dto.SaveQuizResponseDto

interface QuizPersistService {
    fun saveQuiz(req: SaveQuizRequestDto): SaveQuizResponseDto
}
