package org.duocuc.capstonebackend.util

fun splitFullNameFromExcel(fullName: String): Pair<String, String> {
    val parts = fullName.trim().split(" ")
    return if (parts.size >= 3) {
        val lastName = parts.take(2).joinToString(" ")
        val firstName =  parts.drop(2).joinToString(" ")
        Pair(lastName, firstName)
    } else {
        // if there are just two parts, one is lastName and the other is firstName
        Pair(parts.firstOrNull()?: "", parts.getOrNull(1)?: "")
    }
}