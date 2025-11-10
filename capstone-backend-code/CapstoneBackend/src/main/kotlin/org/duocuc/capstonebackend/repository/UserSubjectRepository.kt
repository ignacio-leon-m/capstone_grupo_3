package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.UserSubject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserSubjectRepository : JpaRepository<UserSubject, UUID> {

//    Find subject(s) by user id
    fun findSubjectByUserId(userId: UUID): List<UserSubject>

//    Verify if exists a relationship between user and subject.
    fun existsByUserIdAndSubjectId(userId: UUID, subjectId: UUID): Boolean

//    Find by a specific relationship between user y subject.
    fun findByUserIdAndSubjectId(userId: UUID, subjectId: UUID): UserSubject?

//    Find students of a specific subject (filtered by role).
    @Query(
        """
        SELECT us FROM UserSubject us 
        WHERE us.subject.id = :subjectId 
        AND LOWER(us.user.role.name) = 'alumno'
        """
    )
    fun findStudentsBySubject(subjectId: UUID): List<UserSubject>
}
