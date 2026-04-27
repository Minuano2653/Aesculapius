package com.example.aesculapius.domain.airquality.usecases

import com.example.aesculapius.domain.airquality.AirQualityRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAddressByCoordinatesUseCase @Inject constructor(private val repository: AirQualityRepository) {
    suspend operator fun invoke(lat: Double, lon: Double): String =
        repository.reverseGeocode(lat, lon)
}
