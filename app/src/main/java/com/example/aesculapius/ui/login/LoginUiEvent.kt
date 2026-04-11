package com.example.aesculapius.ui.login

import androidx.annotation.StringRes

sealed interface LoginUiEvent {
    data class NavigateToHome(val userId: String) : LoginUiEvent
    data class ShowToast(@StringRes val messageRes: Int) : LoginUiEvent
}
