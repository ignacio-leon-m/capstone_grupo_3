package com.example.front.data.remote.dto

data class TemaListItemDto(
    val id: String,
    val titulo: String,
    val resumenPreview: String?,
    val documentos: Int,
    val creadoEn: String // lo tratamos como string ISO
)
