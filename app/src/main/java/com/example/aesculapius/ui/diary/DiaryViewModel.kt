package com.example.aesculapius.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.database.UserPreferencesRepository
import com.example.aesculapius.domain.diary.usecases.DeleteDiaryEntryUseCase
import com.example.aesculapius.domain.diary.usecases.GetDiaryEntriesUseCase
import com.example.aesculapius.domain.symptoms.usecases.GetSymptomsUseCase
import com.example.aesculapius.ui.symptoms.SymptomItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    getDiaryEntries: GetDiaryEntriesUseCase,
    private val getSymptomsUseCase: GetSymptomsUseCase,
    private val deleteDiaryEntryUseCase: DeleteDiaryEntryUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val entries: StateFlow<List<DiaryEntryItem>> = getDiaryEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _symptoms = MutableStateFlow<List<SymptomItem>>(emptyList())
    val symptoms: StateFlow<List<SymptomItem>> = _symptoms

    init {
        viewModelScope.launch {
            _symptoms.value = getSymptomsUseCase()
        }
    }

    fun deleteEntry(date: LocalDate) {
        viewModelScope.launch {
            val userId = userPreferencesRepository.user.first().id ?: ""
            deleteDiaryEntryUseCase(userId, date)
        }
    }
}
