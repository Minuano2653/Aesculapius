package com.example.aesculapius.ui.signup

import android.content.Context
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface SignUpEvent {
    data class OnNameChanged(val name: String): SignUpEvent
    data class OnSurnameChanged(val surname: String): SignUpEvent
    data class OnPatronymicChanged(val patronymic: String): SignUpEvent
    data class OnHeightChanged(val height: String): SignUpEvent
    data class OnWeightChanged(val weight: String): SignUpEvent
    data class OnBirthdayChanged(val birthday: LocalDate): SignUpEvent
    data class OnEveningReminderChanged(val eveningReminder: LocalDateTime): SignUpEvent
    data class OnMorningReminderChanged(val morningReminder: LocalDateTime): SignUpEvent
    data class OnEmailChanged(val email: String): SignUpEvent
    data class OnFirstPasswordChanged(val firstPassword: String): SignUpEvent
    data class OnSecondPasswordChanged(val secondPassword: String): SignUpEvent
    data class OnClickRegister(val login: String, val password: String, val context: Context, val onEndRegistration: (String) -> Unit): SignUpEvent
    data class OnCheckEmailIsValid(val email: String, val onComplete: () -> Unit): SignUpEvent
    data class OnUpdateSecondPasswordError(val secondPasswordError: String): SignUpEvent
    data class OnUpdateFirstPasswordError(val firstPasswordError: String): SignUpEvent
}