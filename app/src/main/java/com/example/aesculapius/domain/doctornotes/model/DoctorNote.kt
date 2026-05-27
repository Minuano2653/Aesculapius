package com.example.aesculapius.domain.doctornotes.model

data class DoctorNote(
    val id: String,
    val doctorId: String,
    val text: String,
    val createdAtMillis: Long
)
