package com.example.aesculapius.ui.login

data class LoginUiState(
    val login: String = "",
    val password: String = "",
    val passwordError: String = "",
    val loginError: String = ""
)