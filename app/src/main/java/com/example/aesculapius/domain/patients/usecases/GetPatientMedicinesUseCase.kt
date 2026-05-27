package com.example.aesculapius.domain.patients.usecases

import com.example.aesculapius.data.patients.remote.RemotePatientStatsDataSource
import com.example.aesculapius.ui.therapy.MedicineItem
import javax.inject.Inject

class GetPatientMedicinesUseCase @Inject constructor(
    private val dataSource: RemotePatientStatsDataSource
) {
    suspend operator fun invoke(patientId: String): List<MedicineItem> =
        dataSource.getMedicines(patientId)
}
