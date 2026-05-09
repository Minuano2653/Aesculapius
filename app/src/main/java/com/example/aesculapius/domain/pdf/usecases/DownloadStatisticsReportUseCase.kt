package com.example.aesculapius.domain.pdf.usecases

import com.example.aesculapius.domain.pdf.PdfRepository
import com.example.aesculapius.domain.pdf.model.PdfDownloadResult
import javax.inject.Inject

class DownloadStatisticsReportUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    suspend operator fun invoke(
        psvDays: Int = DEFAULT_PSV_DAYS,
        astDays: Int? = null
    ): PdfDownloadResult {
        val clampedPsv = psvDays.coerceIn(MIN_DAYS, MAX_DAYS)
        val clampedAst = astDays?.coerceIn(MIN_DAYS, MAX_DAYS)
        return repository.downloadReport(clampedPsv, clampedAst)
    }

    private companion object {
        const val DEFAULT_PSV_DAYS = 31
        const val MIN_DAYS = 1
        const val MAX_DAYS = 3650
    }
}
