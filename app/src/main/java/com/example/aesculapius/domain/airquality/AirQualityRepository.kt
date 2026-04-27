package com.example.aesculapius.domain.airquality

import com.example.aesculapius.domain.airquality.model.AirQualityCache
import com.example.aesculapius.domain.airquality.model.AirQualityData
import com.example.aesculapius.domain.airquality.model.LocationData
import kotlinx.coroutines.flow.Flow

interface AirQualityRepository {
    suspend fun fetchAirQuality(lat: Double, lon: Double): AirQualityData
    suspend fun reverseGeocode(lat: Double, lon: Double): String
    suspend fun saveSelectedLocation(location: LocationData)
    suspend fun saveAirQuality(data: AirQualityData)
    suspend fun getCurrentDeviceLocation(): Pair<Double, Double>?
    fun observeCache(): Flow<AirQualityCache?>
    suspend fun getSavedLocation(): LocationData?
}
