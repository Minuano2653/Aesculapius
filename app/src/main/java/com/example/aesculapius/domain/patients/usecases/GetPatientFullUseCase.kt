package com.example.aesculapius.domain.patients.usecases

import com.example.aesculapius.data.patients.remote.RemotePatientsDataSource
import com.example.aesculapius.domain.patients.model.PatientFull
import javax.inject.Inject

class GetPatientFullUseCase @Inject constructor(
    private val dataSource: RemotePatientsDataSource
) {
    suspend operator fun invoke(patientId: String): PatientFull? =
        dataSource.getPatientFull(patientId)
}
