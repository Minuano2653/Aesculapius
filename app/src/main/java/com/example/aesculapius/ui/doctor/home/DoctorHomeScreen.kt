package com.example.aesculapius.ui.doctor.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aesculapius.data.doctorNavigationItemContentList
import com.example.aesculapius.ui.doctor.navigation.DoctorProfileScreen
import com.example.aesculapius.ui.doctor.navigation.PatientDetailScreen
import com.example.aesculapius.ui.doctor.navigation.PatientsListScreen
import com.example.aesculapius.ui.doctor.patientdetail.PatientDetailScreen as PatientDetailScreenUi
import com.example.aesculapius.ui.doctor.patients.PatientsListScreen as PatientsListScreenUi
import com.example.aesculapius.ui.doctor.profile.DoctorProfileScreen as DoctorProfileScreenUi
import com.example.aesculapius.ui.home.NavigationItemContent
import com.example.aesculapius.ui.profile.ProfileEvent
import com.example.aesculapius.ui.signup.DoctorUiState

@Composable
fun DoctorHomeScreen(
    doctorUiState: DoctorUiState,
    onProfileEvent: (ProfileEvent) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: PatientsListScreen.route

    val topLevelRoutes = remember { doctorNavigationItemContentList.map { it.pageType }.toSet() }
    val isBarsDisplayed = currentRoute in topLevelRoutes

    Scaffold(
        topBar = {
            if (isBarsDisplayed) DoctorTopBar(
                text = when (currentRoute) {
                    DoctorProfileScreen.route -> "Профиль"
                    else -> "Пациенты"
                }
            )
        },
        bottomBar = {
            if (isBarsDisplayed) DoctorBottomBar(
                currentTab = currentRoute,
                onTabPressed = { pageType ->
                    if (pageType != currentRoute) navController.navigate(pageType) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = PatientsListScreen.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            composable(PatientsListScreen.route) {
                PatientsListScreenUi(
                    onPatientClick = { patientId ->
                        navController.navigate("${PatientDetailScreen.route}/$patientId")
                    }
                )
            }
            composable(DoctorProfileScreen.route) {
                DoctorProfileScreenUi(
                    doctor = doctorUiState,
                    onSignOut = { onProfileEvent(ProfileEvent.OnSignOut) }
                )
            }
            composable(
                route = PatientDetailScreen.routeWithArgs,
                arguments = listOf(navArgument(PatientDetailScreen.patientIdArg) {
                    type = NavType.StringType
                })
            ) {
                PatientDetailScreenUi(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}

@Composable
private fun DoctorTopBar(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 24.dp)
        )
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun DoctorBottomBar(
    currentTab: String,
    onTabPressed: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(63.dp),
        containerColor = MaterialTheme.colorScheme.onBackground,
        tonalElevation = 10.dp
    ) {
        for (navItem in doctorNavigationItemContentList) {
            NavigationBarItem(
                selected = currentTab == navItem.pageType,
                onClick = { onTabPressed(navItem.pageType) },
                icon = {
                    Image(
                        painter = painterResource(id = navItem.icon),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                },
                alwaysShowLabel = false
            )
        }
    }
}
