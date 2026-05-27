package com.example.aesculapius.domain.patients.usecases

import com.example.aesculapius.domain.patients.model.PatientSummary
import javax.inject.Inject

class SearchPatientsUseCase @Inject constructor() {
    operator fun invoke(patients: List<PatientSummary>, query: String): List<PatientSummary> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return patients
        return patients.filter { patient ->
            patient.fullName.contains(trimmed, ignoreCase = true)
        }
    }
}
