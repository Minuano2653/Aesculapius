package com.example.aesculapius.ui.doctor.patients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ScoreBadge(
    mainScore: Double?,
    modifier: Modifier = Modifier
) {
    val color = when {
        mainScore == null -> Color(0xFFBDBDBD)
        mainScore < 5.0 -> Color(0xFFE53935)
        mainScore < 7.5 -> Color(0xFFFFB300)
        else -> Color(0xFF43A047)
    }
    Box(
        modifier = modifier
            .size(48.dp)
            .background(color = color, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = mainScore?.let { String.format("%.1f", it) } ?: "—",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}
