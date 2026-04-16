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

    // Логика ответов (9 вопросов):
    // Q1–Q4 (0–3): прямое суммирование значения
    // Q5 (4):  0 → 0,  иначе → 4
    // Q6 (5):  прямое суммирование
    // Q7 (6):  0 → 0,  иначе → 4
    // Q8 (7):  0 → 0,  иначе → 4
    // Q9 (8):  ИНВЕРСИЯ — 0 → 4, иначе → 0

    @Test
    fun `все нули — только инверсия Q9 даёт 4 балла`() {
        // Q1-Q4=0, Q5=0, Q6=0, Q7=0, Q8=0, Q9_inv=4
        val result = useCase(listOf(0, 0, 0, 0, 0, 0, 0, 0, 0))
        assertEquals(4, result)
    }

    @Test
    fun `Q5 ненулевое — добавляет 4 балла`() {
        // Q1-Q4=0, Q5=4 (не 0 → +4), Q6=0, Q7=0, Q8=0, Q9_inv=4 → итого 8
        val result = useCase(listOf(0, 0, 0, 0, 1, 0, 0, 0, 0))
        assertEquals(8, result)
    }

    @Test
    fun `Q9 ненулевое — инверсия не даёт 4 балла`() {
        // Q1-Q4=0, Q5=0, Q6=0, Q7=0, Q8=0, Q9_inv=0 (ненулевой ответ) → итого 0
        val result = useCase(listOf(0, 0, 0, 0, 0, 0, 0, 0, 1))
        assertEquals(0, result)
    }

    @Test
    fun `Q7 ненулевое — добавляет 4 балла`() {
        // Q1-Q4=0, Q5=0, Q6=0, Q7=4, Q8=0, Q9_inv=4 → итого 8
        val result = useCase(listOf(0, 0, 0, 0, 0, 0, 1, 0, 0))
        assertEquals(8, result)
    }

    @Test
    fun `Q8 ненулевое — добавляет 4 балла`() {
        // Q1-Q4=0, Q5=0, Q6=0, Q7=0, Q8=4, Q9_inv=4 → итого 8
        val result = useCase(listOf(0, 0, 0, 0, 0, 0, 0, 1, 0))
        assertEquals(8, result)
    }

    @Test
    fun `Q1-Q4 суммируются напрямую`() {
        // Q1=1, Q2=2, Q3=3, Q4=0 → сумма=6; Q5-Q8=0; Q9_inv=4 → итого 10
        val result = useCase(listOf(1, 2, 3, 0, 0, 0, 0, 0, 0))
        assertEquals(10, result)
    }

    @Test
    fun `максимальный сценарий приверженности`() {
        // Q1-Q4=3+3+3+3=12, Q5=4, Q6=3, Q7=4, Q8=4, Q9_inv=4 → итого 31
        val result = useCase(listOf(3, 3, 3, 3, 1, 3, 1, 1, 0))
        assertEquals(31, result)
    }
}
