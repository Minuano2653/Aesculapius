package com.example.aesculapius.domain.profile

data class UserActivityResult(
    val mainScore: Double = -1.0,
    val astTestScore: Double = 0.0,
    val metricsScore: Double = 0.0,
    val medicinesScore: Double = 0.0
)
