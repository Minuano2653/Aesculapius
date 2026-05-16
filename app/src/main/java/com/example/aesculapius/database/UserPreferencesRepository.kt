package com.example.aesculapius.database

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.aesculapius.domain.airquality.model.AirQualityCache
import com.example.aesculapius.domain.airquality.model.AirQualityData
import com.example.aesculapius.domain.airquality.model.LocationData
import com.example.aesculapius.ui.signup.SignUpUiState
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("user_preferences")

/** [UserPreferencesRepository] репозиторий для DataStorePreferences */
@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext appContext: Context,
    moshi: Moshi
) {
    private val settingDataStore = appContext.dataStore
    private val airQualityAdapter = moshi.adapter(AirQualityData::class.java)

    val user: Flow<SignUpUiState> = settingDataStore.data.map { preferences ->
        SignUpUiState(
            id = preferences[IS_USER_REGISTERED] ?: "",
            userRegisterDate = Converters.stringToDateNoFormat(preferences[USER_REGISTER_DATE]),
            name = preferences[NAME] ?: "",
            surname = preferences[SURNAME] ?: "",
            patronymic = preferences[PATRONYMIC] ?: "",
            birthday = LocalDate.parse(preferences[BIRTHDAY] ?: LocalDate.now().toString()),
            height = preferences[HEIGHT] ?: "",
            weight = preferences[WEIGHT] ?: "",
            morningReminder = Converters.stringToTime(preferences[MORNING_REMINDER_TIME]),
            eveningReminder = Converters.stringToTime(preferences[EVENING_REMINDER_TIME]),
            astTestDate = preferences[AST_TEST] ?: "",
            recommendationTestDate = preferences[RECOMMENDATION_TEST] ?: ""
        )
    }

    val airQuality: Flow<AirQualityCache?> = settingDataStore.data.map { preferences ->
        val lat = preferences[LATITUDE] ?: return@map null
        val lon = preferences[LONGITUDE] ?: return@map null
        val name = preferences[LOCATION_NAME].orEmpty()
        val cachedAqi = preferences[AQI_DATA_JSON]?.let { json ->
            runCatching { airQualityAdapter.fromJson(json) }.getOrNull()
        }
        AirQualityCache(LocationData(lat, lon, name), cachedAqi)
    }

    private companion object {
        val IS_USER_REGISTERED = stringPreferencesKey("is_user_registered")
        val MORNING_REMINDER_TIME = stringPreferencesKey("morning_reminder_time")
        val EVENING_REMINDER_TIME = stringPreferencesKey("evening_reminder_time")
        val AST_TEST = stringPreferencesKey("ast_test")
        val RECOMMENDATION_TEST = stringPreferencesKey("recommendation_test")

        val USER_REGISTER_DATE = stringPreferencesKey("user_register_date")

        val SURNAME = stringPreferencesKey("surname")
        val PATRONYMIC = stringPreferencesKey("patronymic")
        val NAME = stringPreferencesKey("name")
        val BIRTHDAY = stringPreferencesKey("birthday")
        val HEIGHT = stringPreferencesKey("height")
        val WEIGHT = stringPreferencesKey("weight")

        val LATITUDE = doublePreferencesKey("aqi_location_lat")
        val LONGITUDE = doublePreferencesKey("aqi_location_lon")
        val LOCATION_NAME = stringPreferencesKey("aqi_location_name")
        val AQI_DATA_JSON = stringPreferencesKey("aqi_data_json")
        val AQI_FETCHED_AT = longPreferencesKey("aqi_fetched_at")
    }

    /**
     * [saveUserData] переносит параметры из SignUpUiState в
     * DotsStore Preferences для дальнейшего редактирования в профиле
     */
    suspend fun saveUserData(signUpUiState: SignUpUiState) {
        settingDataStore.edit { preferences ->
            if (preferences[IS_USER_REGISTERED] == null) {
                preferences[IS_USER_REGISTERED] = signUpUiState.id!!
                preferences[USER_REGISTER_DATE] = LocalDate.now().toString()
                preferences[SURNAME] = signUpUiState.surname
                preferences[PATRONYMIC] = signUpUiState.patronymic
                preferences[NAME] = signUpUiState.name
                preferences[BIRTHDAY] = signUpUiState.birthday.toString()
                preferences[HEIGHT] = signUpUiState.height
                preferences[WEIGHT] = signUpUiState.weight
                preferences[MORNING_REMINDER_TIME] = Converters.timeToString(signUpUiState.morningReminder)
                preferences[EVENING_REMINDER_TIME] = Converters.timeToString(signUpUiState.eveningReminder)
            } else {
                preferences[IS_USER_REGISTERED] = signUpUiState.id!!
                preferences[SURNAME] = signUpUiState.surname
                preferences[PATRONYMIC] = signUpUiState.patronymic
                preferences[NAME] = signUpUiState.name
                preferences[BIRTHDAY] = signUpUiState.birthday.toString()
                preferences[HEIGHT] = signUpUiState.height
                preferences[WEIGHT] = signUpUiState.weight
                preferences[MORNING_REMINDER_TIME] = Converters.timeToString(signUpUiState.morningReminder)
                preferences[EVENING_REMINDER_TIME] = Converters.timeToString(signUpUiState.eveningReminder)
            }
        }
    }

    /**
     * [saveAstTestDate] сохраняет полную дату следующего прохождения АСТ теста
     */
    suspend fun saveAstTestDate(astTestDate: String) {
        settingDataStore.edit { preferences ->
            preferences[AST_TEST] = astTestDate
        }
    }

    /**
     * [saveRecommendationTest] сохраняет полную дату следующего прохождения Теста приверженности
     */
    suspend fun saveRecommendationTest(recommendationTest: String) {
        settingDataStore.edit { preferences ->
            preferences[RECOMMENDATION_TEST] = recommendationTest
        }
    }

    /**
     * [saveUserMorningReminder] сохраняет полную дату и
     * время следующего утреннего внесения метрик
     */
    suspend fun saveUserMorningReminder(morningReminder: String) {
        settingDataStore.edit { preferences ->
            preferences[MORNING_REMINDER_TIME] = morningReminder
        }
    }

    /**
     * [saveUserEveningReminder] сохраняет полную дату и
     * время следующего вечернего внесения метрик
     */
    suspend fun saveUserEveningReminder(eveningReminder: String) {
        settingDataStore.edit { preferences ->
            preferences[EVENING_REMINDER_TIME] = eveningReminder
        }
    }

    suspend fun saveSelectedLocation(lat: Double, lon: Double, name: String) {
        settingDataStore.edit { preferences ->
            preferences[LATITUDE] = lat
            preferences[LONGITUDE] = lon
            preferences[LOCATION_NAME] = name
        }
    }

    suspend fun saveAirQuality(data: AirQualityData) {
        val json = airQualityAdapter.toJson(data)
        settingDataStore.edit { preferences ->
            preferences[AQI_DATA_JSON] = json
            preferences[AQI_FETCHED_AT] = data.fetchedAt
        }
    }

    suspend fun clearUserData() {
        settingDataStore.edit { it.clear() }
    }
}
