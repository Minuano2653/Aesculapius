package com.example.aesculapius.data.airquality.remote

import com.example.aesculapius.data.airquality.remote.dto.GeocoderResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexGeocoderApi {
    @GET("1.x/")
    suspend fun reverseGeocode(
        @Query("apikey") apiKey: String,
        @Query("geocode") geocode: String,
        @Query("format") format: String = "json",
        @Query("results") results: Int = 1,
        @Query("lang") lang: String = "ru_RU"
    ): GeocoderResponseDto
}
