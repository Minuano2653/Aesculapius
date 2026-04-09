package com.example.aesculapius

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.aesculapius.database.UserRemoteDataRepository
import com.example.aesculapius.ui.home.HomeScreen
import com.example.aesculapius.ui.navigation.SignUpNavigation
import com.example.aesculapius.ui.profile.ProfileEvent
import com.example.aesculapius.ui.profile.ProfileViewModel
import com.example.aesculapius.ui.signup.SignUpUiState
import com.example.aesculapius.ui.theme.AesculapiusTheme
import com.example.aesculapius.worker.UserWorkerSchedule
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // инициализация библиотеки для работы с временем
        AndroidThreeTen.init(this)

        setContent {
            AesculapiusTheme {

                // здесь при запуске приложения инициализируются переменные, хранящиеся в DataStore Preferences
                val profileViewModel: ProfileViewModel = hiltViewModel()
                val userUiState: SignUpUiState by profileViewModel.userUiState.collectAsState()

                when (userUiState.id) {
                    "" -> SignUpNavigation(onProfileEvent = profileViewModel::onProfileEvent)

                    null -> ImageDisplay()

                    else -> {
                        // передаём id пользователя в worker, запускающийся периодически для бэкапа статистики
                        val inputData = Data.Builder().putString("userId", userUiState.id).build()
                        Log.d("USER_TAG", "user id: ${userUiState.id}")
                        val workRequest = OneTimeWorkRequestBuilder<UserWorkerSchedule>().setInputData(inputData).build()
                        WorkManager.getInstance(this).enqueue(workRequest)

                        HomeScreen(
                            userUiState = userUiState,
                            onProfileEvent = profileViewModel::onProfileEvent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageDisplay() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}