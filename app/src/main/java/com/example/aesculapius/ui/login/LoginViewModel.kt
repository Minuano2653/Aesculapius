package com.example.aesculapius.ui.login

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.R
import com.example.aesculapius.database.UserAuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userAuthRepository: UserAuthRepository) : ViewModel() {
    private val _loginUiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    fun onLoginEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnLoginChanged -> {
                _loginUiState.update { it.copy(login = event.login) }
            }

            is LoginEvent.OnPasswordChanged -> {
                _loginUiState.update { it.copy(password = event.password) }
            }

            is LoginEvent.OnClickLogin ->
                viewModelScope.launch {
                    try {
                        userAuthRepository.login(event.login, event.password) { isSuccessful, errorMessage, userId ->
                            if (isSuccessful)
                                event.onEndLogin(userId)
                            else {
                                when (errorMessage) {
                                    is FirebaseNetworkException ->
                                        Toast.makeText(event.context, event.context.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()

                                    is FirebaseAuthInvalidCredentialsException ->
                                        _loginUiState.update {
                                            it.copy(
                                                loginError = event.context.getString(R.string.wrong_email),
                                                passwordError = event.context.getString(R.string.wrong_password)
                                            )
                                        }

                                    else ->
                                        Toast.makeText(event.context, event.context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }
}