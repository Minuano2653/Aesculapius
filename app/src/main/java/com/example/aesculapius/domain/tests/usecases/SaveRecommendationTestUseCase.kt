package com.example.aesculapius.domain.tests.usecases

import com.example.aesculapius.domain.tests.TestRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveRecommendationTestUseCase @Inject constructor(private val repository: TestRepository) {
    suspend operator fun invoke(userId: String, date: LocalDate, score: Int) =
        repository.saveRecommendationTest(userId, date, score)
}
