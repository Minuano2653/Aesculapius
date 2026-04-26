package com.example.aesculapius.domain.tests.usecases

import com.example.aesculapius.domain.tests.TestRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAllMetricsUseCase @Inject constructor(private val repository: TestRepository) {
    suspend operator fun invoke() = repository.deleteAllMetrics()
}
