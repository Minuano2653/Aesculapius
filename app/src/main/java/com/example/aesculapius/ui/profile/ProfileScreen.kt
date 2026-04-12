package com.example.aesculapius.ui.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aesculapius.R
import com.example.aesculapius.data.learnList
import com.example.aesculapius.ui.medicines.MedicinesListScreen
import com.example.aesculapius.ui.navigation.NavigationDestination
import com.example.aesculapius.ui.theme.AesculapiusTheme
import com.google.common.primitives.Doubles.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

object ProfileScreen : NavigationDestination {
    override val route = "ProfileScreen"
}

@Composable
fun ProfileScreen(
    userRegisterDate: LocalDate,
    onNavigate: (String) -> Unit,
    getMedicinesScore: suspend () -> Double,
    getTestsScore: suspend () -> Pair<Double, Double>,
    modifier: Modifier = Modifier
) {
    var mainScore by remember { mutableDoubleStateOf(0.0) }

    var medicinesScore by remember { mutableDoubleStateOf(0.0) }
    var astTestScore by remember { mutableDoubleStateOf(0.0) }
    var metricsScore by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(key1 = Unit) {
        mainScore =
            // если с момента регистрации пользователя в системе прошлло 30 и более дней, выводим математику
            if (ChronoUnit.DAYS.between(userRegisterDate, LocalDate.now()) >= 30) {
                withContext(Dispatchers.IO) {
                    val temp = getTestsScore()
                    astTestScore = temp.first
                    metricsScore = temp.second
                    medicinesScore = getMedicinesScore()
                    (astTestScore + metricsScore + medicinesScore + 1) * 2.5
                }
            }
            // иначе делаем показатель равным -1 и пока не отображаем его для пользователя
            else -1.0
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (mainScore > 0)
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                elevation = 0.dp
            ) {
                YourActivity(metricsScore = metricsScore, astTestScore = astTestScore, medicinesScore = medicinesScore, score = mainScore, navigate = onNavigate)
            }

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .clickable { onNavigate(EditProfileScreen.route) }
                .fillMaxWidth(),
            elevation = 0.dp
        ) {
            SingleItem(
                image = R.drawable.profile_icon,
                name = stringResource(id = R.string.profile),
                onClick = { onNavigate(EditProfileScreen.route) }
            )
        }
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigate(LearnScreen.route) }
                .padding(vertical = 24.dp),
            elevation = 0.dp
        ) {
            SingleItem(
                image = R.drawable.book_icon,
                name = stringResource(id = R.string.learn_block_name),
                onClick = { onNavigate(LearnScreen.route) }
            )
        }
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
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigate(MedicinesListScreen.route) }
                .padding(top = 24.dp),
            elevation = 0.dp
        ) {
            SingleItem(
                image = R.drawable.therapy_icon,
                name = stringResource(id = R.string.my_medicines),
                onClick = { onNavigate(MedicinesListScreen.route) }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.version),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 20.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun YourActivity(score: Double, navigate: (String) -> Unit, metricsScore: Double, astTestScore: Double, medicinesScore: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier
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
        Spacer(Modifier.weight(1f))
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
            onNavigate = {},
            getMedicinesScore = { 3.3 },
            getTestsScore = { Pair(3.3, 4.5) },
            userRegisterDate = LocalDate.now().minusMonths(2)
        )
    }
}