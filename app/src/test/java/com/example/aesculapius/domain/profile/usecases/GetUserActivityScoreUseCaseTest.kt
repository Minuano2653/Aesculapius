package com.example.aesculapius.domain.profile.usecases

import com.example.aesculapius.domain.medicine.usecases.GetMedicinesScoreUseCase
import com.example.aesculapius.domain.tests.usecases.GetAllAstResultsUseCase
import com.example.aesculapius.domain.tests.usecases.GetLinePointsAmountUseCase
import com.example.aesculapius.ui.tests.ScoreItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetUserActivityScoreUseCaseTest {

    private lateinit var getMedicinesScoreUseCase: GetMedicinesScoreUseCase
    private lateinit var getAllAstResultsUseCase: GetAllAstResultsUseCase
    private lateinit var getLinePointsAmountUseCase: GetLinePointsAmountUseCase
    private lateinit var useCase: GetUserActivityScoreUseCase

    @Before
    fun setUp() {
        getMedicinesScoreUseCase = mockk()
        getAllAstResultsUseCase = mockk()
        getLinePointsAmountUseCase = mockk()
        useCase = GetUserActivityScoreUseCase(
            getMedicinesScoreUseCase,
            getAllAstResultsUseCase,
            getLinePointsAmountUseCase
        )
    }

    @Test
    fun `возвращает mainScore = -1 при регистрации менее 30 дней назад`() = runTest {
        val registerDate = LocalDate.now().minusDays(10)

        val result = useCase(registerDate)

        assertEquals(-1.0, result.mainScore, 0.001)
        assertEquals(0.0, result.astTestScore, 0.001)
        assertEquals(0.0, result.metricsScore, 0.001)
        assertEquals(0.0, result.medicinesScore, 0.001)

        // Репозитории не должны вызываться
        coVerify(exactly = 0) { getMedicinesScoreUseCase() }
        coVerify(exactly = 0) { getAllAstResultsUseCase() }
        coVerify(exactly = 0) { getLinePointsAmountUseCase(any(), any()) }
    }

    @Test
    fun `возвращает mainScore = -1 при регистрации ровно 29 дней назад`() = runTest {
        val registerDate = LocalDate.now().minusDays(29)

        val result = useCase(registerDate)

        assertEquals(-1.0, result.mainScore, 0.001)
    }

    @Test
    fun `возвращает mainScore = 10 при регистрации 30 дней назад и всех максимальных показателях`() = runTest {
        val registerDate = LocalDate.now().minusDays(30)
        // metricsScore = 30 * 2.0 / 60.0 = 1.0
        coEvery { getLinePointsAmountUseCase(any(), any()) } returns 30
        coEvery { getAllAstResultsUseCase() } returns listOf(
            ScoreItem(id = 1, score = 25, date = LocalDate.now().minusDays(5)) // 25/25 = 1.0
        )
        coEvery { getMedicinesScoreUseCase() } returns 1.0

        val result = useCase(registerDate)

        // (1.0 + 1.0 + 1.0 + 1) * 2.5 = 10.0
        assertEquals(10.0, result.mainScore, 0.001)
        assertEquals(1.0, result.astTestScore, 0.001)
        assertEquals(1.0, result.metricsScore, 0.001)
        assertEquals(1.0, result.medicinesScore, 0.001)
    }

    @Test
    fun `возвращает mainScore = 2_5 когда все показатели нулевые`() = runTest {
        val registerDate = LocalDate.now().minusDays(60)
        coEvery { getLinePointsAmountUseCase(any(), any()) } returns 0
        coEvery { getAllAstResultsUseCase() } returns emptyList()
        coEvery { getMedicinesScoreUseCase() } returns 0.0

        val result = useCase(registerDate)

        // (0 + 0 + 0 + 1) * 2.5 = 2.5
        assertEquals(2.5, result.mainScore, 0.001)
    }

    @Test
    fun `astTestScore = 0 если последний АСТ-тест старше месяца`() = runTest {
        val registerDate = LocalDate.now().minusDays(60)
        coEvery { getLinePointsAmountUseCase(any(), any()) } returns 0
        coEvery { getAllAstResultsUseCase() } returns listOf(
            ScoreItem(id = 1, score = 25, date = LocalDate.now().minusMonths(2)) // устаревший
        )
        coEvery { getMedicinesScoreUseCase() } returns 0.0

        val result = useCase(registerDate)

        assertEquals(0.0, result.astTestScore, 0.001)
        assertEquals(2.5, result.mainScore, 0.001)
    }

    @Test
    fun `metricsScore не обрезается при большом количестве записей`() = runTest {
        val registerDate = LocalDate.now().minusDays(60)
        // 60 * 2.0 / 60.0 = 2.0 — формула не ограничивает сверху
        coEvery { getLinePointsAmountUseCase(any(), any()) } returns 60
        coEvery { getAllAstResultsUseCase() } returns emptyList()
        coEvery { getMedicinesScoreUseCase() } returns 0.0

        val result = useCase(registerDate)

        assertEquals(2.0, result.metricsScore, 0.001)
        // (0 + 2.0 + 0 + 1) * 2.5 = 7.5
        assertEquals(7.5, result.mainScore, 0.001)
    }

    @Test
    fun `astTestScore нормализуется по шкале 25`() = runTest {
        val registerDate = LocalDate.now().minusDays(60)
        coEvery { getLinePointsAmountUseCase(any(), any()) } returns 0
        coEvery { getAllAstResultsUseCase() } returns listOf(
            ScoreItem(id = 1, score = 15, date = LocalDate.now().minusDays(3)) // 15/25 = 0.6
        )
        coEvery { getMedicinesScoreUseCase() } returns 0.0

        val result = useCase(registerDate)

        assertEquals(0.6, result.astTestScore, 0.001)
    }
}
