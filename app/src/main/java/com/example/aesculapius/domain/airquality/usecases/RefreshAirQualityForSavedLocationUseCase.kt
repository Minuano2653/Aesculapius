package com.example.aesculapius.domain.airquality.usecases

import com.example.aesculapius.domain.airquality.AirQualityRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshAirQualityForSavedLocationUseCase @Inject constructor(
    private val repository: AirQualityRepository
) {
    suspend operator fun invoke(): Boolean {
        val location = repository.getSavedLocation() ?: return false
        val data = repository.fetchAirQuality(location.lat, location.lon)
        repository.saveAirQuality(data)
        return true
    }
}
