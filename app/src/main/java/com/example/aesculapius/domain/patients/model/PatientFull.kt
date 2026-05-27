package com.example.aesculapius.domain.patients.model

import java.time.LocalDate

data class PatientFull(
    val id: String,
    val name: String,
    val surname: String,
    val patronymic: String,
    val birthday: LocalDate?,
    val height: Float,
    val weight: Float,
    val activity: ActivitySnapshot?
) {
    val fullName: String
        get() = listOf(surname, name, patronymic).filter { it.isNotBlank() }.joinToString(" ")
}
