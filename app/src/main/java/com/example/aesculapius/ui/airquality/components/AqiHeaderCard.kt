package com.example.aesculapius.ui.airquality.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aesculapius.domain.airquality.model.AqiLevel

@Composable
fun AqiHeaderCard(
    aqi: Int,
    locationName: String,
    modifier: Modifier = Modifier
) {
    val level = AqiLevel.fromAqi(aqi)
    val accent = Color(level.colorArgb)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(accent.copy(alpha = 0.15f))
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = locationName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primaryContainer
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = aqi.toString(),
                color = accent,
                fontWeight = FontWeight.W600,
                fontSize = 72.sp
            )
            Text(
                text = stringResource(id = level.labelRes),
                style = MaterialTheme.typography.titleMedium,
                color = accent,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Text(
            text = stringResource(id = level.descriptionRes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}
