package com.example.aesculapius.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.aesculapius.ui.diary.DiaryScreen
import com.example.aesculapius.ui.diary.entry.DiaryEntryScreen
import com.example.aesculapius.ui.symptoms.SymptomsScreen

fun NavGraphBuilder.diaryNavGraph(
    navController: NavHostController,
    turnOnBars: () -> Unit,
    turnOffBars: () -> Unit
) {
    composable(route = DiaryScreen.route) {
        DiaryScreen(
            onNavigateToEntry = { dateArg ->
                navController.navigate("${DiaryEntryScreen.route}/$dateArg")
            }
        )
        turnOnBars()
    }

    composable(
        route = DiaryEntryScreen.routeWithArgs,
        arguments = listOf(navArgument(DiaryEntryScreen.depart) { type = NavType.StringType })
    ) {
        DiaryEntryScreen(
            onNavigateBack = { navController.navigateUp() },
            onNavigateToSymptoms = { navController.navigate(SymptomsScreen.route) }
        )
        turnOffBars()
    }
}
