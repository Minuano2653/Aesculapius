package com.example.aesculapius.ui.profile

import com.example.aesculapius.ui.signup.SignUpUiState
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface ProfileEvent {
    data class OnSaveAstTestDate(val astTestDate: LocalDate): ProfileEvent
    data class OnSaveRecommendationTestDate(val recommendationTestDate: LocalDate): ProfileEvent
    data class OnUpdateUserProfile(val signUpUiState: SignUpUiState): ProfileEvent
    data class OnSaveEveningTime(val eveningTime: LocalDateTime): ProfileEvent
    data class OnSaveMorningTime(val morningTime: LocalDateTime): ProfileEvent
    data class OnSaveNewUser(val signUpUiState: SignUpUiState): ProfileEvent
    data class OnLoginUser(val userId: String): ProfileEvent
    data object OnRefreshAirQuality: ProfileEvent
    data object OnSignOut: ProfileEvent
}