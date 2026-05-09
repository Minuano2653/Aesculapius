package com.example.aesculapius.ui.navigation

import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.example.aesculapius.ui.medicines.EditMedicineScreen
import com.example.aesculapius.ui.therapy.MedicineCard
import com.example.aesculapius.ui.medicines.NewMedicineScreen
import com.example.aesculapius.ui.therapy.TherapyScreen
import com.example.aesculapius.ui.therapy.TherapyViewModel
import java.time.LocalDate

fun NavGraphBuilder.therapyNavGraph(
    onClickMedicine: (MedicineCard) -> Unit,
    therapyViewModel: TherapyViewModel,
    navController: NavHostController
) {
    composable(route = TherapyScreen.route) {
        val currentLoadingState by therapyViewModel.currentLoadingState.collectAsState()
        val generalLoadingState by therapyViewModel.generalLoadingState.collectAsState()
        val currentWeekDates by therapyViewModel.currentWeekDates.collectAsState()
        val isWeek by therapyViewModel.isWeek.collectAsState()

        TherapyScreen(
            isWeek = isWeek,
            onNavigate = navController::navigate,
            getAmountNotAcceptedMedicines = therapyViewModel::getAmountNotAcceptedMedicines,
            therapyEvent = therapyViewModel::onTherapyEvent,
            generalLoadingState = generalLoadingState,
            currentLoadingState = currentLoadingState,
            currentWeekDates = currentWeekDates,
            onClickMedicine = { onClickMedicine(it) },
            updateCurrentDate = { therapyViewModel.updateCurrentDate(it) },
            isAfterCurrentDate = therapyViewModel.getCurrentDate().isAfter(LocalDate.now()),
            currentDate = therapyViewModel.getCurrentDate(),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}