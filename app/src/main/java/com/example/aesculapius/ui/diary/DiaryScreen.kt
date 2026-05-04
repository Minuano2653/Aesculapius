package com.example.aesculapius.ui.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aesculapius.R
import com.example.aesculapius.ui.navigation.NavigationDestination
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DiaryScreen : NavigationDestination {
    override val route = "DiaryScreen"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = hiltViewModel(),
    onNavigateToEntry: (String) -> Unit
) {
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.new_diary_entry)
                    )
                },
                onClick = { onNavigateToEntry("new") },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier
                    .padding(16.dp)
                    .offset(y = 12.dp)
            )
        }
    ) { innerPadding ->
        if (entries.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.diary_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
        } else {
            LazyColumn(
                state = lazyListState,
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries, key = { it.date.toString() }) { entry ->
                    DiaryCard(
                        entry = entry,
                        onEdit = { onNavigateToEntry(entry.date.toString()) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DiaryCard(
    entry: DiaryEntryItem,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val symptomMap = remember(entry.symptomsJson) { parseSymptoms(entry.symptomsJson) }
    val dateText = remember(entry.date, entry.createdAt) { formatHeader(entry) }

    androidx.compose.material.Card(
        elevation = 0.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_diary_entry),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            symptomMap.entries.forEachIndexed { index, (name, severity) ->
                if (index > 0) {
                    Divider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                SymptomSeverityRow(name = name, severity = severity)
            }
            if (entry.note.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = entry.note,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun SymptomSeverityRow(
    name: String,
    severity: Int
) {
    val activePalette = severityPalette(severity)
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
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
            severityLabels().forEachIndexed { index, label ->
                val isSelected = index == severity
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) activePalette.background else MaterialTheme.colorScheme.background),
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
private fun severityLabels(): List<String> = listOf(
    stringResource(R.string.severity_none),
    stringResource(R.string.severity_mild),
    stringResource(R.string.severity_moderate),
    stringResource(R.string.severity_severe)
)

private val russianMonths = arrayOf(
    "янв", "фев", "мар", "апр", "май", "июн",
    "июл", "авг", "сен", "окт", "ноя", "дек"
)
private val russianDows = arrayOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

private fun formatHeader(entry: DiaryEntryItem): String {
    val date = entry.date
    val dom = date.dayOfMonth
    val mon = russianMonths[date.monthValue - 1]
    val dow = russianDows[date.dayOfWeek.value - 1]
    val time = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(entry.createdAt),
        ZoneId.systemDefault()
    ).format(timeFormatter)
    return "$dom $mon · $dow · $time"
}

private fun parseSymptoms(json: String): Map<String, Int> {
    if (json.isBlank()) return emptyMap()
    val obj = JSONObject(json)
    val result = linkedMapOf<String, Int>()
    val keys = obj.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        result[key] = obj.getInt(key)
    }
    return result
}
