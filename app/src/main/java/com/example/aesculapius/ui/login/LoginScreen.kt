package com.example.aesculapius.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
}

@Preview
@Composable
fun PreviewLoginScreen() {
    AesculapiusTheme {
        LoginScreen(onEndLogin = {}, navigate = {})
    }
}
