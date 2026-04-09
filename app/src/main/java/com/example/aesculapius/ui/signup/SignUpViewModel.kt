package com.example.aesculapius.ui.signup

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.R
import com.example.aesculapius.database.UserAuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val userAuthRepository: UserAuthRepository) :
    ViewModel() {
    private val _uiStateSignUp = MutableStateFlow(SignUpUiState())
    val uiStateSingUp: StateFlow<SignUpUiState> = _uiStateSignUp

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.OnMorningReminderChanged -> {
                _uiStateSignUp.update {
                    it.copy(morningReminder = event.morningReminder)
                }
            }

            is SignUpEvent.OnEveningReminderChanged -> {
                _uiStateSignUp.update {
                    it.copy(eveningReminder = event.eveningReminder)
                }
            }

            is SignUpEvent.OnNameChanged -> {
                _uiStateSignUp.update {
                    it.copy(name = event.name)
                }
            }

            is SignUpEvent.OnSurnameChanged -> {
                _uiStateSignUp.update {
                    it.copy(surname = event.surname)
                }
            }

            is SignUpEvent.OnPatronymicChanged -> {
                _uiStateSignUp.update {
                    it.copy(patronymic = event.patronymic)
                }
            }

            is SignUpEvent.OnHeightChanged -> {
                _uiStateSignUp.update {
                    it.copy(height = event.height)
                }
            }

            is SignUpEvent.OnWeightChanged -> {
                _uiStateSignUp.update {
                    it.copy(weight = event.weight)
                }
            }

            is SignUpEvent.OnBirthdayChanged -> {
                _uiStateSignUp.update {
                    it.copy(birthday = event.birthday)
                }
            }

            is SignUpEvent.OnEmailChanged -> {
                _uiStateSignUp.update {
                    it.copy(email = event.email)
                }
            }

            is SignUpEvent.OnSecondPasswordChanged -> {
                _uiStateSignUp.update {
                    it.copy(secondPassword = event.secondPassword)
                }
            }

            is SignUpEvent.OnFirstPasswordChanged -> {
                _uiStateSignUp.update {
                    it.copy(firstPassword = event.firstPassword)
                }
            }

            is SignUpEvent.OnUpdateFirstPasswordError -> {
                _uiStateSignUp.update {
                    it.copy(firstPasswordError = event.firstPasswordError)
                }
            }

            is SignUpEvent.OnUpdateSecondPasswordError -> {
                _uiStateSignUp.update {
                    it.copy(secondPasswordError = event.secondPasswordError)
                }
            }

            is SignUpEvent.OnCheckEmailIsValid -> {
                val EMAIL_ADDRESS_PATTERN = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$"

                if (event.email.isEmpty() ||
                    event.email[0].isDigit() ||
                    EMAIL_ADDRESS_PATTERN.toRegex().matches(event.email).not()
                )
                    _uiStateSignUp.update {
                        it.copy(emailError = "Проверь, что вводишь почту в правильном формате, например mail@example.com")
                    }
                else
                    event.onComplete()
            }

            is SignUpEvent.OnClickRegister ->
                viewModelScope.launch {
                    try {
                        userAuthRepository.signup(
                            event.login,
                            event.password
                        ) { isSuccessful, errorMessage, userId ->
                            if (isSuccessful)
                                event.onEndRegistration(userId)
                            else {
                                when (errorMessage) {
                                    is FirebaseNetworkException ->
                                        Toast.makeText(
                                            event.context,
                                            event.context.getString(R.string.check_internet_connection),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    is FirebaseAuthInvalidCredentialsException -> {
                                        Toast.makeText(
                                            event.context,
                                            event.context.getString(R.string.check_email_password),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    is FirebaseAuthUserCollisionException -> {
                                        Toast.makeText(
                                            event.context,
                                            event.context.getString(R.string.email_exist),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    else -> {
                                        Toast.makeText(
                                            event.context,
                                            event.context.getString(R.string.something_went_wrong),
                                            Toast.LENGTH_SHORT
                                        ).show()
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