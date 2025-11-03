package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.nosql.DocumentAnalysis
import org.springframework.stereotype.Repository

@Repository
interface DocumentAnalysisRepository {
    fun findByUserId(userId: String): List<DocumentAnalysis>
}