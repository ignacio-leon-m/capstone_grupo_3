package org.duocuc.capstonebackend.util

fun String.nameToTitleCase(): String =
    this.lowercase()
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }


