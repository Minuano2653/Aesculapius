package com.example.aesculapius.domain.tests.usecases

import com.example.aesculapius.domain.tests.TestRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLinePointsAmountUseCase @Inject constructor(private val repository: TestRepository) {
    suspend operator fun invoke(startDate: LocalDate, endDate: LocalDate) =
        repository.getLinePointsAmountOnDates(startDate, endDate)
}
