package org.duocuc.capstonebackend.nosql

import org.springframework.data.mongodb.repository.MongoRepository

interface StoredDocumentRepository : MongoRepository<StoredDocument, String> {
    fun findBySha1(sha1: String): StoredDocument?
}
