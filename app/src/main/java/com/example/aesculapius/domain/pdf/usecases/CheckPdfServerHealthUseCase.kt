package com.example.aesculapius.domain.pdf.usecases

import com.example.aesculapius.domain.pdf.PdfRepository
import com.example.aesculapius.domain.pdf.model.PdfHealthResult
import javax.inject.Inject

class CheckPdfServerHealthUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    suspend operator fun invoke(): PdfHealthResult = repository.checkHealth()
}
