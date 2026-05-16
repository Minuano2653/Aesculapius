package com.example.aesculapius.ui.profile

import android.app.AlarmManager
import android.content.Context
import com.example.aesculapius.database.UserPreferencesRepository
import com.example.aesculapius.database.UserRemoteDataRepository
import com.example.aesculapius.domain.airquality.usecases.GetSavedAirQualityCacheFlowUseCase
import com.example.aesculapius.domain.airquality.usecases.RefreshAirQualityForSavedLocationUseCase
import com.example.aesculapius.domain.auth.usecases.SignOutUseCase
import com.example.aesculapius.domain.profile.UserActivityResult
import com.example.aesculapius.domain.profile.usecases.GetUserActivityScoreUseCase
import com.example.aesculapius.ui.signup.SignUpUiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var prefRepository: UserPreferencesRepository
    private lateinit var remoteRepository: UserRemoteDataRepository
    private lateinit var context: Context
    private lateinit var getUserActivityScoreUseCase: GetUserActivityScoreUseCase
    private lateinit var getSavedAirQualityCacheFlowUseCase: GetSavedAirQualityCacheFlowUseCase
    private lateinit var refreshAirQualityForSavedLocationUseCase: RefreshAirQualityForSavedLocationUseCase
    private lateinit var signOutUseCase: SignOutUseCase

    private val userFlow = MutableStateFlow(SignUpUiState())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        val alarmManager = mockk<AlarmManager>(relaxed = true)
        context = mockk(relaxed = true)
        every { context.getSystemService(Context.ALARM_SERVICE) } returns alarmManager
        every { context.getSystemService("alarm") } returns alarmManager

        prefRepository = mockk()
        remoteRepository = mockk(relaxed = true)
        getUserActivityScoreUseCase = mockk()
        getSavedAirQualityCacheFlowUseCase = mockk()
        refreshAirQualityForSavedLocationUseCase = mockk(relaxed = true)
        signOutUseCase = mockk(relaxed = true)

        every { prefRepository.user } returns userFlow
        every { getSavedAirQualityCacheFlowUseCase() } returns flowOf(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = ProfileViewModel(
        prefRepository = prefRepository,
        userRemoteDataRepository = remoteRepository,
        context = context,
        getUserActivityScoreUseCase = getUserActivityScoreUseCase,
        getSavedAirQualityCacheFlowUseCase = getSavedAirQualityCacheFlowUseCase,
        refreshAirQualityForSavedLocationUseCase = refreshAirQualityForSavedLocationUseCase,
        signOutUseCase = signOutUseCase,
    )

    @Test
    fun `начальное значение activityState имеет mainScore = -1_0`() = runTest {
        // Начальное значение доступно без подписки — stateIn всегда хранит последнее значение
        val vm = createViewModel()

        assertEquals(-1.0, vm.activityState.value.mainScore, 0.001)
    }

    @Test
    fun `activityState обновляется после эмита пользователя с датой больше 30 дней`() = runTest {
        val registerDate = LocalDate.now().minusDays(60)
        val expected = UserActivityResult(
            mainScore = 10.0,
            astTestScore = 1.0,
            metricsScore = 1.0,
            medicinesScore = 1.0
        )
        coEvery { getUserActivityScoreUseCase(registerDate) } returns expected

        val vm = createViewModel()
        // Активируем WhileSubscribed — без подписчика upstream не стартует
        backgroundScope.launch { vm.activityState.collect {} }

        userFlow.emit(SignUpUiState(userRegisterDate = registerDate))
        advanceUntilIdle()

        assertEquals(10.0, vm.activityState.value.mainScore, 0.001)
        assertEquals(1.0, vm.activityState.value.astTestScore, 0.001)
    }

    @Test
    fun `activityState остаётся с mainScore = -1 если пользователь зарегистрирован менее 30 дней`() = runTest {
        val registerDate = LocalDate.now().minusDays(10)
        coEvery { getUserActivityScoreUseCase(registerDate) } returns UserActivityResult()

        val vm = createViewModel()
        backgroundScope.launch { vm.activityState.collect {} }

        userFlow.emit(SignUpUiState(userRegisterDate = registerDate))
        advanceUntilIdle()

        assertEquals(-1.0, vm.activityState.value.mainScore, 0.001)
    }

    @Test
    fun `activityState пересчитывается при повторном эмите пользователя`() = runTest {
        val oldDate = LocalDate.now().minusDays(10)
        val newDate = LocalDate.now().minusDays(60)

        coEvery { getUserActivityScoreUseCase(oldDate) } returns UserActivityResult()
        coEvery { getUserActivityScoreUseCase(newDate) } returns UserActivityResult(
            mainScore = 7.5,
            astTestScore = 0.5,
            metricsScore = 1.0,
            medicinesScore = 0.5
        )

        val vm = createViewModel()
        backgroundScope.launch { vm.activityState.collect {} }

        userFlow.emit(SignUpUiState(userRegisterDate = oldDate))
        advanceUntilIdle()
        assertEquals(-1.0, vm.activityState.value.mainScore, 0.001)

        userFlow.emit(SignUpUiState(userRegisterDate = newDate))
        advanceUntilIdle()
        assertEquals(7.5, vm.activityState.value.mainScore, 0.001)
    }

    @Test
    fun `userUiState отражает данные из prefRepository`() = runTest {
        val vm = createViewModel()
        // Активируем userUiState — тоже использует WhileSubscribed
        backgroundScope.launch { vm.userUiState.collect {} }

        val testUser = SignUpUiState(name = "Иван", id = "user123")
        userFlow.emit(testUser)
        advanceUntilIdle()

        assertEquals("Иван", vm.userUiState.value.name)
        assertEquals("user123", vm.userUiState.value.id)
    }
}
