package com.example.aesculapius.ui.tests

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavOptionsBuilder
import com.example.aesculapius.R
import com.example.aesculapius.ui.TopBar
import com.example.aesculapius.ui.profile.ProfileEvent
import com.example.aesculapius.ui.signup.SignUpUiState
import com.example.aesculapius.ui.signup.TextInput
import java.time.LocalDateTime

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MetricsTestScreen(
    onProfileEvent: (ProfileEvent) -> Unit,
    onTestsEvent: (TestsEvent) -> Unit,
    navigate: (String, NavOptionsBuilder.() -> Unit) -> Unit,
    onNavigateBack: () -> Unit,
    userUiState: SignUpUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequesterSecondMetrics = remember { FocusRequester() }
    val focusRequesterThirdMetrics = remember { FocusRequester() }

    var firstMetrics by remember { mutableStateOf("") }
    var secondMetrics by remember { mutableStateOf("") }
    var thirdMetrics by remember { mutableStateOf("") }

    BackHandler { onNavigateBack() }
    Scaffold(topBar = {
        TopBar(
            onNavigateBack = { onNavigateBack() },
            text = stringResource(id = R.string.enter_metrics),
            existHelpButton = true,
            onClickHelpButton = { /* TODO */ })
    }) { paddingValues ->
        Column(
            modifier = modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = 24.dp,
                    end = 24.dp
                )
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.enter_psv_text),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp, bottom = 40.dp),
                textAlign = TextAlign.Center
            )
            TextInput(
                text = firstMetrics,
                onValueChanged = { firstMetrics = it },
                hint = stringResource(R.string.first_metric),
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onNext = { focusRequesterSecondMetrics.requestFocus() }),
                focusRequester = FocusRequester()
            )

            TextInput(
                text = secondMetrics,
                onValueChanged = { secondMetrics = it },
                hint = stringResource(R.string.second_metric),
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onNext = { focusRequesterThirdMetrics.requestFocus() }),
                focusRequester = focusRequesterSecondMetrics
            )

            TextInput(
                text = thirdMetrics,
                onValueChanged = { thirdMetrics = it },
                hint = stringResource(R.string.third_metric),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                focusRequester = focusRequesterThirdMetrics
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    try {
                        val temp1 = firstMetrics.toFloat()
                        val temp2 = secondMetrics.toFloat()
                        val temp3 = thirdMetrics.toFloat()
                        if (temp1 < 0 || temp2 < 0 || temp3 < 0 || temp1.toInt().toString().length > 3
                            || temp2.toInt().toString().length > 3 || temp3.toInt().toString().length > 3)
                            throw IllegalArgumentException(context.getString(R.string.wrong_metrics))
                        else {
                            val now = LocalDateTime.now()
                            if (now.isAfter(userUiState.morningReminder) && now.isBefore(userUiState.morningReminder.plusMinutes(6))) {
                                onProfileEvent(ProfileEvent.OnSaveMorningTime(now.plusDays(1)))
                                onTestsEvent(TestsEvent.OnInsertNewMetrics(userUiState.id ?: "", temp1, temp2, temp3))
                            } else {
                                onProfileEvent(ProfileEvent.OnSaveEveningTime(now.plusDays(1)))
                                onTestsEvent(TestsEvent.OnUpdateNewMetrics(userUiState.id ?: "", temp1, temp2, temp3))
                            }
                            navigate(TestsScreen.route) {
                                popUpTo(TestsScreen.route) { inclusive = false }
                            }
                        }
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, context.getText(R.string.wrong_numbers), Toast.LENGTH_SHORT)
                            .show()
                    } catch (e: IllegalArgumentException) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .height(56.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.send_data),
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}