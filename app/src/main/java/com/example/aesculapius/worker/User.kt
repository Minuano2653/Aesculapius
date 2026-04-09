package com.example.aesculapius.worker

import com.example.aesculapius.ui.tests.MetricsItem
import com.example.aesculapius.ui.tests.ScoreItem
import com.example.aesculapius.ui.therapy.MedicineItem
import java.time.LocalDate

data class User(
    val name: String = "",
    val surname: String = "",
    val patronymic: String = "",
    val height: Float = 0.0f,
    val weight: Float = 0.0f,
    val birthDate: String = "",
    val morningReminder: String = "",
    val eveningReminder: String = "",
    val recommendationTestDate: String = "",
    val astTestDate: String = "",
    val medicines: List<MedicineItem> = listOf(),
    val metrics: List<MetricsItem> = listOf(),
    val astTests: List<ScoreItem> = listOf()
)
