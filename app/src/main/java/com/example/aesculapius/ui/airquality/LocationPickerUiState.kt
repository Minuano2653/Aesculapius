package com.example.aesculapius.ui.airquality

data class LocationPickerUiState(
    val lat: Double = 55.751244,
    val lon: Double = 37.618423,
    val currentName: String = "",
    val isLoadingName: Boolean = false,
    val isInitialised: Boolean = false
)
