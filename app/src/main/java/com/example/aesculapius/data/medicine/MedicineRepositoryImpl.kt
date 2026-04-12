package com.example.aesculapius.data.medicine

import com.example.aesculapius.data.CurrentMedicineType
import com.example.aesculapius.data.medicine.local.LocalMedicineDataSource
import com.example.aesculapius.data.medicine.remote.RemoteMedicineDataSource
import com.example.aesculapius.domain.medicine.MedicineRepository
import com.example.aesculapius.ui.therapy.MedicineItem
import com.example.aesculapius.ui.therapy.MedicineWithDoses
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicineRepositoryImpl @Inject constructor(
    private val local: LocalMedicineDataSource,
    private val remote: RemoteMedicineDataSource
) : MedicineRepository {

    override suspend fun addMedicine(
        userId: String,
        medicineType: CurrentMedicineType,
        name: String,
        undername: String,
        dose: String,
        frequency: String,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        val newId = local.insertMedicine(medicineType, name, undername, dose, frequency, startDate, endDate)
        if (userId.isNotEmpty()) {
            remote.addMedicine(
                userId, newId,
                MedicineItem(newId, medicineType, name, undername, dose, frequency, startDate, endDate)
            )
        }
    }

    override suspend fun updateMedicine(
        userId: String,
        medicineId: Int,
        frequency: String,
        dose: String,
        medicineType: CurrentMedicineType,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        // Получаем name/undername из Room до обновления
        val existing = local.getMedicineById(medicineId)
        local.updateMedicine(medicineId, frequency, dose, medicineType, startDate, endDate)
        if (userId.isNotEmpty() && existing != null) {
            remote.updateMedicine(
                userId, medicineId,
                MedicineItem(medicineId, medicineType, existing.name, existing.undername, dose, frequency, startDate, endDate)
            )
        }
    }

    override suspend fun deleteMedicine(userId: String, medicineId: Int) {
        local.deleteMedicine(medicineId)
        if (userId.isNotEmpty()) {
            remote.deleteMedicine(userId, medicineId)
        }
    }

    override suspend fun acceptDose(userId: String, doseId: Int) {
        local.acceptDose(doseId)
        if (userId.isNotEmpty()) {
            val dose = local.getDoseById(doseId)
            remote.acceptDose(userId, dose.medicineId, dose.date, dose.isMorning, dose.dosesAmount)
        }
    }

    override suspend fun skipDose(userId: String, doseId: Int) {
        local.skipDose(doseId)
        if (userId.isNotEmpty()) {
            val dose = local.getDoseById(doseId)
            remote.skipDose(userId, dose.medicineId, dose.date, dose.isMorning, dose.dosesAmount)
        }
    }

    override suspend fun getMedicinesForDate(date: LocalDate): List<MedicineWithDoses> =
        local.getMedicinesForDate(date)

    override suspend fun getAllMedicines(): List<MedicineItem> =
        local.getAllMedicines()

    override suspend fun getMedicinesInPeriod(startDate: LocalDate, endDate: LocalDate): List<MedicineWithDoses> =
        local.getMedicinesInPeriod(startDate, endDate)

    override suspend fun getAmountNotAcceptedMedicines(date: LocalDate): Int =
        local.getAmountNotAcceptedMedicines(date)
}
