package com.example.aesculapius.ui.medicines

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavOptionsBuilder
import com.example.aesculapius.R
import com.example.aesculapius.data.CurrentMedicineType
import com.example.aesculapius.data.medicinesAerosol
import com.example.aesculapius.data.medicinesPowder
import com.example.aesculapius.data.medicinesTablets
import com.example.aesculapius.ui.TopBar
import com.example.aesculapius.ui.navigation.NavigationDestination
import com.example.aesculapius.ui.theme.AesculapiusTheme
import com.example.aesculapius.ui.therapy.Medicine
import com.example.aesculapius.ui.therapy.MedicineCard
import com.example.aesculapius.ui.therapy.TherapyEvent
import com.example.aesculapius.ui.therapy.TherapyScreen
import java.time.LocalDate

object EditMedicineScreen : NavigationDestination {
    override val route = "EditMedicineScreen"
}

@Composable
fun EditMedicineScreen(
    modifier: Modifier = Modifier,
    medicinesListViewModel: MedicinesListViewModel = hiltViewModel<MedicinesListViewModel>(),
    onNavigateBack: () -> Unit,
    medicine: MedicineCard
) {
    val context = LocalContext.current
    val currentMedicine: MutableState<Medicine?> = remember { mutableStateOf(null) }
    var selectedDosesIndex by remember {
        mutableIntStateOf(currentMedicine.value?.doses?.indexOf(medicine.dose) ?: 0)
    }
    var selectedFrequencyIndex by remember {
        mutableIntStateOf(currentMedicine.value?.frequency?.indexOf(medicine.fullFrequency) ?: 0)
    }

    LaunchedEffect(key1 = Unit) {
        when (medicine.medicineType) {
            CurrentMedicineType.Aerosol -> {
                medicinesAerosol.forEach { medicineAerosol ->
                    if (medicineAerosol.name == medicine.name)
                        currentMedicine.value = medicineAerosol
                }
            }

            CurrentMedicineType.Powder -> {
                medicinesPowder.forEach { medicinePowder ->
                    if (medicinePowder.name == medicine.name)
                        currentMedicine.value = medicinePowder
                }
            }

            CurrentMedicineType.Tablets -> {
                medicinesTablets.forEach { medicinesTablet ->
                    if (medicinesTablet.name == medicine.name)
                        currentMedicine.value = medicinesTablet
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                onNavigateBack = onNavigateBack,
                text = stringResource(R.string.edit_medicine),
                existHelpButton = false
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier.padding(
                top = paddingValues.calculateTopPadding() + 32.dp,
                start = 24.dp,
                end = 24.dp
            ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = medicine.name,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )
            Text(
                text = "${if (medicine.medicineType == CurrentMedicineType.Tablets) "таблетки" else if (medicine.medicineType == CurrentMedicineType.Powder) "порошок" else "аэрозоль"}, ${medicine.undername}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            DropdownMenu(
                menuName = stringResource(R.string.dose),
                menuList = currentMedicine.value?.doses ?: listOf(medicine.dose),
                onCloseAction = { index -> selectedDosesIndex = index },
                initialIndex = selectedDosesIndex,
                modifier = Modifier.padding(top = 32.dp, bottom = 34.dp),
            )
            DropdownMenu(
                menuName = stringResource(R.string.frequency),
                menuList = currentMedicine.value?.frequency ?: listOf(medicine.fullFrequency),
                onCloseAction = { index -> selectedFrequencyIndex = index },
                initialIndex = selectedFrequencyIndex,
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    medicinesListViewModel.onEvent(
                        MedicineEvent.OnUpdateMedicineItem(
                            medicineId = medicine.id,
                            frequency = currentMedicine.value?.frequency?.get(selectedFrequencyIndex)
                                ?: medicine.frequency,
                            dose = currentMedicine.value?.doses?.get(selectedDosesIndex)
                                ?: medicine.dose,
                            medicineType = medicine.medicineType,
                            startDate = medicine.startDate,
                            endDate = medicine.endDate
                        )
                    )
                    onNavigateBack()
                    Toast.makeText(
                        context,
                        context.getString(R.string.success_save_medicine),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .padding(bottom = 17.dp)
                    .heightIn(min = 56.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.save),
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center
                )
            }
            TextButton(
                onClick = {
                    medicinesListViewModel.onEvent(MedicineEvent.OnDeleteMedicineItem(medicine.id))
                    onNavigateBack()
                    Toast.makeText(
                        context,
                        context.getString(R.string.success_delete_medicine),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier.padding(bottom = 48.dp)
            ) {
                Text(
                    text = stringResource(R.string.delete_medicine),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewEditMedicineScreen() {
    AesculapiusTheme {
        EditMedicineScreen(
            onNavigateBack = {},
            medicine = MedicineCard(
                id = 12,
                dose = "12мг/доза",
                doseId = 13,
                frequency = "2 раза в день",
                fullFrequency = "3 раза в день",
                isSkipped = false,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                isAccepted = false,
                undername = "что-то там",
                name = "препарат",
                medicineType = CurrentMedicineType.Aerosol
            )
        )
    }
}