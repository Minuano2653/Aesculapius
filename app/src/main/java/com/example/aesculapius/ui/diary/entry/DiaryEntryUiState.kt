package com.example.aesculapius.ui.diary.entry

import com.example.aesculapius.ui.symptoms.SymptomItem
import java.time.LocalDate

data class DiaryEntryUiState(
    val date: LocalDate = LocalDate.now(),
    val isEdit: Boolean = false,
    val symptoms: List<SymptomItem> = emptyList(),
    val severityByName: Map<String, Int> = emptyMap(),
    val note: String = "",
    val preservedCreatedAt: Long? = null
)
