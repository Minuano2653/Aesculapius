package com.example.aesculapius.domain.airquality.usecases

import com.example.aesculapius.domain.airquality.AirQualityRepository
import com.example.aesculapius.domain.airquality.model.AirQualityData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAirQualityUseCase @Inject constructor(private val repository: AirQualityRepository) {
    suspend operator fun invoke(lat: Double, lon: Double): AirQualityData =
        repository.fetchAirQuality(lat, lon)
}
