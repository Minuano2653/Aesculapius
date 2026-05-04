package com.example.aesculapius.ui.symptoms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.database.UserPreferencesRepository
import com.example.aesculapius.domain.symptoms.usecases.AddSymptomUseCase
import com.example.aesculapius.domain.symptoms.usecases.DeleteSymptomUseCase
import com.example.aesculapius.domain.symptoms.usecases.GetSymptomsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SymptomsViewModel @Inject constructor(
    private val addSymptomUseCase: AddSymptomUseCase,
    private val deleteSymptomUseCase: DeleteSymptomUseCase,
    private val getSymptomsUseCase: GetSymptomsUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _symptoms = MutableStateFlow<List<SymptomItem>>(emptyList())
    val symptoms: StateFlow<List<SymptomItem>> = _symptoms

    init {
        loadSymptoms()
    }

    private fun loadSymptoms() {
        viewModelScope.launch {
            _symptoms.value = getSymptomsUseCase()
        }
    }

    fun onEvent(event: SymptomEvent) {
        when (event) {
            is SymptomEvent.OnAddSymptom -> viewModelScope.launch {
                val userId = userPreferencesRepository.user.first().id ?: ""
                addSymptomUseCase(userId, event.name)
                loadSymptoms()
            }
            is SymptomEvent.OnDeleteSymptom -> viewModelScope.launch {
                val userId = userPreferencesRepository.user.first().id ?: ""
                deleteSymptomUseCase(userId, event.symptomId)
                loadSymptoms()
            }
        }
    }
}
