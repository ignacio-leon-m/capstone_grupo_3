package org.duocuc.capstonebackend.dto

import java.util.UUID

data class TopicDto(
    var topicId: UUID,
    var name: String,
    var subjectId: UUID
)
