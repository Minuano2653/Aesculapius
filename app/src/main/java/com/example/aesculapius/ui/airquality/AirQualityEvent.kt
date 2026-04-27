package com.example.aesculapius.ui.airquality

sealed interface AirQualityEvent {
    data object OnRefresh : AirQualityEvent
}
