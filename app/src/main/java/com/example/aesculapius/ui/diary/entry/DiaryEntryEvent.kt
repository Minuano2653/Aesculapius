package com.example.aesculapius.ui.diary.entry

import java.time.LocalDate

sealed interface DiaryEntryEvent {
    data class OnDateChanged(val date: LocalDate) : DiaryEntryEvent
    data class OnSeverityChanged(val symptomName: String, val severity: Int) : DiaryEntryEvent
    data class OnNoteChanged(val note: String) : DiaryEntryEvent
    object OnSave : DiaryEntryEvent
}
