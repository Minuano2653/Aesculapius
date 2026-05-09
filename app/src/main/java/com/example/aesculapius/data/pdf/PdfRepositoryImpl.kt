package com.example.aesculapius.data.pdf

import com.example.aesculapius.data.pdf.remote.RemotePdfDataSource
import com.example.aesculapius.domain.pdf.PdfRepository
import com.example.aesculapius.domain.pdf.model.PdfDownloadResult
import com.example.aesculapius.domain.pdf.model.PdfHealthResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfRepositoryImpl @Inject constructor(
    private val remote: RemotePdfDataSource
) : PdfRepository {

    override suspend fun checkHealth(): PdfHealthResult = remote.checkHealth()

    override suspend fun downloadReport(psvDays: Int, astDays: Int?): PdfDownloadResult =
        remote.downloadReport(psvDays, astDays)
}
