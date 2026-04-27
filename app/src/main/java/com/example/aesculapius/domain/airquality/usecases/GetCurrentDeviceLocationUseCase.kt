package com.example.aesculapius.domain.airquality.usecases

import com.example.aesculapius.domain.airquality.AirQualityRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentDeviceLocationUseCase @Inject constructor(
    private val repository: AirQualityRepository
) {
    suspend operator fun invoke(): Pair<Double, Double>? = repository.getCurrentDeviceLocation()
}
