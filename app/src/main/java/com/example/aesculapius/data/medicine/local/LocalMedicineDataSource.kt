package com.example.aesculapius.data.medicine.local

import com.example.aesculapius.data.CurrentMedicineType
import com.example.aesculapius.database.AesculapiusRepository
import com.example.aesculapius.database.ItemDAO
import com.example.aesculapius.ui.therapy.DoseItem
import com.example.aesculapius.ui.therapy.MedicineItem
import com.example.aesculapius.ui.therapy.MedicineWithDoses
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalMedicineDataSource @Inject constructor(
    private val aesculapiusRepository: AesculapiusRepository,
    private val itemDAO: ItemDAO
) {
    /** Вставляет препарат и возвращает сгенерированный id */
    suspend fun insertMedicine(
        medicineType: CurrentMedicineType,
        name: String,
        undername: String,
        dose: String,
        frequency: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Int {
        val newId = if (itemDAO.getRowAmount() > 0) itemDAO.getMaxMedicineId() + 1 else 1
        aesculapiusRepository.insertMedicineItem(medicineType, name, undername, dose, frequency, startDate, endDate)
        return newId
    }

    suspend fun updateMedicine(
        medicineId: Int,
        frequency: String,
        dose: String,
        medicineType: CurrentMedicineType,
        startDate: LocalDate,
        endDate: LocalDate
    ) = aesculapiusRepository.updateMedicineItem(medicineId, frequency, dose, medicineType, startDate, endDate)

    suspend fun deleteMedicine(medicineId: Int) =
        aesculapiusRepository.deleteMedicineItem(medicineId)

    suspend fun acceptDose(doseId: Int) = aesculapiusRepository.acceptMedicine(doseId)

    suspend fun skipDose(doseId: Int) = aesculapiusRepository.skipMedicine(doseId)

    suspend fun getMedicineById(medicineId: Int): MedicineItem? = itemDAO.getMedicineById(medicineId)

    suspend fun getDoseById(doseId: Int): DoseItem = itemDAO.getDoseById(doseId)

    suspend fun getDoseByMedicineIdDateAndMorning(
        medicineId: Int,
        date: LocalDate,
        isMorning: Boolean
    ): DoseItem? = itemDAO.getDoseByMedicineIdDateAndMorning(medicineId, date, isMorning)

    suspend fun getMedicinesForDate(date: LocalDate): List<MedicineWithDoses> =
        aesculapiusRepository.getMedicinesOnCurrentDate(date)

    suspend fun getAllMedicines(): List<MedicineItem> =
        aesculapiusRepository.getAllMedicines()

    suspend fun getMedicinesInPeriod(startDate: LocalDate, endDate: LocalDate): List<MedicineWithDoses> =
        aesculapiusRepository.getAllMedicinesInPeriod(startDate, endDate)

    suspend fun getAmountNotAcceptedMedicines(date: LocalDate): Int =
        aesculapiusRepository.getAmountNotAcceptedMedicines(date)
}
