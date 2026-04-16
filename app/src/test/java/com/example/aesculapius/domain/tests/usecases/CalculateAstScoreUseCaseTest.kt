package com.example.aesculapius.domain.tests.usecases

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateAstScoreUseCaseTest {

    private lateinit var useCase: CalculateAstScoreUseCase

    @Before
    fun setUp() {
        useCase = CalculateAstScoreUseCase()
    }

    @Test
    fun `все ответы 0 — score равен количеству вопросов`() {
        // sum=0, size=5 → 0+5=5
        val result = useCase(listOf(0, 0, 0, 0, 0))
        assertEquals(5, result)
    }

    @Test
    fun `все ответы 3 — score равен сумме плюс размер`() {
        // sum=15, size=5 → 15+5=20
        val result = useCase(listOf(3, 3, 3, 3, 3))
        assertEquals(20, result)
    }

    @Test
    fun `смешанные ответы — score суммируется корректно`() {
        // sum=0+1+2+3+0=6, size=5 → 6+5=11
        val result = useCase(listOf(0, 1, 2, 3, 0))
        assertEquals(11, result)
    }

    @Test
    fun `все ответы 1`() {
        // sum=5, size=5 → 5+5=10
        val result = useCase(listOf(1, 1, 1, 1, 1))
        assertEquals(10, result)
    }

    @Test
    fun `максимально возможный score 25`() {
        // sum=20, size=5 → 20+5=25
        val result = useCase(listOf(4, 4, 4, 4, 4))
        assertEquals(25, result)
    }
}
