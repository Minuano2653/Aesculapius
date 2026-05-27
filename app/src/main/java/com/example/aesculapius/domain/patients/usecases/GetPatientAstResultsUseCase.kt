package com.example.aesculapius.domain.patients.usecases

import com.example.aesculapius.data.patients.remote.RemotePatientStatsDataSource
import com.example.aesculapius.ui.tests.ScoreItem
import javax.inject.Inject

class GetPatientAstResultsUseCase @Inject constructor(
    private val dataSource: RemotePatientStatsDataSource
) {
    suspend operator fun invoke(patientId: String): List<ScoreItem> =
        dataSource.getAllAstResults(patientId)
}
