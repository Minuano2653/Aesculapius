package com.example.aesculapius.domain.tests.usecases

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateRecommendationScoreUseCaseTest {

    private lateinit var useCase: CalculateRecommendationScoreUseCase

    @Before
    fun setUp() {
        useCase = CalculateRecommendationScoreUseCase()
    }

    @Test
    fun `все нули — только инверсия Q9 даёт 4 балла`() {
        val result = useCase(listOf(0, 0, 0, 0, 0, 0, 0, 0, 0))
        assertEquals(4, result)
    }

    @Test
    fun `Q5 ненулевое — добавляет 4 балла`() {
        val result = useCase(listOf(0, 0, 0, 0, 1, 0, 0, 0, 0))
        assertEquals(8, result)
    }

    @Test
    fun `Q9 ненулевое — инверсия не даёт 4 балла`() {
        val result = useCase(listOf(0, 0, 0, 0, 0, 0, 0, 0, 1))
        assertEquals(0, result)
    }

    @Test
    fun `Q7 ненулевое — добавляет 4 балла`() {
        val result = useCase(listOf(0, 0, 0, 0, 0, 0, 1, 0, 0))
        assertEquals(8, result)
    }

    @Test
    fun `Q8 ненулевое — добавляет 4 балла`() {
        val result = useCase(listOf(0, 0, 0, 0, 0, 0, 0, 1, 0))
        assertEquals(8, result)
    }

    @Test
    fun `Q1-Q4 суммируются напрямую`() {
        val result = useCase(listOf(1, 2, 3, 0, 0, 0, 0, 0, 0))
        assertEquals(10, result)
    }

    @Test
    fun `максимальный сценарий приверженности`() {
        val result = useCase(listOf(3, 3, 3, 3, 1, 3, 1, 1, 0))
        assertEquals(31, result)
    }
}
