package com.example.aesculapius.data.airquality

import com.example.aesculapius.data.airquality.device.DeviceLocationProvider
import com.example.aesculapius.data.airquality.remote.RemoteAirQualityDataSource
import com.example.aesculapius.database.UserPreferencesRepository
import com.example.aesculapius.domain.airquality.AirQualityRepository
import com.example.aesculapius.domain.airquality.model.AirQualityCache
import com.example.aesculapius.domain.airquality.model.AirQualityData
import com.example.aesculapius.domain.airquality.model.LocationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AirQualityRepositoryImpl @Inject constructor(
    private val remote: RemoteAirQualityDataSource,
    private val deviceLocationProvider: DeviceLocationProvider,
    private val preferences: UserPreferencesRepository
) : AirQualityRepository {

    override suspend fun fetchAirQuality(lat: Double, lon: Double): AirQualityData =
        remote.fetchAirQuality(lat, lon)

    override suspend fun reverseGeocode(lat: Double, lon: Double): String =
        remote.reverseGeocode(lat, lon)

    override suspend fun saveSelectedLocation(location: LocationData) {
        preferences.saveSelectedLocation(location.lat, location.lon, location.name)
    }

    override suspend fun saveAirQuality(data: AirQualityData) {
        preferences.saveAirQuality(data)
    }

    override suspend fun getCurrentDeviceLocation(): Pair<Double, Double>? =
        deviceLocationProvider.getCurrentLocation()

    override fun observeCache(): Flow<AirQualityCache?> = preferences.airQuality

    override suspend fun getSavedLocation(): LocationData? =
        preferences.airQuality.first()?.location
}
