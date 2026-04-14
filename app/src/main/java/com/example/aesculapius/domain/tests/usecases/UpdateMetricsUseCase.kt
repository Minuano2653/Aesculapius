package com.example.aesculapius.domain.tests.usecases

import com.example.aesculapius.domain.tests.TestRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateMetricsUseCase @Inject constructor(private val repository: TestRepository) {
    suspend operator fun invoke(userId: String, metrics: Float, date: LocalDate) =
        repository.updateMetrics(userId, metrics, date)
}
