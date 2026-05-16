package com.example.aesculapius.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aesculapius.R
import com.example.aesculapius.ui.navigation.NavigationDestination
import com.example.aesculapius.ui.signup.SignUpScreen
import com.example.aesculapius.ui.signup.TextInput
import com.example.aesculapius.ui.theme.AesculapiusTheme
import com.example.aesculapius.ui.theme.errorLoginField

object LoginScreen : NavigationDestination {
    override val route = "LoginScreen"
}

@Composable
fun LoginScreen(
    navigate: (String) -> Unit,
    onEndLogin: (String) -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val loginUiState by loginViewModel.loginUiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        loginViewModel.uiEvent.collect { event ->
            when (event) {
                is LoginUiEvent.NavigateToHome -> onEndLogin(event.userId)
                is LoginUiEvent.ShowToast ->
                    Toast.makeText(context, context.getString(event.messageRes), Toast.LENGTH_SHORT).show()
            }
        }
    }

    LoginScreenContent(
        loginUiState = loginUiState,
        onLoginEvent = loginViewModel::onLoginEvent,
        navigate = navigate
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreenContent(
    onLoginEvent: (LoginEvent) -> Unit,
    loginUiState: LoginUiState,
    navigate: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequesterPassword = remember { FocusRequester() }

    LazyColumn(
        modifier = Modifier
            .padding(top = 80.dp)
            .padding(horizontal = 23.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = stringResource(R.string.your_helper_with_asthma),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )
            TextInput(
                text = loginUiState.login,
                onValueChanged = { onLoginEvent(LoginEvent.OnLoginChanged(it)) },
                hint = stringResource(R.string.email),
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Password
                ),
                focusRequester = FocusRequester(),
                keyboardActions = KeyboardActions(onNext = { focusRequesterPassword.requestFocus() }),
                isError = loginUiState.loginError.isNotEmpty()
            )
            if (loginUiState.loginError.isNotEmpty())
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = loginUiState.loginError,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = errorLoginField
                    )
                }
            TextInput(
                text = loginUiState.password,
                onValueChanged = { onLoginEvent(LoginEvent.OnPasswordChanged(it)) },
                hint = stringResource(R.string.password),
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                focusRequester = focusRequesterPassword,
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                isError = loginUiState.passwordError.isNotEmpty(),
                visualTransformation = PasswordVisualTransformation()
            )
            if (loginUiState.passwordError.isNotEmpty())
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = loginUiState.passwordError,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = errorLoginField
                    )
                }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onLoginEvent(LoginEvent.OnOpenResetSheet) }) {
                    Text(
                        text = stringResource(R.string.forgot_password),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Button(
                onClick = {
                    onLoginEvent(
                        LoginEvent.OnClickLogin(
                            login = loginUiState.login,
                            password = loginUiState.password
                        )
                    )
                },
                modifier = Modifier
                    .padding(bottom = 16.dp, top = 51.dp)
                    .height(56.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(disabledContainerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.login),
                    style = MaterialTheme.typography.displaySmall
                )
            }

            TextButton(onClick = { navigate(SignUpScreen.route) }) {
                Text(
                    text = stringResource(R.string.new_user),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (loginUiState.showResetSheet) {
        ResetPasswordBottomSheet(
            email = loginUiState.resetEmail,
            emailError = loginUiState.resetEmailError,
            isSending = loginUiState.isResetSending,
            onEmailChanged = { onLoginEvent(LoginEvent.OnResetEmailChanged(it)) },
            onSendClicked = { onLoginEvent(LoginEvent.OnSendResetEmail(loginUiState.resetEmail)) },
            onDismiss = { onLoginEvent(LoginEvent.OnDismissResetSheet) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResetPasswordBottomSheet(
    email: String,
    emailError: String,
    isSending: Boolean,
    onEmailChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 16.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.password_recovery_title),
                style = MaterialTheme.typography.titleLarge
            )
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChanged,
                label = {
                    Text(
                        text = stringResource(R.string.password_recovery_hint),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Email
                ),
                singleLine = true,
                isError = emailError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            if (emailError.isNotEmpty())
                Text(
                    text = emailError,
                    style = MaterialTheme.typography.bodySmall,
                    color = errorLoginField,
                    modifier = Modifier.padding(start = 16.dp)
                )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    modifier = Modifier.widthIn(min = 106.dp),
                    onClick = onDismiss
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.padding(horizontal = 4.dp))
                Button(
                    modifier = Modifier.widthIn(min = 106.dp),
                    onClick = onSendClicked,
                    enabled = email.isNotBlank() && !isSending,
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.send),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    AesculapiusTheme {
        LoginScreen(onEndLogin = {}, navigate = {})
    }
}
