package com.example.aesculapius.database

import com.example.aesculapius.data.CurrentMedicineType
import com.example.aesculapius.ui.tests.MetricsItem
import com.example.aesculapius.ui.tests.ScoreItem
import com.example.aesculapius.ui.therapy.MedicineItem
import com.example.aesculapius.ui.therapy.MedicineWithDoses
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/** [AesculapiusRepository] репозиторий для Room */
@Singleton
class AesculapiusRepository @Inject constructor(private val itemDAO: ItemDAO) {
    suspend fun insertMedicineItem(
        medicineType: CurrentMedicineType,
        name: String,
        undername: String,
        dose: String,
        frequency: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ) {
        val currentMaxMedicineId = if (itemDAO.getRowAmount() > 0) itemDAO.getMaxMedicineId() else 0
        itemDAO.insertMedicineItem(
            idMedicine = currentMaxMedicineId + 1,
            medicineType = medicineType.name,
            name = name,
            undername = undername,
            dose = dose,
            frequency = frequency,
            startDate = startDate,
            endDate = endDate
        )

        when (medicineType) {
            CurrentMedicineType.Tablets -> {
                var currentDate = startDate
                while (currentDate != endDate) {
                    itemDAO.insertNewDose(
                        dosesAmount = "1 таблетка",
                        date = currentDate,
                        isSkipped = false,
                        isAccepted = false,
                        medicineId = currentMaxMedicineId + 1,
                        isMorning = false
                    )
                    currentDate = currentDate.plusDays(1)
                }
            }

            CurrentMedicineType.Aerosol -> {
                var currentDate = startDate
                while (currentDate != endDate) {
                    when (frequency) {
                        "По 1 дозе 2 раза в сутки" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = false
                            )
                        }
                        "По 2 дозы 2 раза в сутки" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = false
                            )
                        }
                        "По 1 дозе утром и 2 дозы вечером" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = false
                            )
                        }
                        "По 2 дозы утром и 1 дозе вечером" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = false
                            )
                        }
                    }
                    currentDate = currentDate.plusDays(1)
                }
            }

            CurrentMedicineType.Powder -> {
                var currentDate = startDate
                while (currentDate != endDate) {
                    when(frequency) {
                        "По 1 дозе 1 раз в сутки" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = true
                            )
                        }
                        "По 1 дозе 2 раза в сутки" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = false
                            )
                        }
                        "По 2 дозы 2 раза в сутки" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = false
                            )
                        }
                        "По 1 дозе утром и 2 дозы вечером" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = false
                            )
                        }
                        "По 2 дозы утром и 1 дозе вечером" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = currentMaxMedicineId + 1,
                                isMorning = false
                            )
                        }
                    }
                    currentDate = currentDate.plusDays(1)
                }
            }
        }
    }

    suspend fun updateMedicineItem(medicineId: Int, frequency: String, dose: String, medicineType: CurrentMedicineType, startDate: LocalDate, endDate: LocalDate) {
        itemDAO.deleteAllDosesWithMedicineId(medicineId)
        itemDAO.updateMedicineItem(medicineId, dose)
        when (medicineType) {
            CurrentMedicineType.Tablets -> {
                var currentDate = startDate
                while (currentDate != endDate) {
                    itemDAO.insertNewDose(
                        dosesAmount = "1 таблетка",
                        date = currentDate,
                        isSkipped = false,
                        isAccepted = false,
                        medicineId = medicineId,
                        isMorning = false
                    )
                    currentDate = currentDate.plusDays(1) // Переход к следующей дате
                }
            }

            CurrentMedicineType.Aerosol -> {
                var currentDate = startDate
                while (currentDate != endDate) {
                    when (frequency) {
                        "По 1 дозе 2 раза в сутки" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = false
                            )
                        }
                        "По 2 дозы 2 раза в сутки" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = false
                            )
                        }
                        "По 1 дозе утром 2 вечером" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = false
                            )
                        }
                        "2 дозы утром 1 вечером" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = false
                            )
                        }
                    }
                    currentDate = currentDate.plusDays(1) // Переход к следующей дате
                }
            }

            CurrentMedicineType.Powder -> {
                var currentDate = startDate
                while (currentDate != endDate) {
                    when(frequency) {
                        "По 1 дозе 1 раз в сутки" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = true
                            )
                        }
                        "По 1 дозе 2 раза в сутки" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = false
                            )
                        }
                        "По 2 дозы 2 раза в сутки" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = false
                            )
                        }
                        "По 1 дозе утром 2 вечером" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = false
                            )
                        }
                        "2 дозы утром 1 вечером" -> {
                            itemDAO.insertNewDose(
                                dosesAmount = "2 дозы",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = true
                            )
                            itemDAO.insertNewDose(
                                dosesAmount = "1 доза",
                                date = currentDate,
                                isSkipped = false,
                                isAccepted = false,
                                medicineId = medicineId,
                                isMorning = false
                            )
                        }
                    }
                    currentDate = currentDate.plusDays(1) // Переход к следующей дате
                }
            }
        }
    }

    suspend fun acceptMedicine(doseId: Int) {
        itemDAO.acceptMedicine(doseId)
    }

    suspend fun skipMedicine(doseId: Int) {
        itemDAO.skipMedicine(doseId)
    }

    suspend fun deleteMedicineItem(medicineId: Int) {
        itemDAO.deleteMedicineItem(medicineId)
        itemDAO.deleteAllDosesWithMedicineId(medicineId)
    }

    suspend fun getMedicinesOnCurrentDate(currentDate: LocalDate): List<MedicineWithDoses> {
        return itemDAO.getMedicinesOnCurrentDate(currentDate)
    }

    suspend fun getAllMedicinesInPeriod(begin: LocalDate, end: LocalDate): List<MedicineWithDoses> {
        return itemDAO.getAllMedicinesInPeriod(begin, end)
    }

    suspend fun insertAstTestScore(date: LocalDate, score: Int) {
        itemDAO.insertASTTestScore(date, score)
    }

    suspend fun getAllAstResultsInRange(): List<ScoreItem> {
        return itemDAO.getAllAstResultsInRange(LocalDate.now().minusYears(1), LocalDate.now())
    }

    suspend fun getAllAstResults(): List<ScoreItem> {
        return itemDAO.getAllAstResults()
    }

    suspend fun getAllMetrics(): List<MetricsItem> {
        return itemDAO.getAllMetrics()
    }

    suspend fun getAllMedicines(): List<MedicineItem> {
        return itemDAO.getAllMedicines()
    }

    fun getAllMedicinesFlow(): Flow<List<MedicineItem>> = itemDAO.getAllMedicinesFlow()

    suspend fun getAllMetricsInRange(startDate: LocalDate, endDate: LocalDate): List<MetricsItem> {
        return itemDAO.getAllMetricsInRange(startDate, endDate)
    }

    suspend fun getLinePointsAmountOnDates(startDate: LocalDate, endDate: LocalDate): Int {
        return itemDAO.getLinePointsAmountOnDates(startDate, endDate)
    }

    suspend fun getColumnPointsAmountOnDates(startDate: LocalDate, endDate: LocalDate): Int {
        return itemDAO.getColumnPointsAmountOnDates(startDate, endDate)
    }

    suspend fun deleteAllMetrics() {
        itemDAO.deleteAllMetrics()
    }

    suspend fun insertMetrics(metrics: Float, date: LocalDate) {
        return itemDAO.insertMetrics(metrics, date)
    }

    suspend fun updateMetrics(metrics: Float, date: LocalDate) {
        return itemDAO.updateMetrics(metrics, date)
    }

    suspend fun getAllMetricsWithDate(date: LocalDate): List<MetricsItem> {
        return itemDAO.getAllMetricsWithDate(date)
    }

    suspend fun getAmountNotAcceptedMedicines(date: LocalDate): Int {
        return itemDAO.getAmountNotAcceptedMedicines(date)
    }
}