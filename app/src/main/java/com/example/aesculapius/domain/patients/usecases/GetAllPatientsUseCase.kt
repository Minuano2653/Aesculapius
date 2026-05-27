package com.example.aesculapius.domain.patients.usecases

import com.example.aesculapius.data.patients.remote.RemotePatientsDataSource
import com.example.aesculapius.domain.patients.model.PatientSummary
import javax.inject.Inject

class GetAllPatientsUseCase @Inject constructor(
    private val dataSource: RemotePatientsDataSource
) {
    suspend operator fun invoke(): List<PatientSummary> =
        dataSource.getAllPatients().sortedWith(
            compareBy(nullsLast()) { it.activity?.mainScore }
        )
}
