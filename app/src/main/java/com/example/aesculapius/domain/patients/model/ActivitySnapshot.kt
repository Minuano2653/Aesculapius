package com.example.aesculapius.domain.patients.model

data class ActivitySnapshot(
    val mainScore: Double,
    val astTestScore: Double,
    val metricsScore: Double,
    val medicinesScore: Double,
    val updatedAtMillis: Long? = null
)
