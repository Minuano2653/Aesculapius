package com.example.aesculapius.ui.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.aesculapius.R
import com.example.aesculapius.data.navigationItemContentList
import com.example.aesculapius.data.topBarHomeScreen
import com.example.aesculapius.ui.navigation.diaryNavGraph
import com.example.aesculapius.ui.navigation.profileNavGraph
import com.example.aesculapius.ui.navigation.statisticsNavGraph
import com.example.aesculapius.ui.navigation.testsNavGraph
import com.example.aesculapius.ui.navigation.therapyNavGraph
import com.example.aesculapius.ui.profile.ProfileEvent
import com.example.aesculapius.ui.signup.SignUpUiState
import com.example.aesculapius.ui.therapy.MedicineCard
import com.example.aesculapius.ui.therapy.TherapyScreen
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    onProfileEvent: (ProfileEvent) -> Unit,
    userUiState: SignUpUiState,
) {
    val selectedMedicineFromProfile: MutableState<MedicineCard?> = remember { mutableStateOf(null) }

    val navController = rememberAnimatedNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute: String = navBackStackEntry?.destination?.route ?: TherapyScreen.route

    val topLevelRoutes = remember { navigationItemContentList.map { it.pageType }.toSet() }
    val isBarsDisplayed = currentRoute in topLevelRoutes

    Scaffold(
        topBar = {
            if (isBarsDisplayed) TopBar(
                screenName = stringResource(
                    id = topBarHomeScreen[currentRoute]?.first ?: R.string.therapy_name
                )
            )
        },
        bottomBar = {
            if (isBarsDisplayed) BottomNavigationBar(
                currentTab = currentRoute,
                onTabPressed = { pageType ->
                    navController.navigate(pageType)
                },
                navigationItemContentList = navigationItemContentList
            )
        }) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = TherapyScreen.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = contentPadding),
            builder = {
                therapyNavGraph(navController = navController)

                profileNavGraph(
                    userUiState = userUiState,
                    navController = navController,
                    onProfileEvent = onProfileEvent,
                    selectedMedicine = selectedMedicineFromProfile.value,
                    onMedicineSelected = { selectedMedicineFromProfile.value = it }
                )

                statisticsNavGraph(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    userUiState = userUiState
                )

                testsNavGraph(
                    userUiState = userUiState,
                    navController = navController,
                    onProfileEvent = onProfileEvent
                )

                diaryNavGraph(
                    navController = navController
                )
            }
        )
    }
}

/** [TopBar] для главного экрана без навигации */
@Composable
fun TopBar(modifier: Modifier = Modifier, screenName: String) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = screenName,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 24.dp)
        )
        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun BottomNavigationBar(
    currentTab: String,
    onTabPressed: ((String) -> Unit),
    navigationItemContentList: List<NavigationItemContent>,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(63.dp),
        containerColor = MaterialTheme.colorScheme.onBackground,
        tonalElevation = 10.dp
    ) {
        for (navItem in navigationItemContentList) {
            NavigationBarItem(
                selected = currentTab == navItem.pageType,
                onClick = { onTabPressed(navItem.pageType) },
                icon = {
                    Image(
                        painterResource(id = navItem.icon),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                },
                alwaysShowLabel = false
            )
        }
    }
}
