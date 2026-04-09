package com.example.aesculapius.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aesculapius.R
import com.example.aesculapius.data.Hours
import com.example.aesculapius.ui.login.LoginScreen
import com.example.aesculapius.ui.profile.ProfileEvent
import com.example.aesculapius.ui.signup.SetReminderTime
import com.example.aesculapius.ui.signup.SetReminderTimeScreen
import com.example.aesculapius.ui.signup.SignUpEvent
import com.example.aesculapius.ui.signup.SignUpScreen
import com.example.aesculapius.ui.signup.SignUpUiState
import com.example.aesculapius.ui.signup.SignUpViewModel
import java.time.format.DateTimeFormatter

@Composable
fun SignUpNavigation(
    onProfileEvent: (ProfileEvent) -> Unit,
    navController: NavHostController = rememberNavController(),
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val signUpUiState by signUpViewModel.uiStateSingUp.collectAsState()
    var currentPage by remember { mutableIntStateOf(0) }

    NavHost(
        navController = navController,
        startDestination = LoginScreen.route,
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        composable(LoginScreen.route) {
            LoginScreen(
                onEndLogin = { onProfileEvent(ProfileEvent.OnLoginUser(it)) },
                navigate = navController::navigate
            )
        }
        composable(
            route = SetReminderTime.routeWithArgs,
            arguments = listOf(navArgument(name = SetReminderTime.depart) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val arg = Hours.valueOf(
                backStackEntry.arguments?.getString(SetReminderTime.depart) ?: Hours.Morning.name
            )
            when (arg) {
                Hours.Morning -> SetReminderTimeScreen(
                    title = stringResource(id = R.string.morning_reminder),
                    textHours = signUpUiState.morningReminder.format(DateTimeFormatter.ofPattern("HH")),
                    textMinutes = signUpUiState.morningReminder.format(DateTimeFormatter.ofPattern("mm")),
                    onDoneButton = {
                        signUpViewModel.onEvent(SignUpEvent.OnMorningReminderChanged(it))
                        navController.navigateUp()
                    },
                    onNavigateBack = { navController.navigateUp() },
                    textTopBar = stringResource(R.string.reminder_time)
                )

                Hours.Evening -> SetReminderTimeScreen(
                    title = stringResource(id = R.string.evening_reminder),
                    textHours = signUpUiState.eveningReminder.format(DateTimeFormatter.ofPattern("HH")),
                    textMinutes = signUpUiState.eveningReminder.format(DateTimeFormatter.ofPattern("mm")),
                    onDoneButton = {
                        signUpViewModel.onEvent(SignUpEvent.OnEveningReminderChanged(it))
                        navController.navigateUp()
                    },
                    onNavigateBack = { navController.navigateUp() },
                    textTopBar = stringResource(R.string.reminder_time)
                )
            }
        }
        composable(route = SignUpScreen.route) {
            SignUpScreen(
                userUiState = signUpUiState,
                currentPage = currentPage,
                onChangeCurrentPage = { currentPage++ },
                onEvent = signUpViewModel::onEvent,
                onEndRegistration = {
                    onProfileEvent(ProfileEvent.OnSaveNewUser(signUpUiState.copy(id = it)))
                },
                onClickSetReminder = { navController.navigate("${SetReminderTime.route}/${it}") },
                onNavigateBack = navController::navigateUp
            )
        }
    }
}