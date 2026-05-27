package com.example.aesculapius.domain.patients.usecases

import com.example.aesculapius.data.patients.remote.RemotePatientStatsDataSource
import java.time.LocalDate
import javax.inject.Inject

class GetPatientLinePointsAmountUseCase @Inject constructor(
    private val dataSource: RemotePatientStatsDataSource
) {
    suspend operator fun invoke(
        patientId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Int = dataSource.getLinePointsAmount(patientId, startDate, endDate)
}
