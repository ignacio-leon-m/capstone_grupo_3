package org.duocuc.capstonebackend.util

import java.security.MessageDigest

object HashingUtils {
    fun sha1(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-1").digest(bytes).toHex()

    fun sha256Hex(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes).toHex()

    fun sha256Hex(text: String): String =
        sha256Hex(text.toByteArray(Charsets.UTF_8))

    private fun ByteArray.toHex(): String =
        joinToString("") { "%02x".format(it) }
}