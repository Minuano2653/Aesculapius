package com.example.aesculapius.ui.medicines

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun CurrentMedicineTypeCard(
    isEnabled: Boolean,
    @DrawableRes medicineDrawableRes: Int,
    @StringRes medicineStringRes: Int,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .size(96.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        backgroundColor =
            if (isEnabled) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.errorContainer
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = medicineDrawableRes),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                colorFilter = ColorFilter.tint(
                    if (isEnabled) MaterialTheme.colorScheme.tertiaryContainer
                    else MaterialTheme.colorScheme.primary
                ),
                alignment = Alignment.TopCenter
            )
            Text(
                text = stringResource(medicineStringRes),
                color =
                    if (isEnabled) MaterialTheme.colorScheme.tertiaryContainer
                    else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}