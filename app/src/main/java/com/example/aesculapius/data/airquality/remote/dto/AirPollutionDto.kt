package com.example.aesculapius.data.airquality.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AirPollutionDto(
    @Json(name = "list") val list: List<AirPollutionEntryDto>
)

@JsonClass(generateAdapter = true)
data class AirPollutionEntryDto(
    @Json(name = "main") val main: AirPollutionMainDto,
    @Json(name = "components") val components: ComponentsDto
)

@JsonClass(generateAdapter = true)
data class AirPollutionMainDto(
    @Json(name = "aqi") val aqi: Int
)
