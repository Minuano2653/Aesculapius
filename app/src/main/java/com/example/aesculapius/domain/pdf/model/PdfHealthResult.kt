package com.example.aesculapius.domain.pdf.model

sealed interface PdfHealthResult {
    data object Ok : PdfHealthResult
    data object Down : PdfHealthResult
}
