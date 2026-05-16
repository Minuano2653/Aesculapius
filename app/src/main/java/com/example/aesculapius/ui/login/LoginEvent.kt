package com.example.aesculapius.ui.login

sealed interface LoginEvent {
    data class OnLoginChanged(val login: String) : LoginEvent
    data class OnPasswordChanged(val password: String) : LoginEvent
    data class OnClickLogin(val login: String, val password: String) : LoginEvent
    data object OnOpenResetSheet : LoginEvent
    data object OnDismissResetSheet : LoginEvent
    data class OnResetEmailChanged(val email: String) : LoginEvent
    data class OnSendResetEmail(val email: String) : LoginEvent
}