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
import com.example.aesculapius.domain.auth.model.SessionState
import com.example.aesculapius.ui.doctor.home.DoctorHomeScreen
import com.example.aesculapius.ui.home.HomeScreen
import com.example.aesculapius.ui.navigation.SignUpNavigation
import com.example.aesculapius.ui.profile.ProfileViewModel
import com.example.aesculapius.ui.theme.AesculapiusTheme
import com.example.aesculapius.worker.UserWorkerSchedule
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidThreeTen.init(this)

        setContent {
            AesculapiusTheme {
                val profileViewModel: ProfileViewModel = hiltViewModel()
                val session by profileViewModel.sessionState.collectAsState()

                when (val state = session) {
                    is SessionState.Loading -> ImageDisplay()

                    is SessionState.Guest ->
                        SignUpNavigation(onProfileEvent = profileViewModel::onProfileEvent)

                    is SessionState.Patient -> {
                        val inputData = Data.Builder()
                            .putString("userId", state.state.id)
                            .build()
                        Log.d("USER_TAG", "user id: ${state.state.id}")
                        val workRequest = OneTimeWorkRequestBuilder<UserWorkerSchedule>()
                            .setInputData(inputData)
                            .build()
                        WorkManager.getInstance(this).enqueue(workRequest)

                        HomeScreen(
                            userUiState = state.state,
                            onProfileEvent = profileViewModel::onProfileEvent
                        )
                    }

                    is SessionState.Doctor -> {
                        DoctorHomeScreen(
                            doctorUiState = state.state,
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
