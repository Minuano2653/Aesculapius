package com.example.aesculapius.ui.airquality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.domain.airquality.model.AirQualityCache
import com.example.aesculapius.domain.airquality.usecases.GetSavedAirQualityCacheFlowUseCase
import com.example.aesculapius.domain.airquality.usecases.RefreshAirQualityForSavedLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AirQualityEffect {
    data object RefreshFailed : AirQualityEffect
}

@HiltViewModel
class AirQualityViewModel @Inject constructor(
    getSavedAirQualityCacheFlowUseCase: GetSavedAirQualityCacheFlowUseCase,
    private val refreshAirQualityForSavedLocationUseCase: RefreshAirQualityForSavedLocationUseCase
) : ViewModel() {

    val cache: StateFlow<AirQualityCache?> = getSavedAirQualityCacheFlowUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _effects = Channel<AirQualityEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        refresh()
    }

    fun onEvent(event: AirQualityEvent) {
        when (event) {
            AirQualityEvent.OnRefresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _isRefreshing.update { true }
            val ok = runCatching { refreshAirQualityForSavedLocationUseCase() }.getOrDefault(false)
            if (!ok) _effects.trySend(AirQualityEffect.RefreshFailed)
            _isRefreshing.update { false }
        }
    }
}
