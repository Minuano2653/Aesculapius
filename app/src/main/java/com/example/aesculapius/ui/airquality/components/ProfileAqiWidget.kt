package com.example.aesculapius.ui.airquality.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aesculapius.R
import com.example.aesculapius.domain.airquality.model.AirQualityCache
import com.example.aesculapius.domain.airquality.model.AqiLevel
import com.example.aesculapius.ui.profile.SingleItem

@Composable
fun ProfileAqiWidget(
    state: AirQualityCache?,
    onNavigateToPicker: () -> Unit,
    onNavigateToAirQuality: () -> Unit,
    modifier: Modifier = Modifier
) {
    val location = state?.location
    if (location == null) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
                .fillMaxWidth()
                .clickable { onNavigateToPicker() },
            elevation = 0.dp
        ) {
            SingleItem(
                image = R.drawable.air_quality_icon,
                name = stringResource(id = R.string.air_quality_index),
                onClick = onNavigateToPicker
            )
        }
        return
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onNavigateToAirQuality() },
        elevation = 0.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.air_quality_index),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = location.name.ifBlank { stringResource(R.string.aqi_unknown_location) },
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(Modifier.size(12.dp))
            val aqi = state.airQuality
            if (aqi != null) {
                val level = AqiLevel.fromAqi(aqi.aqi)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = Color(level.colorArgb), shape = CircleShape)
                ) {
                    Text(
                        text = aqi.aqi.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp
                    )
                }
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = CircleShape
                        )
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(R.drawable.air_quality_icon),
                        contentDescription = null
                    )
                }
            }
        }
    }
}
