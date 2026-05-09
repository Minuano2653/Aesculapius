package com.example.aesculapius.domain.pdf

import com.example.aesculapius.domain.pdf.model.PdfDownloadResult
import com.example.aesculapius.domain.pdf.model.PdfHealthResult

interface PdfRepository {
    suspend fun checkHealth(): PdfHealthResult
    suspend fun downloadReport(psvDays: Int, astDays: Int?): PdfDownloadResult
}
