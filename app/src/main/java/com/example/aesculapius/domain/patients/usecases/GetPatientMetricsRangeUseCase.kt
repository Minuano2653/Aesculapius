package com.example.aesculapius.domain.patients.usecases

import com.example.aesculapius.data.patients.remote.RemotePatientStatsDataSource
import com.example.aesculapius.ui.tests.MetricsItem
import java.time.LocalDate
import javax.inject.Inject

class GetPatientMetricsRangeUseCase @Inject constructor(
    private val dataSource: RemotePatientStatsDataSource
) {
    suspend operator fun invoke(
        patientId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<MetricsItem> = dataSource.getMetricsInRange(patientId, startDate, endDate)
}
