package com.example.aesculapius.ui.diary.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.database.UserPreferencesRepository
import com.example.aesculapius.domain.diary.usecases.GetDiaryEntryByDateUseCase
import com.example.aesculapius.domain.diary.usecases.SaveDiaryEntryUseCase
import com.example.aesculapius.domain.symptoms.usecases.GetSymptomsFlowUseCase
import com.example.aesculapius.ui.diary.DiaryEntryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saveDiaryEntryUseCase: SaveDiaryEntryUseCase,
    private val getEntryByDateUseCase: GetDiaryEntryByDateUseCase,
    private val getSymptomsFlowUseCase: GetSymptomsFlowUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val initialDate: LocalDate = run {
        val arg = savedStateHandle.get<String>(DiaryEntryScreen.depart) ?: "new"
        if (arg == "new") LocalDate.now() else runCatching { LocalDate.parse(arg) }.getOrDefault(LocalDate.now())
    }

    private val _state = MutableStateFlow(DiaryEntryUiState(date = initialDate))
    val state: StateFlow<DiaryEntryUiState> = _state

    init {
        getSymptomsFlowUseCase()
            .onEach { syms -> _state.update { it.copy(symptoms = syms) } }
            .launchIn(viewModelScope)
        viewModelScope.launch { loadExistingEntry(initialDate) }
    }

    fun onEvent(event: DiaryEntryEvent) {
        when (event) {
            is DiaryEntryEvent.OnDateChanged -> viewModelScope.launch {
                loadExistingEntry(event.date)
            }
            is DiaryEntryEvent.OnSeverityChanged -> _state.update { st ->
                st.copy(severityByName = st.severityByName + (event.symptomName to event.severity))
            }
            is DiaryEntryEvent.OnNoteChanged -> _state.update { it.copy(note = event.note) }
            DiaryEntryEvent.OnSave -> saveCurrent()
        }
    }

    private suspend fun loadExistingEntry(date: LocalDate) {
        val existing = getEntryByDateUseCase(date)
        _state.update { st ->
            st.copy(
                date = date,
                isEdit = existing != null,
                severityByName = existing?.let { parseSymptoms(it.symptomsJson) } ?: emptyMap(),
                note = existing?.note ?: "",
                preservedCreatedAt = existing?.createdAt
            )
        }
    }

    private fun saveCurrent() {
        viewModelScope.launch {
            val current = _state.value
            val userId = userPreferencesRepository.user.first().id ?: ""
            val createdAt = current.preservedCreatedAt ?: System.currentTimeMillis()
            val fullMap = current.symptoms.associate { sym ->
                sym.name to (current.severityByName[sym.name] ?: 0)
            }
            val symptomsJson = JSONObject(fullMap as Map<*, *>).toString()
            saveDiaryEntryUseCase(
                userId = userId,
                entry = DiaryEntryItem(
                    date = current.date,
                    symptomsJson = symptomsJson,
                    note = current.note.trim(),
                    createdAt = createdAt
                )
            )
        }
    }

    private fun parseSymptoms(json: String): Map<String, Int> {
        if (json.isBlank()) return emptyMap()
        val obj = JSONObject(json)
        val result = mutableMapOf<String, Int>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            result[key] = obj.getInt(key)
        }
        return result
    }
}
