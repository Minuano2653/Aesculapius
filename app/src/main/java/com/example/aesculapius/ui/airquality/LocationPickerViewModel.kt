package com.example.aesculapius.ui.airquality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.domain.airquality.model.LocationData
import com.example.aesculapius.domain.airquality.usecases.GetAddressByCoordinatesUseCase
import com.example.aesculapius.domain.airquality.usecases.GetCurrentDeviceLocationUseCase
import com.example.aesculapius.domain.airquality.usecases.GetSavedAirQualityCacheFlowUseCase
import com.example.aesculapius.domain.airquality.usecases.SaveSelectedLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LocationPickerEffect {
    data class ShowError(val message: String) : LocationPickerEffect
    data object LocationSaved : LocationPickerEffect
}

@OptIn(FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class LocationPickerViewModel @Inject constructor(
    private val getAddressByCoordinatesUseCase: GetAddressByCoordinatesUseCase,
    private val saveSelectedLocationUseCase: SaveSelectedLocationUseCase,
    private val getCurrentDeviceLocationUseCase: GetCurrentDeviceLocationUseCase,
    private val getSavedAirQualityCacheFlowUseCase: GetSavedAirQualityCacheFlowUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationPickerUiState())
    val uiState: StateFlow<LocationPickerUiState> = _uiState.asStateFlow()

    private val _moveCameraTo = MutableSharedFlow<Pair<Double, Double>>(extraBufferCapacity = 1)
    val moveCameraTo = _moveCameraTo.asSharedFlow()

    private val _effects = Channel<LocationPickerEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val cameraTarget = MutableStateFlow<Pair<Double, Double>?>(null)

    init {
        viewModelScope.launch {
            val savedLocation = getSavedAirQualityCacheFlowUseCase().first()?.location
            val (lat, lon, name) = if (savedLocation != null) {
                Triple(savedLocation.lat, savedLocation.lon, savedLocation.name)
            } else {
                val state = _uiState.value
                Triple(state.lat, state.lon, "")
            }
            _uiState.update {
                it.copy(
                    lat = lat,
                    lon = lon,
                    currentName = name,
                    isLoadingName = name.isBlank(),
                    isInitialised = true
                )
            }
            if (name.isBlank()) cameraTarget.value = lat to lon
        }

        viewModelScope.launch {
            cameraTarget
                .filterNotNull()
                .debounce(400)
                .distinctUntilChanged()
                .mapLatest { (lat, lon) ->
                    runCatching { getAddressByCoordinatesUseCase(lat, lon) }.getOrDefault("")
                }
                .collect { name ->
                    _uiState.update { it.copy(currentName = name, isLoadingName = false) }
                }
        }
    }

    fun onEvent(event: LocationPickerEvent) {
        when (event) {
            is LocationPickerEvent.OnCameraIdle -> {
                _uiState.update {
                    it.copy(lat = event.lat, lon = event.lon, isLoadingName = true)
                }
                cameraTarget.value = event.lat to event.lon
            }
            LocationPickerEvent.OnRequestCurrentLocation -> {
                viewModelScope.launch {
                    val location = runCatching { getCurrentDeviceLocationUseCase() }.getOrNull()
                    if (location == null) {
                        _effects.trySend(LocationPickerEffect.ShowError("location_unavailable"))
                        return@launch
                    }
                    val (lat, lon) = location
                    _uiState.update {
                        it.copy(lat = lat, lon = lon, isLoadingName = true)
                    }
                    _moveCameraTo.tryEmit(lat to lon)
                    cameraTarget.value = lat to lon
                }
            }
            LocationPickerEvent.OnConfirmLocation -> {
                viewModelScope.launch {
                    val state = _uiState.value
                    val name = state.currentName.ifBlank {
                        runCatching { getAddressByCoordinatesUseCase(state.lat, state.lon) }
                            .getOrDefault("")
                    }
                    saveSelectedLocationUseCase(
                        LocationData(lat = state.lat, lon = state.lon, name = name)
                    )
                    _effects.trySend(LocationPickerEffect.LocationSaved)
                }
            }
        }
    }
}
