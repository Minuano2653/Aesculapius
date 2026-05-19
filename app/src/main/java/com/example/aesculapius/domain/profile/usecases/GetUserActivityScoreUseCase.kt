package com.example.aesculapius.domain.profile.usecases

import com.example.aesculapius.domain.medicine.usecases.GetMedicinesScoreUseCase
import com.example.aesculapius.domain.profile.UserActivityResult
import com.example.aesculapius.domain.tests.usecases.GetAllAstResultsUseCase
import com.example.aesculapius.domain.tests.usecases.GetLinePointsAmountUseCase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.datetime.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserActivityScoreUseCase @Inject constructor(
    private val getMedicinesScoreUseCase: GetMedicinesScoreUseCase,
    private val getAllAstResultsUseCase: GetAllAstResultsUseCase,
    private val getLinePointsAmountUseCase: GetLinePointsAmountUseCase
) {
    suspend operator fun invoke(userRegisterDate: LocalDate): UserActivityResult {
        if (ChronoUnit.DAYS.between(userRegisterDate, LocalDate.now()) < 30)
            return UserActivityResult()

        val metricsScore = getLinePointsAmountUseCase(
            LocalDate.now().minusMonths(1), LocalDate.now()
        ) * 2.0 / 60.0

        val astResults = getAllAstResultsUseCase()
        val astTestScore = when {
            astResults.isEmpty() -> 0.0
            astResults.last().date >= LocalDate.now().minusMonths(1) -> astResults.last().score / 25.0
            else -> 0.0
        }

        val medicinesScore = getMedicinesScoreUseCase()

        return UserActivityResult(
            mainScore = (astTestScore + metricsScore + medicinesScore + 1) * 2.5,
            astTestScore = astTestScore,
            metricsScore = metricsScore,
            medicinesScore = medicinesScore
        )
    }
}
