package com.bboost.brainboost.dto

import java.util.UUID

data class SubjectInfoDto(
    val id: UUID,
    val name: String,
    val active: Boolean,
    val enrollmentDate: String?
)
