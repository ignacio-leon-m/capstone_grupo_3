package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.Document
import org.duocuc.capstonebackend.model.Subject
import org.duocuc.capstonebackend.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for Document entity.
 * Manages uploaded content files (PDFs, DOCX, TXT).
 */
@Repository
interface DocumentRepository : JpaRepository<Document, UUID> {
    fun findBySubject(subject: Subject): List<Document>
    fun findByUploadedBy(user: User): List<Document>
    fun findBySubjectAndProcessed(subject: Subject, processed: Boolean): List<Document>
    fun findByProcessed(processed: Boolean): List<Document>
}
