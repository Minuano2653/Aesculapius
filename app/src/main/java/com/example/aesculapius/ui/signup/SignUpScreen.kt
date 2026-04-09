package com.example.aesculapius.ui.signup

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chargemap.compose.numberpicker.ListItemPicker
import com.example.aesculapius.R
import com.example.aesculapius.data.Hours
import com.example.aesculapius.data.daysUsual
import com.example.aesculapius.data.daysSpecial
import com.example.aesculapius.data.months
import com.example.aesculapius.ui.navigation.NavigationDestination
import com.example.aesculapius.ui.theme.AesculapiusTheme
import com.example.aesculapius.ui.theme.errorLoginField
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object SignUpScreen : NavigationDestination {
    override val route = "SignUpScreen"
}

@Composable
fun SignUpScreen(
    onNavigateBack: () -> Unit,
    onChangeCurrentPage: () -> Unit,
    userUiState: SignUpUiState,
    currentPage: Int,
    onEvent: (SignUpEvent) -> Unit,
    onClickSetReminder: (Hours) -> Unit,
    onEndRegistration: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                DotsMenuSignUp(
                    totalDots = 6,
                    selectedIndex = currentPage,
                    modifier = Modifier.padding(top = 26.dp, bottom = 24.dp),
                    onNavigateBack = onNavigateBack
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 20.dp)
                .padding(paddingValues)
        ) {
            item {

                when (currentPage) {
                    0 -> EmailField(
                        email = userUiState.email,
                        emailError = userUiState.emailError,
                        onEvent = onEvent
                    )

                    1 -> PasswordFields(
                        passwordFirst = userUiState.firstPassword,
                        passwordSecond = userUiState.secondPassword,
                        onEvent = onEvent,
                        firstPasswordError = userUiState.firstPasswordError,
                        secondPasswordError = userUiState.secondPasswordError
                    )

                    2 -> FieldsFIO(
                        name = userUiState.name,
                        surname = userUiState.surname,
                        patronymic = userUiState.patronymic,
                        onEvent = onEvent
                    )

                    3 -> BirthdayFiled(onEvent = onEvent)
                    4 -> HeightWeightFields(
                        onEvent = onEvent,
                        height = userUiState.height,
                        weight = userUiState.weight
                    )

                    5 -> ReminderFields(
                        onClickSetReminder = { onClickSetReminder(it) },
                        eveningTime = userUiState.eveningReminder,
                        morningTime = userUiState.morningReminder,
                    )
                }
                Button(
                    onClick = {
                        when (currentPage) {
                            5 -> {
                                if (userUiState.eveningReminder.hour - userUiState.morningReminder.hour < 8)
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.reminder_warning),
                                        Toast.LENGTH_LONG
                                    ).show()
                                else
                                    onEvent(
                                        SignUpEvent.OnClickRegister(
                                            login = userUiState.email,
                                            password = userUiState.firstPassword,
                                            context = context,
                                            onEndRegistration = onEndRegistration
                                        )
                                    )
                            }

                            4 -> {
                                try {
                                    val heightFinal = userUiState.height.toFloat()
                                    val weightFinal = userUiState.weight.toFloat()
                                    if (!(heightFinal in 20f..300f && weightFinal in 0f..1000f))
                                        throw IllegalArgumentException(context.getString(R.string.weight_height_warning))
                                    else {
                                        onChangeCurrentPage()
                                    }
                                } catch (e: NumberFormatException) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.warning_numbers),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } catch (e: IllegalArgumentException) {
                                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                                }
                            }

                            2 -> {
                                val regex = Regex("[а-яА-Яa-zA-Z]+")
                                if (regex.matches(userUiState.name) && regex.matches(userUiState.surname) && (userUiState.patronymic.isBlank() || regex.matches(
                                        userUiState.patronymic
                                    ))
                                )
                                    onChangeCurrentPage()
                                else
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.wrong_data),
                                        Toast.LENGTH_SHORT
                                    ).show()
                            }

                            1 -> {
                                if (userUiState.firstPassword != userUiState.secondPassword)
                                    onEvent(SignUpEvent.OnUpdateSecondPasswordError("Пароли не совпадают"))
                                else onEvent(SignUpEvent.OnUpdateSecondPasswordError(""))
                                if (userUiState.firstPassword.length < 8)
                                    onEvent(SignUpEvent.OnUpdateFirstPasswordError("Пароль должен быть не меньше 8 символов"))
                                else onEvent(SignUpEvent.OnUpdateFirstPasswordError(""))
                                if (userUiState.firstPassword == userUiState.secondPassword && userUiState.firstPassword.length >= 8)
                                    onChangeCurrentPage()
                            }

                            0 -> {
                                onEvent(SignUpEvent.OnCheckEmailIsValid(email = userUiState.email, onComplete = { onChangeCurrentPage() }))
                            }

                            else -> onChangeCurrentPage()
                        }
                    },
                    enabled =
                        when (currentPage) {
                            0 -> userUiState.email != ""
                            1 -> userUiState.firstPassword != "" && userUiState.secondPassword != ""
                            2 -> userUiState.name != "" && userUiState.surname != ""
                            4 -> userUiState.height != "" && userUiState.weight != ""
                            else -> true
                        },
                    modifier = Modifier
                        .padding(bottom = 16.dp, top = 51.dp)
                        .height(56.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(disabledContainerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.next),
                        style = MaterialTheme.typography.displaySmall
                    )
                }
                TextButton(onClick = onNavigateBack,
                    Modifier
                        .padding(bottom = 24.dp)
                        .fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.i_have_account),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmailField(emailError: String, email: String, onEvent: (SignUpEvent) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Text(
        text = stringResource(R.string.type_email),
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center
    )
    TextInput(
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        text = email,
        onValueChanged = { onEvent(SignUpEvent.OnEmailChanged(it)) },
        hint = stringResource(id = R.string.email),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        modifier = Modifier.padding(top = 28.dp, bottom = 4.dp),
        isError = emailError.isNotEmpty()
    )
    if (emailError.isNotEmpty())
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = emailError,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                color = errorLoginField,
                modifier = Modifier.padding(start = 32.dp)
            )
        }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordFields(
    firstPasswordError: String,
    secondPasswordError: String,
    passwordFirst: String,
    passwordSecond: String,
    onEvent: (SignUpEvent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Text(
        text = stringResource(R.string.think_password),
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center
    )
    TextInput(
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
        text = passwordFirst,
        onValueChanged = { onEvent(SignUpEvent.OnFirstPasswordChanged(it)) },
        hint = stringResource(id = R.string.password),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        modifier = Modifier.padding(top = 28.dp, bottom = 4.dp),
        visualTransformation = PasswordVisualTransformation()
    )
    if (firstPasswordError.isNotEmpty())
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = firstPasswordError,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(start = 32.dp)
            )
        }
    TextInput(
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
        text = passwordSecond,
        onValueChanged = { onEvent(SignUpEvent.OnSecondPasswordChanged(it)) },
        hint = stringResource(R.string.retype_password),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        modifier = Modifier.padding(top = 28.dp, bottom = 4.dp),
        isError = secondPasswordError.isNotEmpty(),
        visualTransformation = PasswordVisualTransformation()
    )
    if (secondPasswordError.isNotEmpty())
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = secondPasswordError,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                color = errorLoginField,
                modifier = Modifier.padding(start = 32.dp),
            )
        }
}

