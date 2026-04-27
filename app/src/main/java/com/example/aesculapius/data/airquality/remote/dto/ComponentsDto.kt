package com.example.aesculapius.data.airquality.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ComponentsDto(
    @Json(name = "co") val co: Double,
    @Json(name = "no2") val no2: Double,
    @Json(name = "o3") val o3: Double,
    @Json(name = "so2") val so2: Double,
    @Json(name = "pm2_5") val pm2_5: Double,
    @Json(name = "pm10") val pm10: Double
)
