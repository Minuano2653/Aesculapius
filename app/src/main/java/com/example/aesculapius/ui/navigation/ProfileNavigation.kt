package com.example.aesculapius.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.aesculapius.R
import com.example.aesculapius.data.Hours
import com.example.aesculapius.ui.profile.EditProfileScreen
import com.example.aesculapius.ui.profile.LearnItemScreen
import com.example.aesculapius.ui.profile.LearnScreen
import com.example.aesculapius.ui.profile.ProfileEvent
import com.example.aesculapius.ui.profile.ProfileScreen
import com.example.aesculapius.ui.profile.SetReminderTimeProfile
import com.example.aesculapius.ui.signup.SetReminderTime
import com.example.aesculapius.ui.signup.SetReminderTimeScreen
import com.example.aesculapius.ui.signup.SignUpUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun NavGraphBuilder.profileNavGraph(
    userUiState: SignUpUiState,
    turnOffBars: () -> Unit,
    turnOnBars: () -> Unit,
    onProfileEvent: (ProfileEvent) -> Unit,
    navController: NavHostController,
    getTestsScore: suspend () -> Pair<Double, Double>,
    getMedicinesScore: suspend () -> Double
) {
    composable(route = ProfileScreen.route) {
        ProfileScreen(
            modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
            onNavigate = navController::navigate,
            userRegisterDate = userUiState.userRegisterDate,
            getMedicinesScore = getMedicinesScore,
            getTestsScore = getTestsScore
        )
        turnOnBars()
    }
    composable(route = SetReminderTimeProfile.route) {
        SetReminderTimeProfile(
            morningTime = userUiState.morningReminder,
            eveningTime = userUiState.eveningReminder,
            onNavigateBack = { navController.navigateUp() },
            onClickSetReminder = { navController.navigate("${SetReminderTime.route}/${it}") },
        )
        turnOffBars()
    }
    composable(route = EditProfileScreen.route) {
        EditProfileScreen(
            onNavigateBack = { navController.navigateUp() },
            user = userUiState,
            onSaveNewUser = { onProfileEvent(ProfileEvent.OnSaveNewUser(it)) }
        )
        turnOffBars()
    }
    composable(
        route = SetReminderTime.routeWithArgs,
        arguments = listOf(navArgument(name = SetReminderTime.depart) {
            type = NavType.StringType
        })
    ) { backStackEntry ->
        val context = LocalContext.current
        val arg = Hours.valueOf(
            backStackEntry.arguments?.getString(SetReminderTime.depart) ?: context.getString(R.string.morning)
        )
        when (arg) {
            Hours.Morning -> SetReminderTimeScreen(
                title = context.getString(R.string.morning_reminder),
                textHours = userUiState.morningReminder.format(DateTimeFormatter.ofPattern("HH")),
                textMinutes = userUiState.morningReminder.format(DateTimeFormatter.ofPattern("mm")),
                onDoneButton = {
                    if (userUiState.eveningReminder.hour - it.hour < 8)
                        Toast.makeText(context, context.getString(R.string.reminder_warning), Toast.LENGTH_LONG).show()
                    else {
                        onProfileEvent(ProfileEvent.OnSaveMorningTime(it))
                        navController.navigateUp()
                    }
                },
                onNavigateBack = { navController.navigateUp() },
                textTopBar = context.getString(R.string.set_reminders)
            )

            Hours.Evening -> SetReminderTimeScreen(
                title = context.getString(R.string.evening_reminder),
                textHours = userUiState.eveningReminder.format(DateTimeFormatter.ofPattern("HH")),
                textMinutes = userUiState.eveningReminder.format(DateTimeFormatter.ofPattern("mm")),
                onDoneButton = {
                    if (it.hour - userUiState.morningReminder.hour < 8)
                        Toast.makeText(context, context.getString(R.string.reminder_warning), Toast.LENGTH_LONG).show()
                    else {
                        onProfileEvent(ProfileEvent.OnSaveEveningTime(it))
                        navController.navigateUp()
                    }
                },
                onNavigateBack = { navController.navigateUp() },
                textTopBar = context.getString(R.string.set_reminders)
            )
        }
    }
    composable(route = LearnScreen.route) {
        LearnScreen(
            onNavigateBack = { navController.navigateUp() },
            onClickItem = { name, text -> navController.navigate("${LearnItemScreen.route}/$name^$text") }
        )
        turnOffBars()
    }
    composable(
        route = LearnItemScreen.routeWithArgs,
        arguments = listOf(navArgument(name = LearnItemScreen.depart) {
            type = NavType.StringType
        })
    ) { backStackEntry ->
        val arg = (backStackEntry.arguments?.getString(LearnItemScreen.depart)?.split('^') ?: listOf()).map { it.toInt() }
        LearnItemScreen(
            onNavigateBack = { navController.navigateUp() },
            name = stringResource(id = arg[0]),
            text = stringResource(id = arg[1])
        )
        turnOffBars()
    }
}