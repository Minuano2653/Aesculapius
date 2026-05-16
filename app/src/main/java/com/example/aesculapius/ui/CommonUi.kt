package com.example.aesculapius.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.aesculapius.ui.home.TopBar

/** [TopBar] для второстепенных экранов с обработкой нажатия на кнопку "назад" */
@Composable
fun TopBar(
    onNavigateBack: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    existHelpButton: Boolean = false,
    onClickHelpButton: () -> Unit = {},
    rightIcon: Painter? = null,
    rightIconVector: ImageVector? = null,
    onClickRightIcon: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
            //.padding(top = 20.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(Modifier.weight(1f))
        if (existHelpButton)
            IconButton(
                onClick = { onClickHelpButton() },
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
        if (rightIcon != null)
            IconButton(
                onClick = { onClickRightIcon() },
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Icon(
                    painter = rightIcon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
        else if (rightIconVector != null)
            IconButton(
                onClick = { onClickRightIcon() },
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Icon(
                    imageVector = rightIconVector,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
    }
}