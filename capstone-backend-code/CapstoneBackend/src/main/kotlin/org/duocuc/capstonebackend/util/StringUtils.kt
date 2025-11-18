package org.duocuc.capstonebackend.util

fun String.nameToTitleCase(): String =
    lowercase().split(" ").joinToString(" ") {
        it.replaceFirstChar { char -> char.uppercase() }
    }

fun splitFullNameFromExcel(fullName: String): Pair<String, String> {
    val parts = fullName.trim().split(" ")
    return if (parts.size >= 3) {
        val lastName = parts.take(2).joinToString(" ")
        val firstName = parts.drop(2).joinToString(" ")
        Pair(lastName, firstName)
    } else {
        Pair(parts.firstOrNull() ?: "", parts.getOrNull(1) ?: "")
    }
}