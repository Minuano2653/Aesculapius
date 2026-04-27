package com.example.aesculapius.domain.airquality.model

data class AirQualityCache(
    val location: LocationData,
    val airQuality: AirQualityData?
)