@Composable
fun ReminderFields(
    onClickSetReminder: (Hours) -> Unit,
    eveningTime: LocalDateTime,
    morningTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.set_time_reminders),
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center
    )

    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 24.dp, bottom = 16.dp)
            .clickable { onClickSetReminder(Hours.Morning) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row() {
            Column(modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 16.dp)) {
                Text(
                    text = stringResource(id = R.string.morning_text),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = morningTime.format(DateTimeFormatter.ofPattern("HH")) + " : "
                            + morningTime.format(DateTimeFormatter.ofPattern("mm")),
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 28.dp)
                    .size(25.dp)
            )
        }
    }
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClickSetReminder(Hours.Evening) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row() {
            Column(modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 16.dp)) {
                Text(
                    text = stringResource(id = R.string.evening_text),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = eveningTime.format(DateTimeFormatter.ofPattern("HH")) + " : "
                            + eveningTime.format(DateTimeFormatter.ofPattern("mm")),
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 28.dp)
                    .size(25.dp)
            )
        }
    }

    Text(
        text = "Планирование твоего дня",
        style = MaterialTheme.typography.headlineLarge,
        modifier = modifier.padding(top = 40.dp, bottom = 8.dp),
        color = MaterialTheme.colorScheme.primary,
    )
    Text(text = stringResource(id = R.string.set_reminder_text), style = MaterialTheme.typography.headlineMedium)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HeightWeightFields(
    onEvent: (SignUpEvent) -> Unit,
    height: String,
    weight: String
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Text(text = stringResource(R.string.your_height), style = MaterialTheme.typography.titleMedium)
    TextInput(
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        text = height,
        onValueChanged = { onEvent(SignUpEvent.OnHeightChanged(it)) },
        hint = stringResource(id = R.string.height),
        focusRequester = FocusRequester(),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        modifier = Modifier.padding(bottom = 48.dp)
    )
    Text(text = stringResource(R.string.your_weight), style = MaterialTheme.typography.titleMedium)
    TextInput(
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        text = weight,
        onValueChanged = { onEvent(SignUpEvent.OnWeightChanged(it)) },
        hint = stringResource(id = R.string.weight),
        focusRequester = FocusRequester(),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
    )
}

