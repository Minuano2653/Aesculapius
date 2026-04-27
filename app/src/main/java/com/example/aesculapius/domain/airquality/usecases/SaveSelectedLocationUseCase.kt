package com.example.aesculapius.domain.airquality.usecases

import com.example.aesculapius.domain.airquality.AirQualityRepository
import com.example.aesculapius.domain.airquality.model.LocationData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveSelectedLocationUseCase @Inject constructor(private val repository: AirQualityRepository) {
    suspend operator fun invoke(location: LocationData) =
        repository.saveSelectedLocation(location)
}
