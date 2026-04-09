package com.example.aesculapius.ui.login

import android.content.Context

sealed interface LoginEvent {
    data class OnLoginChanged(val login: String): LoginEvent
    data class OnPasswordChanged(val password: String): LoginEvent
    data class OnClickLogin(val login: String, val password: String, val context: Context, val onEndLogin: (String) -> Unit): LoginEvent
}