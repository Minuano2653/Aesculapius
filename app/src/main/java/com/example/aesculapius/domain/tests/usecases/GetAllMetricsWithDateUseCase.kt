package com.example.aesculapius.domain.tests.usecases

import com.example.aesculapius.domain.tests.TestRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllMetricsWithDateUseCase @Inject constructor(private val repository: TestRepository) {
    suspend operator fun invoke(date: LocalDate) = repository.getAllMetricsWithDate(date)
}