@Composable
fun BirthdayFiled(onEvent: (SignUpEvent) -> Unit) {
    Text(text = stringResource(R.string.select_birthday), style = MaterialTheme.typography.titleMedium)
    var day by remember { mutableIntStateOf(1) }
    var month by remember { mutableStateOf("июн") }
    var year by remember { mutableIntStateOf(2000) }
    Row(modifier = Modifier.padding(top = 24.dp)) {
        Card(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .weight(0.7f)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        ) {
            ListItemPicker(
                modifier = Modifier.fillMaxWidth(),
                value = day,
                list = if (year % 4 != 0) (1..daysUsual[month]!!).toList() else (1..daysSpecial[month]!!).toList(),
                onValueChange = {
                    day = it
                    onEvent(SignUpEvent.OnBirthdayChanged(LocalDate.of(year, months.indexOf(month) + 1, day)))
                },
                textStyle = MaterialTheme.typography.displayMedium,
                dividersColor = MaterialTheme.colorScheme.primary
            )
        }
        Card(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .wrapContentHeight()
                .padding(horizontal = 12.dp)
                .weight(1.1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        ) {
            ListItemPicker(
                modifier = Modifier.fillMaxWidth(),
                value = month,
                list = months,
                onValueChange = {
                    month = it
                    onEvent(SignUpEvent.OnBirthdayChanged(LocalDate.of(year, months.indexOf(month) + 1, day)))
                },
                textStyle = MaterialTheme.typography.displayMedium,
                dividersColor = MaterialTheme.colorScheme.primary
            )
        }
        Card(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .weight(1.2f)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            ListItemPicker(
                modifier = Modifier.fillMaxWidth(),
                value = year,
                list = (1900..2023).toList(),
                onValueChange = {
                    year = it
                    onEvent(SignUpEvent.OnBirthdayChanged(LocalDate.of(year, months.indexOf(month) + 1, day)))
                },
                textStyle = MaterialTheme.typography.displayMedium,
                dividersColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FieldsFIO(
    onEvent: (SignUpEvent) -> Unit,
    name: String,
    surname: String,
    patronymic: String
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterPatronymic = remember { FocusRequester() }

    Text(text = stringResource(R.string.your_fio), style = MaterialTheme.typography.titleMedium)
    TextInput(
        text = surname,
        onValueChanged = { onEvent(SignUpEvent.OnSurnameChanged(it)) },
        hint = stringResource(id = R.string.surname),
        modifier = Modifier.padding(top = 24.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusRequesterName.requestFocus() }),
        focusRequester = FocusRequester()
    )
    TextInput(
        text = name,
        onValueChanged = { onEvent(SignUpEvent.OnNameChanged(it)) },
        hint = stringResource(id = R.string.name),
        modifier = Modifier.padding(top = 24.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        focusRequester = focusRequesterName,
        keyboardActions = KeyboardActions(onNext = { focusRequesterPatronymic.requestFocus() }),
    )
    TextInput(
        text = patronymic,
        onValueChanged = { onEvent(SignUpEvent.OnPatronymicChanged(it)) },
        hint = stringResource(id = R.string.patronymic),
        modifier = Modifier.padding(top = 24.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        focusRequester = focusRequesterPatronymic,
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
    )
}


@Composable
fun TextInput(
    keyboardOptions: KeyboardOptions,
    text: String,
    onValueChanged: (String) -> Unit,
    hint: String,
    focusRequester: FocusRequester,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = text,
        label = { Text(text = hint, color = MaterialTheme.colorScheme.primaryContainer) },
        onValueChange = { onValueChanged(it) },
        trailingIcon = {
            if (text != "")
                IconButton(onClick = { onValueChanged("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }
        },
        singleLine = true,
        modifier = modifier.focusRequester(focusRequester),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    )
}

@Composable
fun DotsMenuSignUp(
    onNavigateBack: () -> Unit,
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        IconButton(onClick = onNavigateBack, modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.Center)
        ) {
            items(totalDots) { index ->
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(color = if (index == selectedIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                )
                Spacer(Modifier.width(if (index != totalDots - 1) 16.dp else 0.dp))
            }
        }
    }
}


@Composable
fun TextInput(
    keyboardOptions: KeyboardOptions,
    text: String,
    onValueChanged: (String) -> Unit,
    hint: String,
    focusRequester: FocusRequester = FocusRequester(),
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = text,
        label = {
            Text(
                text = hint,
                color = if (isError) errorLoginField else MaterialTheme.colorScheme.primaryContainer
            )
        },
        onValueChange = { onValueChanged(it) },
        trailingIcon = {
            if (text != "")
                IconButton(onClick = { onValueChanged("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }
        },
        singleLine = true,
        modifier = modifier.focusRequester(focusRequester),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isError = isError,
        visualTransformation = visualTransformation
    )
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    AesculapiusTheme {
        SignUpScreen(
            onChangeCurrentPage = {},
            userUiState = SignUpUiState(),
            currentPage = 1,
            onEvent = {},
            onClickSetReminder = {},
            onEndRegistration = {},
            onNavigateBack = {}
        )
    }
}