package com.example.aesculapius.ui.tests

import com.example.aesculapius.domain.tests.usecases.CalculateAstScoreUseCase
import com.example.aesculapius.domain.tests.usecases.CalculateRecommendationScoreUseCase
import com.example.aesculapius.domain.tests.usecases.GetAllMetricsWithDateUseCase
import com.example.aesculapius.domain.tests.usecases.InsertMetricsUseCase
import com.example.aesculapius.domain.tests.usecases.SaveAstTestUseCase
import com.example.aesculapius.domain.tests.usecases.SaveRecommendationTestUseCase
import com.example.aesculapius.domain.tests.usecases.UpdateMetricsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class TestsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var saveAstTestUseCase: SaveAstTestUseCase
    private lateinit var saveRecommendationTestUseCase: SaveRecommendationTestUseCase
    private lateinit var insertMetricsUseCase: InsertMetricsUseCase
    private lateinit var updateMetricsUseCase: UpdateMetricsUseCase
    private lateinit var getAllMetricsWithDateUseCase: GetAllMetricsWithDateUseCase
    private lateinit var calculateAstScoreUseCase: CalculateAstScoreUseCase
    private lateinit var calculateRecommendationScoreUseCase: CalculateRecommendationScoreUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        saveAstTestUseCase = mockk(relaxed = true)
        saveRecommendationTestUseCase = mockk(relaxed = true)
        insertMetricsUseCase = mockk(relaxed = true)
        updateMetricsUseCase = mockk(relaxed = true)
        getAllMetricsWithDateUseCase = mockk()
        calculateAstScoreUseCase = mockk()
        calculateRecommendationScoreUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = TestsViewModel(
        saveAstTestUseCase = saveAstTestUseCase,
        saveRecommendationTestUseCase = saveRecommendationTestUseCase,
        insertMetricsUseCase = insertMetricsUseCase,
        updateMetricsUseCase = updateMetricsUseCase,
        getAllMetricsWithDateUseCase = getAllMetricsWithDateUseCase,
        calculateAstScoreUseCase = calculateAstScoreUseCase,
        calculateRecommendationScoreUseCase = calculateRecommendationScoreUseCase
    )

    // ── OnUpdateSummaryScore ────────────────────────────────────────────────

    @Test
    fun `isAstTest = true — вызывается saveAstTestUseCase, saveRecommendationTestUseCase не вызывается`() = runTest {
        val answers = listOf(3, 3, 3, 3, 3)
        coEvery { calculateAstScoreUseCase(answers) } returns 20

        val vm = createViewModel()
        vm.onTestsEvent(TestsEvent.OnUpdateSummaryScore("userId", answers, isAstTest = true))
        advanceUntilIdle()

        coVerify(exactly = 1) { saveAstTestUseCase("userId", any(), 20) }
        coVerify(exactly = 0) { saveRecommendationTestUseCase(any(), any(), any()) }
    }

    @Test
    fun `isAstTest = false — вызывается saveRecommendationTestUseCase, saveAstTestUseCase не вызывается`() = runTest {
        val answers = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
        coEvery { calculateRecommendationScoreUseCase(answers) } returns 4

        val vm = createViewModel()
        vm.onTestsEvent(TestsEvent.OnUpdateSummaryScore("userId", answers, isAstTest = false))
        advanceUntilIdle()

        coVerify(exactly = 1) { saveRecommendationTestUseCase("userId", any(), 4) }
        coVerify(exactly = 0) { saveAstTestUseCase(any(), any(), any()) }
    }

    @Test
    fun `summaryScore обновляется после OnUpdateSummaryScore`() = runTest {
        val answers = listOf(1, 1, 1, 1, 1)
        coEvery { calculateAstScoreUseCase(answers) } returns 10

        val vm = createViewModel()
        assertEquals(0, vm.summaryScore.value)

        vm.onTestsEvent(TestsEvent.OnUpdateSummaryScore("userId", answers, isAstTest = true))
        advanceUntilIdle()

        assertEquals(10, vm.summaryScore.value)
    }

    // ── OnInsertNewMetrics — усреднение ПСВ ────────────────────────────────

    @Test
    fun `три одинаковых значения ПСВ — среднее равно каждому из них`() = runTest {
        val vm = createViewModel()
        vm.onTestsEvent(TestsEvent.OnInsertNewMetrics("userId", 300f, 300f, 300f))
        advanceUntilIdle()

        coVerify(exactly = 1) { insertMetricsUseCase("userId", 300f, any()) }
    }

    @Test
    fun `разные значения ПСВ — вычисляется среднее арифметическое`() = runTest {
        val vm = createViewModel()
        // (100 + 200 + 300) / 3 = 200
        vm.onTestsEvent(TestsEvent.OnInsertNewMetrics("userId", 100f, 200f, 300f))
        advanceUntilIdle()

        coVerify(exactly = 1) { insertMetricsUseCase("userId", 200f, any()) }
    }

    @Test
    fun `нулевые значения ПСВ — среднее равно нулю`() = runTest {
        val vm = createViewModel()
        vm.onTestsEvent(TestsEvent.OnInsertNewMetrics("userId", 0f, 0f, 0f))
        advanceUntilIdle()

        coVerify(exactly = 1) { insertMetricsUseCase("userId", 0f, any()) }
    }

    // ── OnUpdateNewMetrics — insert vs update ─────────────────────────────

    @Test
    fun `нет записи на сегодня — вызывается insertMetricsUseCase`() = runTest {
        coEvery { getAllMetricsWithDateUseCase(LocalDate.now()) } returns emptyList()

        val vm = createViewModel()
        vm.onTestsEvent(TestsEvent.OnUpdateNewMetrics("userId", 300f, 300f, 300f))
        advanceUntilIdle()

        coVerify(exactly = 1) { insertMetricsUseCase("userId", 300f, any()) }
        coVerify(exactly = 0) { updateMetricsUseCase(any(), any(), any()) }
    }

    @Test
    fun `запись на сегодня уже есть — вызывается updateMetricsUseCase`() = runTest {
        coEvery { getAllMetricsWithDateUseCase(LocalDate.now()) } returns listOf(
            MetricsItem(id = 1, metrics = 280f, date = LocalDate.now())
        )

        val vm = createViewModel()
        vm.onTestsEvent(TestsEvent.OnUpdateNewMetrics("userId", 300f, 300f, 300f))
        advanceUntilIdle()

        coVerify(exactly = 1) { updateMetricsUseCase("userId", 300f, any()) }
        coVerify(exactly = 0) { insertMetricsUseCase(any(), any(), any()) }
    }
}
