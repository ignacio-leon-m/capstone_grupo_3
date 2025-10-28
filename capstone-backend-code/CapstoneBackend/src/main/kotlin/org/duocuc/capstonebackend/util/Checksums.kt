package org.duocuc.capstonebackend.util

import java.security.MessageDigest

object Checksums {
    fun sha1(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-1")
        val d = md.digest(bytes)
        return d.joinToString("") { "%02x".format(it) }
    }
}
