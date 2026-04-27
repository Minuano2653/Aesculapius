package com.example.aesculapius.ui.airquality.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aesculapius.R
import com.example.aesculapius.domain.airquality.model.AqiLevel
import com.example.aesculapius.domain.airquality.model.Components
import com.example.aesculapius.domain.airquality.model.Pollutant

@Composable
fun AqiComponentsGrid(
    components: Components,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Pollutant.CO to components.co,
        Pollutant.NO2 to components.no2,
        Pollutant.O3 to components.o3,
        Pollutant.SO2 to components.so2,
        Pollutant.PM25 to components.pm2_5,
        Pollutant.PM10 to components.pm10
    )
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.aqi_components_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSecondary
        )
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { (pollutant, value) ->
                    ComponentCell(
                        pollutant = pollutant,
                        value = value,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ComponentCell(
    pollutant: Pollutant,
    value: Double,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val level = pollutant.getLevel(value)
    val levelColor = Color(level.colorArgb)

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { showDialog = true },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(pollutant.nameRes),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = String.format("%.2f", value),
                style = MaterialTheme.typography.titleMedium,
                color = levelColor,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = stringResource(level.labelRes),
                style = MaterialTheme.typography.bodySmall,
                color = levelColor,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }

    if (showDialog) {
        PollutantInfoDialog(
            pollutant = pollutant,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun PollutantInfoDialog(
    modifier: Modifier = Modifier,
    pollutant: Pollutant,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(pollutant.nameRes),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(pollutant.descRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Divider(color = MaterialTheme.colorScheme.secondary)
                pollutant.scaleRanges.forEach { (level, range) ->
                    ScaleRow(level = level, range = range)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.ok))
            }
        }
    )
}

@Composable
private fun ScaleRow(level: AqiLevel, range: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(Color(level.colorArgb))
        )
        Text(
            text = stringResource(level.labelRes),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = range,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primaryContainer
        )
    }
}
