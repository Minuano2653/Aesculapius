package com.example.aesculapius.ui.medicines

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aesculapius.R
import com.example.aesculapius.data.CurrentMedicineType
import com.example.aesculapius.data.medicinesAerosol
import com.example.aesculapius.data.medicinesPowder
import com.example.aesculapius.data.medicinesTablets
import com.example.aesculapius.ui.TopBar
import com.example.aesculapius.ui.navigation.NavigationDestination
import com.example.aesculapius.ui.theme.AesculapiusTheme
import com.example.aesculapius.ui.therapy.Medicine
import com.example.aesculapius.ui.therapy.TherapyEvent
import java.time.LocalDate

object NewMedicineScreen : NavigationDestination {
    override val route = "NewMedicineScreen"
}

@Composable
fun NewMedicineScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    medicinesListViewModel: MedicinesListViewModel = hiltViewModel<MedicinesListViewModel>()
) {
    lateinit var currentMedicineItem: Medicine
    val context = LocalContext.current

    var currentMedicineType by remember { mutableStateOf(CurrentMedicineType.Aerosol) }
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    var selectedDosesIndex by remember { mutableIntStateOf(0) }
    var selectedFrequencyIndex by remember { mutableIntStateOf(0) }
    var isFrequencyChoosen by remember { mutableStateOf(true) }
    var isDoseChoosen by remember { mutableStateOf(true) }

    Scaffold(topBar = {
        TopBar(onNavigateBack = { onNavigateBack() }, text = stringResource(R.string.new_medicine))
    }) { paddingValues ->
        Column(
            modifier = modifier.padding(
                top = paddingValues.calculateTopPadding(),
                start = 24.dp,
                end = 24.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.padding(top = 24.dp)) {
                CurrentMedicineTypeCard(
                    isEnabled = currentMedicineType == CurrentMedicineType.Aerosol,
                    medicineDrawableRes = R.drawable.aerosol_icon,
                    medicineStringRes = R.string.aerosol,
                    onClick = {
                        if (currentMedicineType != CurrentMedicineType.Aerosol) {
                            currentMedicineType = CurrentMedicineType.Aerosol
                            isDoseChoosen = true
                            isFrequencyChoosen = true
                        }
                    },
                )
                Spacer(Modifier.weight(1f))
                CurrentMedicineTypeCard(
                    isEnabled = currentMedicineType == CurrentMedicineType.Powder,
                    medicineDrawableRes = R.drawable.powder_icon,
                    medicineStringRes = R.string.powder,
                    onClick = {
                        if (currentMedicineType != CurrentMedicineType.Powder) {
                            currentMedicineType = CurrentMedicineType.Powder
                            isDoseChoosen = true
                            isFrequencyChoosen = true
                        }
                    },
                )
                Spacer(Modifier.weight(1f))
                CurrentMedicineTypeCard(
                    isEnabled = currentMedicineType == CurrentMedicineType.Tablets,
                    medicineDrawableRes = R.drawable.tablets_icon,
                    medicineStringRes = R.string.tablets,
                    onClick = {
                        if (currentMedicineType != CurrentMedicineType.Tablets) {
                            currentMedicineType = CurrentMedicineType.Tablets
                            isDoseChoosen = true
                            isFrequencyChoosen = true
                        }
                    },
                )
            }

            val medicines = when (currentMedicineType) {
                CurrentMedicineType.Aerosol -> medicinesAerosol
                CurrentMedicineType.Powder -> medicinesPowder
                CurrentMedicineType.Tablets -> medicinesTablets
            }
            MedicineSelectionContent(
                medicines = medicines,
                selectedItemIndex = selectedItemIndex,
                onItemSelected = { index ->
                    selectedItemIndex = index
                    isDoseChoosen = false
                    isFrequencyChoosen = false
                },
                onDoseSelected = { index ->
                    selectedDosesIndex = index
                    isDoseChoosen = true
                },
                onFrequencySelected = { index ->
                    selectedFrequencyIndex = index
                    isFrequencyChoosen = true
                },
                isDoseChoosen = isDoseChoosen,
                isFrequencyChoosen = isFrequencyChoosen,
                onMedicineAssigned = { currentMedicineItem = it }
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    if (!isFrequencyChoosen || !isDoseChoosen)
                        Toast.makeText(
                            context,
                            context.getString(R.string.choose_dose_frequency),
                            Toast.LENGTH_SHORT
                        ).show()
                    else {
                        medicinesListViewModel.onEvent(
                            MedicineEvent.OnAddMedicineItem(
                                currentMedicineType,
                                currentMedicineItem.name,
                                currentMedicineItem.undername,
                                currentMedicineItem.doses[selectedDosesIndex],
                                currentMedicineItem.frequency[selectedFrequencyIndex],
                                LocalDate.now(),
                                LocalDate.now().plusMonths(1)
                            )
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .height(56.dp)
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
        }
    }
}

@Composable
private fun MedicineSelectionContent(
    medicines: List<Medicine>,
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit,
    onDoseSelected: (Int) -> Unit,
    onFrequencySelected: (Int) -> Unit,
    isDoseChoosen: Boolean,
    isFrequencyChoosen: Boolean,
    onMedicineAssigned: (Medicine) -> Unit
) {
    DropdownMenu(
        menuName = stringResource(R.string.naming),
        menuList = List(medicines.size) { medicines[it].name },
        onCloseAction = onItemSelected,
        modifier = Modifier.padding(top = 48.dp),
    )
    DropdownMenu(
        menuName = stringResource(R.string.dose),
        menuList = medicines[selectedItemIndex].doses,
        onCloseAction = onDoseSelected,
        modifier = Modifier.padding(top = 40.dp),
        isChoosen = isDoseChoosen
    )
    DropdownMenu(
        menuName = stringResource(R.string.frequency),
        menuList = medicines[selectedItemIndex].frequency,
        onCloseAction = onFrequencySelected,
        modifier = Modifier.padding(top = 40.dp),
        isChoosen = isFrequencyChoosen
    )
    onMedicineAssigned(medicines[selectedItemIndex])
}

@Preview(showBackground = true)
@Composable
fun NewMedicineScreenPreview() {
    AesculapiusTheme {
        NewMedicineScreen(onNavigateBack = {})
    }
}