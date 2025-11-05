package org.duocuc.capstonebackend.nosql

import org.springframework.data.mongodb.repository.MongoRepository

interface AiQueryLogRepository : MongoRepository<AiQueryLog, String>
