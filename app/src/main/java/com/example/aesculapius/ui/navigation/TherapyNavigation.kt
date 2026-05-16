package com.example.aesculapius.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.aesculapius.ui.therapy.TherapyScreen

fun NavGraphBuilder.therapyNavGraph(navController: NavHostController) {
    composable(route = TherapyScreen.route) {
        TherapyScreen(
            onNavigate = navController::navigate,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
