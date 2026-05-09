package com.example.aesculapius.data.pdf.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HealthDto(
    val status: String?
)
