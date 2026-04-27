package com.example.aesculapius.data.airquality.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocoderResponseDto(
    @Json(name = "response") val response: GeocoderResponseBodyDto
)

@JsonClass(generateAdapter = true)
data class GeocoderResponseBodyDto(
    @Json(name = "GeoObjectCollection") val collection: GeoObjectCollectionDto
)

@JsonClass(generateAdapter = true)
data class GeoObjectCollectionDto(
    @Json(name = "featureMember") val featureMember: List<FeatureMemberDto>
)

@JsonClass(generateAdapter = true)
data class FeatureMemberDto(
    @Json(name = "GeoObject") val geoObject: GeoObjectDto
)

@JsonClass(generateAdapter = true)
data class GeoObjectDto(
    @Json(name = "name") val name: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "metaDataProperty") val metaDataProperty: GeoObjectMetaPropertyDto?
)

@JsonClass(generateAdapter = true)
data class GeoObjectMetaPropertyDto(
    @Json(name = "GeocoderMetaData") val geocoderMetaData: GeocoderMetaDataDto?
)

@JsonClass(generateAdapter = true)
data class GeocoderMetaDataDto(
    @Json(name = "text") val text: String?
)
