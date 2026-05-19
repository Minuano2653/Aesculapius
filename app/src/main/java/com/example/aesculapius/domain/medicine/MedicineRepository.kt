package com.example.aesculapius.domain.medicine

import com.example.aesculapius.data.CurrentMedicineType
import com.example.aesculapius.ui.therapy.MedicineItem
import com.example.aesculapius.ui.therapy.MedicineWithDoses
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MedicineRepository {

    suspend fun addMedicine(
        userId: String,
        medicineType: CurrentMedicineType,
        name: String,
        undername: String,
        dose: String,
        frequency: String,
        startDate: LocalDate,
        endDate: LocalDate
    )

    suspend fun updateMedicine(
        userId: String,
        medicineId: Int,
        frequency: String,
        dose: String,
        medicineType: CurrentMedicineType,
        startDate: LocalDate,
        endDate: LocalDate
    )

    suspend fun deleteMedicine(userId: String, medicineId: Int)

    suspend fun acceptDose(userId: String, doseId: Int)

    suspend fun skipDose(userId: String, doseId: Int)

    suspend fun getMedicinesForDate(date: LocalDate): List<MedicineWithDoses>

    suspend fun getAllMedicines(): List<MedicineItem>

    fun getAllMedicinesFlow(): Flow<List<MedicineItem>>

    suspend fun getMedicinesInPeriod(startDate: LocalDate, endDate: LocalDate): List<MedicineWithDoses>

    suspend fun getAmountNotAcceptedMedicines(date: LocalDate): Int
}
