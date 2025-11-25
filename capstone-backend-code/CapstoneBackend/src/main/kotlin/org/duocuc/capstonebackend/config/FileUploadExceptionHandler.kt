package org.duocuc.capstonebackend.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException

/**
 * Global exception handler for file upload errors
 */
@RestControllerAdvice
class FileUploadExceptionHandler {

    data class ErrorResponse(
        val error: String,
        val message: String,
        val status: Int
    )

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxSizeException(exc: MaxUploadSizeExceededException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            error = "FILE_TOO_LARGE",
            message = "El archivo excede el tamaño máximo permitido (50 MB). Por favor, selecciona un archivo más pequeño.",
            status = HttpStatus.PAYLOAD_TOO_LARGE.value()
        )
        return ResponseEntity
            .status(HttpStatus.PAYLOAD_TOO_LARGE)
            .body(errorResponse)
    }
}
