package com.example.aesculapius.domain.airquality.model

data class AirQualityData(
    val aqi: Int,
    val components: Components,
    val fetchedAt: Long
)
