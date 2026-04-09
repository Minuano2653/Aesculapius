package com.example.aesculapius.ui.profile

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.database.AesculapiusRepository
import com.example.aesculapius.database.Converters
import com.example.aesculapius.database.UserPreferencesRepository
import com.example.aesculapius.database.UserRemoteDataRepository
import com.example.aesculapius.notifications.MetricsAlarm
import com.example.aesculapius.ui.signup.SignUpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefRepository: UserPreferencesRepository,
    private val userRemoteDataRepository: UserRemoteDataRepository,
    private val aesculapiusRepository: AesculapiusRepository,
    private val application: Application
) : ViewModel() {
    private val morningAlarmManager =
        application.applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
    private val eveningAlarmManager =
        application.applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager

    val userUiState: StateFlow<SignUpUiState> = prefRepository.user
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SignUpUiState()
        )

    fun onProfileEvent(event: ProfileEvent) = viewModelScope.launch {
        when (event) {
            is ProfileEvent.OnSaveAstTestDate -> {
                prefRepository.saveAstTestDate(Converters.dateToStringWithFormat(event.astTestDate))
                userRemoteDataRepository.updateAstDate(userId = userUiState.value.id!!, event.astTestDate)
            }

            is ProfileEvent.OnSaveRecommendationTestDate -> {
                prefRepository.saveRecommendationTest(
                    Converters.dateToStringWithFormat(event.recommendationTestDate)
                )
                userRemoteDataRepository.updateRecDate(userId = userUiState.value.id!!, event.recommendationTestDate)
            }

            is ProfileEvent.OnUpdateUserProfile -> {
                prefRepository.saveUserData(event.signUpUiState)
                userRemoteDataRepository.updateUserProfile(event.signUpUiState)
            }

            is ProfileEvent.OnSaveEveningTime -> {
                prefRepository.saveUserEveningReminder(Converters.timeToString(event.eveningTime))
                userRemoteDataRepository.updateEveningDate(userId = userUiState.value.id!!, event.eveningTime)
                setEveningNotification(event.eveningTime)
            }

            is ProfileEvent.OnSaveMorningTime -> {
                prefRepository.saveUserMorningReminder(Converters.timeToString(event.morningTime))
                userRemoteDataRepository.updateMorningDate(userId = userUiState.value.id!!, event.morningTime)
                setMorningNotification(event.morningTime)
            }

            is ProfileEvent.OnSaveNewUser -> {
                prefRepository.saveUserData(event.signUpUiState)
                userRemoteDataRepository.addUserAtFirst(event.signUpUiState)
                initMorningEveningNotifications(
                    event.signUpUiState.morningReminder,
                    event.signUpUiState.eveningReminder
                )
            }

            is ProfileEvent.OnLoginUser -> {
                val user = userRemoteDataRepository.pullUserData(event.userId)
                initMorningEveningNotifications(
                    LocalDateTime.parse(user.morningReminder),
                    LocalDateTime.parse(user.eveningReminder)
                )
                prefRepository.saveUserData(
                    SignUpUiState(
                        id = event.userId,
                        name = user.name,
                        surname = user.surname,
                        patronymic = user.patronymic,
                        birthday = LocalDate.parse(user.birthDate),
                        height = user.height.toString(),
                        weight = user.weight.toString(),
                        morningReminder = LocalDateTime.parse(user.morningReminder),
                        eveningReminder = LocalDateTime.parse(user.eveningReminder),
                        astTestDate = user.astTestDate,
                        recommendationTestDate = user.recommendationTestDate
                    )
                )
                user.metrics.forEach {
                    aesculapiusRepository.insertMetrics(it.metrics, it.date)
                }
                user.astTests.forEach {
                    aesculapiusRepository.insertAstTestScore(it.date, it.score)
                }
            }
        }
    }

    private fun setMorningNotification(
        morningTimeMillis: Long,
        morningPendingIntent: PendingIntent
    ) {
        morningAlarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            morningTimeMillis,
            AlarmManager.INTERVAL_DAY,
            morningPendingIntent
        )
    }

    private fun setEveningNotification(
        eveningTimeMillis: Long,
        eveningPendingIntent: PendingIntent
    ) {
        eveningAlarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            eveningTimeMillis,
            AlarmManager.INTERVAL_DAY,
            eveningPendingIntent
        )
    }

    private fun setMorningNotification(morningTime: LocalDateTime) {
        val morningTimeMillis =
            morningTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val morningIntent = Intent(application.applicationContext, MetricsAlarm::class.java).apply {
            putExtra("title", "Утреннее напоминание")
            putExtra("message", "Не забудьте ввести метрики с пикфлоуметра!")
        }
        val morningPendingIntent = PendingIntent.getBroadcast(
            application.applicationContext,
            0,
            morningIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        setMorningNotification(
            morningTimeMillis,
            morningPendingIntent
        )
    }

    private fun setEveningNotification(eveningTime: LocalDateTime) {
        val eveningTimeMillis =
            eveningTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val eveningIntent = Intent(application.applicationContext, MetricsAlarm::class.java).apply {
            putExtra("title", "Вечернее напоминание")
            putExtra("message", "Не забудьте ввести метрики с пикфлоуметра!")
        }
        val eveningPendingIntent = PendingIntent.getBroadcast(
            application.applicationContext,
            1,
            eveningIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        setEveningNotification(
            eveningTimeMillis,
            eveningPendingIntent
        )
    }

    private fun initMorningEveningNotifications(
        morningReminder: LocalDateTime,
        eveningReminder: LocalDateTime
    ) {
        val morningTimeMillis =
            morningReminder.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val morningIntent = Intent(application.applicationContext, MetricsAlarm::class.java).apply {
            putExtra("title", "Утреннее напоминание")
            putExtra("message", "Не забудьте ввести метрики с пикфлоуметра!")
        }
        val morningPendingIntent = PendingIntent.getBroadcast(
            application.applicationContext,
            0,
            morningIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        setMorningNotification(
            morningTimeMillis,
            morningPendingIntent
        )

        val eveningTimeMillis =
            eveningReminder.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val eveningIntent = Intent(application.applicationContext, MetricsAlarm::class.java).apply {
            putExtra("title", "Вечернее напоминание")
            putExtra("message", "Не забудьте ввести метрики с пикфлоуметра!")
        }
        val eveningPendingIntent = PendingIntent.getBroadcast(
            application.applicationContext,
            1,
            eveningIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        setEveningNotification(
            eveningTimeMillis,
            eveningPendingIntent
        )
    }
}