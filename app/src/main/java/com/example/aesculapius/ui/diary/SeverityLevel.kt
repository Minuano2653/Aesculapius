package com.example.aesculapius.ui.diary

enum class SeverityLevel(val value: Int) {
    NONE(0),
    MILD(1),
    MODERATE(2),
    SEVERE(3);

    companion object {
        fun fromInt(v: Int): SeverityLevel = entries.find { it.value == v } ?: NONE
    }
}
