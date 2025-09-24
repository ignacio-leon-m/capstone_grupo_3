// MultipartExt.kt
package com.example.front.util

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun String.toTextPart(): RequestBody =
    this.toRequestBody("text/plain".toMediaTypeOrNull())

suspend fun Context.uriToMultipartIO(name: String, uri: Uri): MultipartBody.Part = withContext(Dispatchers.IO) {
    val cr: ContentResolver = contentResolver
    val mime = cr.getType(uri) ?: "application/octet-stream"
    val fileName = getFileName(cr, uri) ?: "documento"

    val bytes = cr.openInputStream(uri)!!.use { it.readBytes() }
    val body: RequestBody = bytes.toRequestBody(mime.toMediaTypeOrNull())
    MultipartBody.Part.createFormData(name, fileName, body)
}

private fun getFileName(cr: ContentResolver, uri: Uri): String? {
    var name: String? = null
    cr.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
        val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (c.moveToFirst() && idx >= 0) name = c.getString(idx)
    }
    return name
}

