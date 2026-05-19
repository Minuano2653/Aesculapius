package com.example.aesculapius.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aesculapius.R
import com.example.aesculapius.data.learnList
import com.example.aesculapius.ui.airquality.AirQualityScreen
import com.example.aesculapius.ui.airquality.LocationPickerScreen
import com.example.aesculapius.ui.airquality.components.ProfileAqiWidget
import com.example.aesculapius.ui.medicines.MedicinesListScreen
import com.example.aesculapius.ui.navigation.NavigationDestination
import com.example.aesculapius.ui.symptoms.SymptomsScreen
import com.example.aesculapius.ui.theme.AesculapiusTheme
import com.google.common.primitives.Doubles.min
import kotlin.math.abs

object ProfileScreen : NavigationDestination {
    override val route = "ProfileScreen"
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel<ProfileViewModel>(),
    onNavigate: (String) -> Unit,
) {
    val activityState by viewModel.activityState.collectAsStateWithLifecycle()
    val airQualityState by viewModel.airQualityState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onProfileEvent(ProfileEvent.OnRefreshAirQuality)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        if (activityState.mainScore > 0) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 0.dp
                ) {
                    YourActivity(
                        metricsScore = activityState.metricsScore,
                        astTestScore = activityState.astTestScore,
                        medicinesScore = activityState.medicinesScore,
                        score = activityState.mainScore,
                        navigate = onNavigate
                    )
                }
            }
        }

        item {
            ProfileAqiWidget(
                state = airQualityState,
                onNavigateToPicker = { onNavigate(LocationPickerScreen.route) },
                onNavigateToAirQuality = { onNavigate(AirQualityScreen.route) },
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(EditProfileScreen.route) },
                elevation = 0.dp
            ) {
                SingleItem(
                    image = R.drawable.profile_icon,
                    name = stringResource(id = R.string.profile),
                    onClick = { onNavigate(EditProfileScreen.route) }
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(LearnScreen.route) },
                elevation = 0.dp
            ) {
                SingleItem(
                    image = R.drawable.book_icon,
                    name = stringResource(id = R.string.learn_block_name),
                    onClick = { onNavigate(LearnScreen.route) }
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(SetReminderTimeProfile.route) },
                elevation = 0.dp
            ) {
                SingleItem(
                    image = R.drawable.timer_icon,
                    name = stringResource(id = R.string.set_reminders),
                    onClick = { onNavigate(SetReminderTimeProfile.route) }
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(MedicinesListScreen.route) },
                elevation = 0.dp
            ) {
                SingleItem(
                    image = R.drawable.therapy_icon,
                    name = stringResource(id = R.string.my_medicines),
                    onClick = { onNavigate(MedicinesListScreen.route) }
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(SymptomsScreen.route) },
                elevation = 0.dp
            ) {
                SingleItem(
                    image = R.drawable.symptoms_icon,
                    name = stringResource(id = R.string.my_symptoms),
                    onClick = { onNavigate(SymptomsScreen.route) }
                )
            }
        }
    }
}

@Composable
fun YourActivity(score: Double, navigate: (String) -> Unit, metricsScore: Double, astTestScore: Double, medicinesScore: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp)
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.your_activity),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            // выявляем наименьший параметр и выводим информацию о нём
            when(min(metricsScore, astTestScore, medicinesScore)) {
                metricsScore ->
                    Text(
                        text = "Пикфлоуметр требует\nособого внимания",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                medicinesScore ->
                    Text(
                        text = "Не забывай своевременно принимать препараты",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                astTestScore -> {
                    Text(
                        text = "Наблюдаются проблемы с контролем астмы ",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable {
                        navigate("${LearnItemScreen.route}/${learnList.last().name}^${learnList.last().text}")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.what_is_activity),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.vector),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(vertical = 16.dp)
                .padding(end = 27.dp, bottom = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            val distance0: Double = score
            val distance25: Double = abs(score - 2.5)
            val distance33: Double = abs(score - 3.3)
            val distance50: Double = abs(score - 5.0)
            val distance66: Double = abs(score - 6.6)
            val distance75: Double = abs(score - 7.5)
            val distance100: Double = abs(score - 10.0)
            val distance = minOf(distance0, distance33, distance50, distance75, distance100)
            when (distance) {
                distance0 -> {
                    Image(
                        painter = painterResource(id = R.drawable.progress0),
                        contentDescription = null
                    )
                }

                distance25 -> {
                    Image(
                        painter = painterResource(id = R.drawable.progress25),
                        contentDescription = null
                    )
                }

                distance33 -> {
                    Image(
                        painter = painterResource(id = R.drawable.progress33),
                        contentDescription = null
                    )
                }

                distance50 -> {
                    Image(
                        painter = painterResource(id = R.drawable.progress50),
                        contentDescription = null
                    )
                }

                distance66 -> {
                    Image(
                        painter = painterResource(id = R.drawable.progress66),
                        contentDescription = null
                    )
                }

                distance75 -> {
                    Image(
                        painter = painterResource(id = R.drawable.progress75),
                        contentDescription = null
                    )
                }

                distance100 -> {
                    Image(
                        painter = painterResource(id = R.drawable.progress100),
                        contentDescription = null
                    )
                }
            }
            Text(
                text = String.format("%.2f", score),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@Composable
fun SingleItem(image: Int, name: String, onClick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 20.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Image(painter = painterResource(id = image), contentDescription = null)
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ProfileScreenPreview() {
    AesculapiusTheme {
        ProfileScreen(
            onNavigate = {}
        )
    }
}