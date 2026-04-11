package com.example.aesculapius.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.R
import com.example.aesculapius.database.UserAuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userAuthRepository: UserAuthRepository) : ViewModel() {
    private val _loginUiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _uiEvent = Channel<LoginUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

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
                            viewModelScope.launch {
                                if (isSuccessful) {
                                    _uiEvent.send(LoginUiEvent.NavigateToHome(userId))
                                } else {
                                    when (errorMessage) {
                                        is FirebaseNetworkException ->
                                            _uiEvent.send(LoginUiEvent.ShowToast(R.string.check_internet_connection))

                                        is FirebaseAuthInvalidCredentialsException ->
                                            _loginUiState.update {
                                                it.copy(
                                                    loginError = "Неверная почта",
                                                    passwordError = "Неверный пароль"
                                                )
                                            }

                                        else ->
                                            _uiEvent.send(LoginUiEvent.ShowToast(R.string.something_went_wrong))
                                    }
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
