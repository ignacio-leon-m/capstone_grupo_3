package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.UserSubject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserSubjectRepository : JpaRepository<UserSubject, UUID> {

    fun findByUserId(userId: UUID): List<UserSubject>
    fun findBySubjectId(subjectId: UUID): List<UserSubject>
    fun findByUserIdAndActiveTrue(userId: UUID): List<UserSubject>
    fun existsByUserIdAndSubjectId(userId: UUID, subjectId: UUID): Boolean
    fun findByUserIdAndSubjectId(userId: UUID, subjectId: UUID): UserSubject?
    fun countByUserIdAndActiveTrue(userId: UUID): Long

    //find profesores/students by subject with role filter
    @Query(
        """
        SELECT ua FROM UserSubject ua 
        WHERE ua.subject.id = :subjectId 
        AND LOWER(ua.user.role.name) = 'profesor'
        """
    )
    fun findProfessorsBySubject(subjectId: UUID): List<UserSubject>
    @Query(
        """
        SELECT ua FROM UserSubject ua 
        WHERE ua.subject.id = :subjectId 
        AND LOWER(ua.user.role.name) = 'student'
        """
    )
    fun findStudentsBySubject(subjectId: UUID): List<UserSubject>
}
