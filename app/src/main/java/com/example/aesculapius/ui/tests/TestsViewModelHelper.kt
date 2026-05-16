package com.example.aesculapius.ui.tests

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

/**
 * Возвращает [TestsViewModel], привязанный к back stack entry [TestsScreen].
 * Это позволяет шарить один и тот же экземпляр между экранами тестов
 * (TestScreen, MetricsTestScreen, AstTestResult, RecommendationsTestResult),
 * чтобы summaryScore, выставленный при сабмите теста, был виден на result-экране.
 */
@Composable
fun rememberTestsViewModel(navController: NavHostController): TestsViewModel {
    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(TestsScreen.route)
    }
    return hiltViewModel(parentEntry)
}
