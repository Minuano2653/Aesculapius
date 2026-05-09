package com.example.aesculapius.ui.statistics.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.aesculapius.R

@Composable
fun DownloadReportFab(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { if (enabled) onClick() },
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.size(width = 312.dp, height = 56.dp)
    ) {
        Text(
            text = stringResource(id = R.string.download_statistics),
            style = MaterialTheme.typography.displaySmall
        )
    }
}
