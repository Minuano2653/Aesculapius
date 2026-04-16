package com.example.aesculapius.domain.medicine.usecases

import com.example.aesculapius.data.CurrentMedicineType
import com.example.aesculapius.domain.medicine.MedicineRepository
import com.example.aesculapius.ui.therapy.DoseItem
import com.example.aesculapius.ui.therapy.MedicineItem
import com.example.aesculapius.ui.therapy.MedicineWithDoses
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetMedicinesScoreUseCaseTest {

    private lateinit var repository: MedicineRepository
    private lateinit var useCase: GetMedicinesScoreUseCase

    private val today: LocalDate = LocalDate.now()
    // Дата внутри окна [now-1month, now]
    private val withinWindow: LocalDate = today.minusDays(15)

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetMedicinesScoreUseCase(repository)
    }

    private fun buildMedicineItem() = MedicineItem(
        idMedicine = 1,
        medicineType = CurrentMedicineType.Aerosol,
        name = "Test Medicine",
        undername = "test",
        dose = "100 мкг",
        frequency = "1 раз в день утром",
        startDate = today.minusMonths(1),
        endDate = today
    )

    private fun buildDose(
        isAccepted: Boolean,
        dosesAmount: String = "1 доза",
        date: LocalDate = withinWindow
    ) = DoseItem(
        idDose = 1,
        dosesAmount = dosesAmount,
        isMorning = true,
        date = date,
        isSkipped = !isAccepted,
        isAccepted = isAccepted,
        medicineId = 1
    )

    private fun medicineWith(doses: List<DoseItem>) =
        MedicineWithDoses(medicine = buildMedicineItem(), doses = doses)

    @Test
    fun `возвращает 0_0 когда нет препаратов`() = runTest {
        coEvery { repository.getMedicinesInPeriod(any(), any()) } returns emptyList()

        val result = useCase()

        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `возвращает 1_0 когда все дозы приняты`() = runTest {
        coEvery { repository.getMedicinesInPeriod(any(), any()) } returns
            listOf(medicineWith(listOf(buildDose(isAccepted = true))))

        val result = useCase()

        assertEquals(1.0, result, 0.001)
    }

    @Test
    fun `возвращает 0_0 когда все дозы пропущены`() = runTest {
        coEvery { repository.getMedicinesInPeriod(any(), any()) } returns
            listOf(medicineWith(listOf(buildDose(isAccepted = false))))

        val result = useCase()

        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `возвращает 0_5 когда половина доз принята`() = runTest {
        val doses = listOf(
            buildDose(isAccepted = true).copy(idDose = 1),
            buildDose(isAccepted = false).copy(idDose = 2)
        )
        coEvery { repository.getMedicinesInPeriod(any(), any()) } returns
            listOf(medicineWith(doses))

        val result = useCase()

        assertEquals(0.5, result, 0.001)
    }

    @Test
    fun `двойная доза принята — считается как 2 из 2`() = runTest {
        // dosesAmount начинается с '2' → acceptedDoses += 2, amountDoses += 2
        val dose = buildDose(isAccepted = true, dosesAmount = "2 дозы")
        coEvery { repository.getMedicinesInPeriod(any(), any()) } returns
            listOf(medicineWith(listOf(dose)))

        val result = useCase()

        assertEquals(1.0, result, 0.001)
    }

    @Test
    fun `дозы вне временного окна не учитываются`() = runTest {
        // Дата 2 месяца назад — вне окна [now-1month, now]
        val dose = buildDose(isAccepted = true, date = today.minusMonths(2))
        coEvery { repository.getMedicinesInPeriod(any(), any()) } returns
            listOf(medicineWith(listOf(dose)))

        val result = useCase()

        // amountDoses == 0 → возвращает 0.0
        assertEquals(0.0, result, 0.001)
    }
}
