package com.example.aesculapius.domain.auth.model

import com.example.aesculapius.ui.signup.DoctorUiState
import com.example.aesculapius.ui.signup.SignUpUiState

sealed interface SessionState {
    data object Loading : SessionState
    data object Guest : SessionState
    data class Patient(val state: SignUpUiState) : SessionState
    data class Doctor(val state: DoctorUiState) : SessionState
}
