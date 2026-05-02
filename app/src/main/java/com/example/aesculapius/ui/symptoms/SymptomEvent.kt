package com.example.aesculapius.ui.symptoms

sealed interface SymptomEvent {
    data class OnAddSymptom(val name: String) : SymptomEvent
    data class OnDeleteSymptom(val symptomId: String) : SymptomEvent
}
