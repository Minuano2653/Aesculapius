package com.example.aesculapius.data.airquality.remote

import com.example.aesculapius.data.airquality.remote.dto.AirPollutionDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {
    @GET("data/2.5/air_pollution")
    suspend fun getAirPollution(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appId: String
    ): AirPollutionDto
}
