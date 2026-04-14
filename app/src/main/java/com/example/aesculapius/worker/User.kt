package com.example.aesculapius.worker

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
    val astTestDate: String = ""
)
