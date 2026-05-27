package com.example.aesculapius.domain.patients.model

data class PatientSummary(
    val id: String,
    val name: String,
    val surname: String,
    val patronymic: String,
    val activity: ActivitySnapshot?
) {
    val fullName: String
        get() = listOf(surname, name, patronymic).filter { it.isNotBlank() }.joinToString(" ")
}
