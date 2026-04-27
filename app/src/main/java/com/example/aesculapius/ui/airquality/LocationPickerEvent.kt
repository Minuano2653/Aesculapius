package com.example.aesculapius.ui.airquality

sealed interface LocationPickerEvent {
    data class OnCameraIdle(val lat: Double, val lon: Double) : LocationPickerEvent
    data object OnRequestCurrentLocation : LocationPickerEvent
    data object OnConfirmLocation : LocationPickerEvent
}
