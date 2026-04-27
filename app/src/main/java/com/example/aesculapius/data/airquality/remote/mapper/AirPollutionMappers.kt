package com.example.aesculapius.data.airquality.remote.mapper

import com.example.aesculapius.data.airquality.remote.dto.AirPollutionDto
import com.example.aesculapius.data.airquality.remote.dto.ComponentsDto
import com.example.aesculapius.data.airquality.remote.dto.GeocoderResponseDto
import com.example.aesculapius.domain.airquality.model.AirQualityData
import com.example.aesculapius.domain.airquality.model.Components

fun AirPollutionDto.toDomain(fetchedAt: Long): AirQualityData {
    val entry = list.firstOrNull() ?: error("Air pollution response is empty")
    return AirQualityData(
        aqi = entry.main.aqi,
        components = entry.components.toDomain(),
        fetchedAt = fetchedAt
    )
}

fun ComponentsDto.toDomain(): Components = Components(
    co = co,
    no2 = no2,
    o3 = o3,
    so2 = so2,
    pm2_5 = pm2_5,
    pm10 = pm10
)

fun GeocoderResponseDto.toAddress(): String? {
    val first = response.collection.featureMember.firstOrNull()?.geoObject ?: return null
    val text = first.metaDataProperty?.geocoderMetaData?.text
    val name = first.name
    val description = first.description
    return when {
        !name.isNullOrBlank() && !description.isNullOrBlank() -> "$name, $description"
        !name.isNullOrBlank() -> name
        !text.isNullOrBlank() -> text
        else -> null
    }
}
