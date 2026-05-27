package com.example.aesculapius.ui.doctor.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.domain.patients.model.PatientSummary
import com.example.aesculapius.domain.patients.usecases.GetAllPatientsUseCase
import com.example.aesculapius.domain.patients.usecases.SearchPatientsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientsListViewModel @Inject constructor(
    private val getAllPatientsUseCase: GetAllPatientsUseCase,
    private val searchPatientsUseCase: SearchPatientsUseCase
) : ViewModel() {

    private val _patients = MutableStateFlow<List<PatientSummary>>(emptyList())
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val displayedPatients: StateFlow<List<PatientSummary>> = combine(_patients, _query) { all, q ->
        searchPatientsUseCase(all, q)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        refresh()
    }

    fun onQueryChanged(value: String) {
        _query.value = value
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching { getAllPatientsUseCase() }
                .onSuccess { _patients.value = it }
            _isLoading.value = false
        }
    }
}
