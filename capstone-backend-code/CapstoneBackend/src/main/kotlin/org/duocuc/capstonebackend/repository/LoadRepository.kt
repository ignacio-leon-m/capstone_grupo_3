package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.Load
import org.duocuc.capstonebackend.model.LoadStatus
import org.duocuc.capstonebackend.model.LoadType
import org.duocuc.capstonebackend.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for Load audit records.
 */
@Repository
interface LoadRepository : JpaRepository<Load, UUID> {
    fun findByUploadedBy(user: User): List<Load>
    fun findByStatus(status: LoadStatus): List<Load>
    fun findByLoadType(loadType: LoadType): List<Load>
}

/**
 * Repository for LoadStatus catalog.
 */
@Repository
interface LoadStatusRepository : JpaRepository<LoadStatus, UUID> {
    fun findByStatusName(statusName: String): LoadStatus?
}

/**
 * Repository for LoadType catalog.
 */
@Repository
interface LoadTypeRepository : JpaRepository<LoadType, UUID> {
    fun findByTypeName(typeName: String): LoadType?
}
