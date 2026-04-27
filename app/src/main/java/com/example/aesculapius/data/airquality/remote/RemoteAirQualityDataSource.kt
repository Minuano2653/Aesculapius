package com.example.aesculapius.data.airquality.remote

import com.example.aesculapius.data.airquality.remote.mapper.toAddress
import com.example.aesculapius.data.airquality.remote.mapper.toDomain
import com.example.aesculapius.di.GeocoderApiKey
import com.example.aesculapius.di.OpenWeatherApiKey
import com.example.aesculapius.domain.airquality.model.AirQualityData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteAirQualityDataSource @Inject constructor(
    private val openWeatherApi: OpenWeatherApi,
    private val geocoderApi: YandexGeocoderApi,
    @OpenWeatherApiKey private val openWeatherKey: String,
    @GeocoderApiKey private val geocoderKey: String
) {
    suspend fun fetchAirQuality(lat: Double, lon: Double): AirQualityData {
        val response = openWeatherApi.getAirPollution(lat = lat, lon = lon, appId = openWeatherKey)
        return response.toDomain(fetchedAt = System.currentTimeMillis())
    }

    suspend fun reverseGeocode(lat: Double, lon: Double): String {
        val response = geocoderApi.reverseGeocode(
            apiKey = geocoderKey,
            geocode = "$lon,$lat"
        )
        return response.toAddress().orEmpty()
    }
}
