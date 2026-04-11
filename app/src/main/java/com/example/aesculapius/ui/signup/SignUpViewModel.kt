package com.example.aesculapius.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.R
import com.example.aesculapius.database.UserAuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val userAuthRepository: UserAuthRepository) :
    ViewModel() {
    private val _uiStateSignUp = MutableStateFlow(SignUpUiState())
    val uiStateSingUp: StateFlow<SignUpUiState> = _uiStateSignUp

    private val _uiEvent = Channel<SignUpUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.OnMorningReminderChanged -> {
                _uiStateSignUp.update { it.copy(morningReminder = event.morningReminder) }
            }

            is SignUpEvent.OnEveningReminderChanged -> {
                _uiStateSignUp.update { it.copy(eveningReminder = event.eveningReminder) }
            }

            is SignUpEvent.OnNameChanged -> {
                _uiStateSignUp.update { it.copy(name = event.name) }
            }

            is SignUpEvent.OnSurnameChanged -> {
                _uiStateSignUp.update { it.copy(surname = event.surname) }
            }

            is SignUpEvent.OnPatronymicChanged -> {
                _uiStateSignUp.update { it.copy(patronymic = event.patronymic) }
            }

            is SignUpEvent.OnHeightChanged -> {
                _uiStateSignUp.update { it.copy(height = event.height) }
            }

            is SignUpEvent.OnWeightChanged -> {
                _uiStateSignUp.update { it.copy(weight = event.weight) }
            }

            is SignUpEvent.OnBirthdayChanged -> {
                _uiStateSignUp.update { it.copy(birthday = event.birthday) }
            }

            is SignUpEvent.OnEmailChanged -> {
                _uiStateSignUp.update { it.copy(email = event.email) }
            }

            is SignUpEvent.OnSecondPasswordChanged -> {
                _uiStateSignUp.update { it.copy(secondPassword = event.secondPassword) }
            }

            is SignUpEvent.OnFirstPasswordChanged -> {
                _uiStateSignUp.update { it.copy(firstPassword = event.firstPassword) }
            }

            is SignUpEvent.OnUpdateFirstPasswordError -> {
                _uiStateSignUp.update { it.copy(firstPasswordError = event.firstPasswordError) }
            }

            is SignUpEvent.OnUpdateSecondPasswordError -> {
                _uiStateSignUp.update { it.copy(secondPasswordError = event.secondPasswordError) }
            }

            is SignUpEvent.OnCheckEmailIsValid -> {
                val EMAIL_ADDRESS_PATTERN = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$"

                if (event.email.isEmpty() ||
                    event.email[0].isDigit() ||
                    EMAIL_ADDRESS_PATTERN.toRegex().matches(event.email).not()
                ) {
                    _uiStateSignUp.update {
                        it.copy(emailError = "Проверь, что вводишь почту в правильном формате, например mail@example.com")
                    }
                } else {
                    _uiStateSignUp.update { it.copy(currentPage = it.currentPage + 1) }
                }
            }

            is SignUpEvent.OnNextPage -> {
                _uiStateSignUp.update { it.copy(currentPage = it.currentPage + 1) }
            }

            is SignUpEvent.OnClickRegister ->
                viewModelScope.launch {
                    try {
                        userAuthRepository.signup(
                            event.login,
                            event.password
                        ) { isSuccessful, errorMessage, userId ->
                            viewModelScope.launch {
                                if (isSuccessful) {
                                    _uiEvent.send(SignUpUiEvent.NavigateToHome(userId))
                                } else {
                                    when (errorMessage) {
                                        is FirebaseNetworkException ->
                                            _uiEvent.send(SignUpUiEvent.ShowToast(R.string.check_internet_connection))

                                        is FirebaseAuthInvalidCredentialsException ->
                                            _uiEvent.send(SignUpUiEvent.ShowToast(R.string.check_email_password))

                                        is FirebaseAuthUserCollisionException ->
                                            _uiEvent.send(SignUpUiEvent.ShowToast(R.string.email_exist))

                                        else ->
                                            _uiEvent.send(SignUpUiEvent.ShowToast(R.string.something_went_wrong))
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
