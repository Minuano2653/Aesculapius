package com.example.aesculapius.ui.therapy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.aesculapius.R
import com.example.aesculapius.ui.theme.tertiaryContainer

@Composable
fun EditMedicineSheet(
    acceptMedicine: (Int) -> Unit,
    skipMedicine: (Int) -> Unit,
    medicine: MedicineCard,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .navigationBarsPadding()
        .padding(24.dp)) {
        Row {
            Column {
                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = medicine.undername,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                Text(
                    text = medicine.dose,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
        Row(modifier = Modifier.padding(top = 16.dp)) {
            if ("вечером" in medicine.frequency)
                Icon(
                    painter = painterResource(id = R.drawable.moon_icon),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp, bottom = 12.dp),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            else
                Icon(
                    painter = painterResource(id = R.drawable.sun_icon),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp, bottom = 12.dp),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            Text(
                text = medicine.frequency,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.secondary
        )
        Row(modifier = Modifier.align(Alignment.End)) {
            TextButton(
                onClick = { skipMedicine(medicine.doseId) },
                modifier = Modifier
                    .widthIn(min = 106.dp)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.skip),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                onClick = { acceptMedicine(medicine.doseId) },
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                modifier = Modifier.widthIn(min = 106.dp)
            ) {
                Text(
                    text = stringResource(R.string.accept),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}
