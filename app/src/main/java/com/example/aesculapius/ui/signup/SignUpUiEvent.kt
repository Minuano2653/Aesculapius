package com.example.aesculapius.ui.signup

import androidx.annotation.StringRes

sealed interface SignUpUiEvent {
    data class NavigateToHome(val userId: String) : SignUpUiEvent
    data class ShowToast(@StringRes val messageRes: Int) : SignUpUiEvent
}
