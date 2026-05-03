package com.example.aesculapius.ui.diary.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aesculapius.R
import com.example.aesculapius.ui.TopBar
import com.example.aesculapius.ui.diary.severityPalette
import com.example.aesculapius.ui.diary.severitySmileRes
import com.example.aesculapius.ui.navigation.NavigationDestination
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

object DiaryEntryScreen : NavigationDestination {
    override val route = "DiaryEntryScreen"
    const val depart = "date"
    val routeWithArgs = "$route/{$depart}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEntryScreen(
    modifier: Modifier = Modifier,
    viewModel: DiaryEntryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToSymptoms: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                onNavigateBack = onNavigateBack,
                text = if (state.isEdit)
                    stringResource(R.string.edit_diary_entry)
                else
                    stringResource(R.string.new_diary_entry)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 96.dp)
            ) {
                SectionTitle(stringResource(R.string.diary_section_when))
                DatePickerCard(
                    date = state.date,
                    onClick = { showDatePicker = true }
                )

                Spacer(Modifier.height(8.dp))

                SectionTitle(stringResource(R.string.diary_section_symptoms))
                if (state.symptoms.isEmpty()) {
                    EmptySymptomsCard(onNavigateToSymptoms = onNavigateToSymptoms)
                } else {
                    SymptomsCard(
                        state = state,
                        onSeverityChanged = { name, severity ->
                            viewModel.onEvent(DiaryEntryEvent.OnSeverityChanged(name, severity))
                        }
                    )
                }

                Spacer(Modifier.height(8.dp))

                SectionTitle(stringResource(R.string.diary_section_note))
                OutlinedTextField(
                    value = state.note,
                    onValueChange = { viewModel.onEvent(DiaryEntryEvent.OnNoteChanged(it)) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.diary_note_hint),
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            ActionRow(
                saveEnabled = state.symptoms.isNotEmpty(),
                onCancel = onNavigateBack,
                onSave = {
                    viewModel.onEvent(DiaryEntryEvent.OnSave)
                    onNavigateBack()
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    if (showDatePicker) {
        val initialMillis = state.date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            tonalElevation = 0.dp,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        viewModel.onEvent(DiaryEntryEvent.OnDateChanged(newDate))
                    }
                    showDatePicker = false
                }) {
                    Text(
                        text = stringResource(R.string.ok),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                selectedDayContentColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            DatePicker(
                state = datePickerState,
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        color = MaterialTheme.colorScheme.primary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 8.dp)
    )
}

@Composable
private fun DatePickerCard(date: LocalDate, onClick: () -> Unit) {
    val mon = russianMonths[date.monthValue - 1]
    val dow = russianDows[date.dayOfWeek.value - 1]
    val formatted = "${date.dayOfMonth} $mon, $dow"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = stringResource(R.string.diary_date_label),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            Text(
                text = formatted,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptySymptomsCard(onNavigateToSymptoms: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.diary_no_symptoms),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onNavigateToSymptoms) {
            Text(
                text = stringResource(R.string.go_to_my_symptoms),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
private fun SymptomsCard(
    state: DiaryEntryUiState,
    onSeverityChanged: (String, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(horizontal = 16.dp)
    ) {
        state.symptoms.forEachIndexed { index, symptom ->
            if (index > 0) {
                Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.background)
            }
            SymptomSeverityEditor(
                name = symptom.name,
                severity = state.severityByName[symptom.name] ?: 0,
                onSeverityChanged = { sev -> onSeverityChanged(symptom.name, sev) }
            )
        }
    }
}

@Composable
private fun SymptomSeverityEditor(
    name: String,
    severity: Int,
    onSeverityChanged: (Int) -> Unit
) {
    val labels = listOf(
        stringResource(R.string.severity_none),
        stringResource(R.string.severity_mild),
        stringResource(R.string.severity_moderate),
        stringResource(R.string.severity_severe)
    )
    val activePalette = severityPalette(severity)

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = severitySmileRes(severity)),
                contentDescription = null,
                tint = activePalette.face,
                modifier = Modifier.size(22.dp)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            labels.forEachIndexed { index, label ->
                val isSelected = index == severity
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = 1.dp,
                                    color = activePalette.border,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            } else Modifier
                        )
                        .background(
                            if (isSelected) activePalette.background
                            else MaterialTheme.colorScheme.background
                        )
                        .background(
                            if (isSelected) activePalette.background
                            else MaterialTheme.colorScheme.background
                        )
                        .clickable { onSeverityChanged(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = if (isSelected) activePalette.text else MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionRow(
    saveEnabled: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = MaterialTheme.colorScheme.background
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to Color.Transparent,
                        0.3f to bg,
                        1f to bg
                    )
                )
            )
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
        ) {
            Text(
                text = stringResource(R.string.cancel),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Button(
            onClick = onSave,
            enabled = saveEnabled,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Text(
                text = stringResource(R.string.save),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

private val russianMonths = arrayOf(
    "янв", "фев", "мар", "апр", "май", "июн",
    "июл", "авг", "сен", "окт", "ноя", "дек"
)
private val russianDows = arrayOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
