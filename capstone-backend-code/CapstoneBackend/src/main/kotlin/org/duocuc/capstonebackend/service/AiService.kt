package org.duocuc.capstonebackend.service

interface AiService {
    fun query(text: String, prompt: String): String
}

