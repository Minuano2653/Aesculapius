package com.example.aesculapius.ui.doctor.patientdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aesculapius.domain.patients.model.ActivitySnapshot
import com.example.aesculapius.domain.patients.model.PatientFull
import com.example.aesculapius.ui.doctor.patients.ScoreBadge
import com.example.aesculapius.ui.medicines.MedicineManagementCard
import com.example.aesculapius.ui.signup.SignUpUiState
import com.example.aesculapius.ui.statistics.components.StatisticsChartCard
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@Composable
fun PatientDetailScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    detailViewModel: PatientDetailViewModel = hiltViewModel(),
    statsViewModel: PatientStatsViewModel = hiltViewModel()
) {
    val patient by detailViewModel.patient.collectAsState()
    val medicines by detailViewModel.medicines.collectAsState()
    val notes by detailViewModel.notes.collectAsState()
    val isLoading by detailViewModel.isLoading.collectAsState()

    val statsState by statsViewModel.uiState.collectAsState()
    val chartLine by statsViewModel.chartEntryModelLine.collectAsState()
    val chartColumn by statsViewModel.chartEntryModelColumn.collectAsState()
    val datesLine by statsViewModel.datesForLineChart.collectAsState()
    val datesColumn by statsViewModel.datesForColumnChart.collectAsState()

    Scaffold(
        topBar = {
            DetailTopBar(
                title = patient?.fullName.orEmpty(),
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (isLoading && patient == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                PatientHeader(patient = patient)
                Spacer(Modifier.height(16.dp))

                ActivityBreakdownCard(activity = patient?.activity)
                Spacer(Modifier.height(16.dp))

                val pseudoUserUiState = remember(patient) {
                    SignUpUiState(
                        birthday = patient?.birthday ?: LocalDate.now(),
                        height = (patient?.height?.toInt() ?: 0).toString()
                    )
                }
                StatisticsChartCard(
                    state = statsState,
                    chartEntryModelLine = chartLine,
                    chartEntryModelColumn = chartColumn,
                    datesForLineChart = datesLine,
                    datesForColumnChart = datesColumn,
                    userUiState = pseudoUserUiState,
                    convertToRussian = statsViewModel::convertToRussian,
                    onEvent = statsViewModel::onEvent
                )

                Spacer(Modifier.height(24.dp))
                MedicinesBlock(medicines = medicines)

                Spacer(Modifier.height(24.dp))
                NotesBlock(
                    notes = notes,
                    onAddNote = detailViewModel::addNote,
                    onDeleteNote = detailViewModel::deleteNote
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DetailTopBar(title: String, onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }
        Text(
            text = title.ifBlank { "Пациент" },
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun PatientHeader(patient: PatientFull?) {
    if (patient == null) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = patient.fullName.ifBlank { "Без имени" },
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            val ageText = patient.birthday?.let { computeAge(it).toString() + " лет" } ?: "—"
            Text(
                text = "Возраст: $ageText",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )
            Text(
                text = "Рост: ${patient.height.toInt()} см · Вес: ${patient.weight.toInt()} кг",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Composable
private fun ActivityBreakdownCard(activity: ActivitySnapshot?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Активность пациента",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = activity?.let { "Общий показатель из 10" } ?: "Недостаточно данных",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
                ScoreBadge(mainScore = activity?.mainScore)
            }
            if (activity != null) {
                Spacer(Modifier.height(16.dp))
                ScoreRow("АСТ-тест", activity.astTestScore)
                ScoreRow("Пикфлоуметрия", activity.metricsScore)
                ScoreRow("Приём препаратов", activity.medicinesScore)
            }
        }
    }
}

@Composable
private fun ScoreRow(label: String, score: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = when {
                        score >= 0.75 -> Color(0xFF43A047)
                        score >= 0.4 -> Color(0xFFFFB300)
                        else -> Color(0xFFE53935)
                    },
                    shape = CircleShape
                )
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = String.format("%.0f%%", score * 100),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Composable
private fun MedicinesBlock(medicines: List<com.example.aesculapius.ui.therapy.MedicineItem>) {
    Text(
        text = "Препараты",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    if (medicines.isEmpty()) {
        Text(
            text = "У пациента нет назначенных препаратов",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primaryContainer
        )
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            medicines.forEach { item ->
                MedicineManagementCard(
                    medicineItem = item,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun NotesBlock(
    notes: List<com.example.aesculapius.domain.doctornotes.model.DoctorNote>,
    onAddNote: (String) -> Unit,
    onDeleteNote: (String) -> Unit
) {
    var draft by remember { mutableStateOf("") }

    Text(
        text = "Заметки врача",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    OutlinedTextField(
        value = draft,
        onValueChange = { draft = it },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Новая заметка") },
        minLines = 3,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary
        )
    )
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = {
                if (draft.isNotBlank()) {
                    onAddNote(draft)
                    draft = ""
                }
            },
            enabled = draft.isNotBlank(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Сохранить", style = MaterialTheme.typography.headlineSmall)
        }
    }
    Spacer(Modifier.height(8.dp))

    if (notes.isEmpty()) {
        Text(
            text = "Заметок пока нет",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primaryContainer
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightForNotes(notes.size)
        ) {
            items(notes, key = { it.id }) { note ->
                NoteCard(note = note, onDelete = { onDeleteNote(note.id) })
            }
        }
    }
}

private fun Modifier.heightForNotes(count: Int): Modifier =
    this.then(Modifier.height((100 * count.coerceAtMost(8)).dp))

@Composable
private fun NoteCard(
    note: com.example.aesculapius.domain.doctornotes.model.DoctorNote,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (note.createdAtMillis > 0) {
                    Text(
                        text = formatTimestamp(note.createdAtMillis),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    text = note.text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    }
}

private fun formatTimestamp(millis: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru"))
    return formatter.format(Date(millis))
}

private fun computeAge(birthday: LocalDate): Int =
    java.time.Period.between(birthday, LocalDate.now()).years
