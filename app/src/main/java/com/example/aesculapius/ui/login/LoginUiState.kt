package com.example.aesculapius.ui.login

data class LoginUiState(
    val login: String = "",
    val password: String = "",
    val passwordError: String = "",
    val loginError: String = "",
    val showResetSheet: Boolean = false,
    val resetEmail: String = "",
    val resetEmailError: String = "",
    val isResetSending: Boolean = false,
)