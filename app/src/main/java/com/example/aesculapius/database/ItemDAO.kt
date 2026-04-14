package com.example.aesculapius.database

import androidx.room.Dao
import androidx.room.Query
import com.example.aesculapius.ui.tests.MetricsItem
import com.example.aesculapius.ui.tests.RecommendationItem
import com.example.aesculapius.ui.tests.ScoreItem
import com.example.aesculapius.ui.therapy.DoseItem
import com.example.aesculapius.ui.therapy.MedicineItem
import com.example.aesculapius.ui.therapy.MedicineWithDoses
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

// data access object
@Dao
interface ItemDAO {
    @Query("INSERT INTO medicines_items VALUES(:idMedicine, :medicineType, :name, :undername, :dose, :frequency, :startDate, :endDate)")
    suspend fun insertMedicineItem(
        idMedicine: Int,
        medicineType: String,
        name: String,
        undername: String,
        dose: String,
        frequency: String,
        startDate: LocalDate,
        endDate: LocalDate
    )

    @Query("SELECT COUNT(*) FROM dose_items WHERE date = :date AND isAccepted = 0")
    suspend fun getAmountNotAcceptedMedicines(date: LocalDate): Int

    @Query("UPDATE dose_items SET isAccepted = 1 WHERE idDose = :idDose")
    suspend fun acceptMedicine(idDose: Int)

    @Query("UPDATE dose_items SET isSkipped = 1 WHERE idDose = :idDose")
    suspend fun skipMedicine(idDose: Int)

    @Query("SELECT MAX(idMedicine) FROM medicines_items")
    suspend fun getMaxMedicineId(): Int

    @Query("SELECT COUNT(*) FROM medicines_items")
    suspend fun getRowAmount(): Int

    @Query("INSERT INTO dose_items VALUES(NULL, :dosesAmount, :isMorning, :date, :isSkipped, :isAccepted, :medicineId)")
    suspend fun insertNewDose(dosesAmount: String, isMorning: Boolean, date: LocalDate, isSkipped: Boolean, isAccepted: Boolean, medicineId: Int)

    @Query("UPDATE medicines_items SET dose = :dose WHERE idMedicine = :medicineId")
    suspend fun updateMedicineItem(medicineId: Int, dose: String)

    @Query("DELETE from dose_items WHERE (medicineId = :medicineId)")
    suspend fun deleteAllDosesWithMedicineId(medicineId: Int)

    @Query("DELETE from medicines_items WHERE idMedicine = :medicineId")
    suspend fun deleteMedicineItem(medicineId: Int)

    @Query("SELECT * from medicines_items WHERE (startDate <= :currentDate) AND (endDate > :currentDate)")
    suspend fun getMedicinesOnCurrentDate(currentDate: LocalDate): List<MedicineWithDoses>

    @Query("SELECT * from medicines_items")
    suspend fun getAllMedicines(): List<MedicineItem>

    @Query("SELECT * from medicines_items WHERE (endDate >= :begin) AND (startDate <= :end)")
    suspend fun getAllMedicinesInPeriod(begin: LocalDate, end: LocalDate): List<MedicineWithDoses>

    @Query("INSERT INTO score_items VALUES(NULL, :score, :date)")
    suspend fun insertASTTestScore(date: LocalDate, score: Int)

    @Query("SELECT * from score_items WHERE (date <= :endDate) AND (date >= :startDate)")
    suspend fun getAllAstResultsInRange(startDate: LocalDate, endDate: LocalDate): List<ScoreItem>

    @Query("SELECT * from score_items")
    suspend fun getAllAstResults(): List<ScoreItem>

    @Query("INSERT INTO metrics_items VALUES(NULL, ROUND(:metrics, 1), :date)")
    suspend fun insertMetrics(metrics: Float, date: LocalDate)

    @Query("UPDATE metrics_items SET metrics = ROUND((metrics + :metrics) / 2, 1) WHERE date = :date")
    suspend fun updateMetrics(metrics: Float, date: LocalDate)

    @Query("SELECT * from metrics_items WHERE (date <= :endDate) AND (date >= :startDate)")
    suspend fun getAllMetricsInRange(startDate: LocalDate, endDate: LocalDate): List<MetricsItem>

    @Query("SELECT COUNT(*) from metrics_items WHERE (date <= :endDate) AND (date >= :startDate)")
    suspend fun getLinePointsAmountOnDates(startDate: LocalDate, endDate: LocalDate): Int

    @Query("SELECT COUNT(*) from score_items WHERE (date <= :endDate) AND (date >= :startDate)")
    suspend fun getColumnPointsAmountOnDates(startDate: LocalDate, endDate: LocalDate): Int

    @Query("SELECT * from metrics_items")
    suspend fun getAllMetrics(): List<MetricsItem>

    @Query("SELECT * from metrics_items WHERE date = :date")
    suspend fun getAllMetricsWithDate(date: LocalDate): List<MetricsItem>

    @Query("DELETE from metrics_items")
    suspend fun deleteAllMetrics()

    @Query("SELECT * FROM medicines_items WHERE idMedicine = :medicineId LIMIT 1")
    suspend fun getMedicineById(medicineId: Int): MedicineItem?

    @Query("SELECT * FROM dose_items WHERE idDose = :doseId LIMIT 1")
    suspend fun getDoseById(doseId: Int): DoseItem

    @Query("SELECT * FROM dose_items WHERE medicineId = :medicineId AND date = :date AND isMorning = :isMorning LIMIT 1")
    suspend fun getDoseByMedicineIdDateAndMorning(medicineId: Int, date: LocalDate, isMorning: Boolean): DoseItem?

    @Query("INSERT INTO recommendation_items VALUES(NULL, :score, :date)")
    suspend fun insertRecommendationScore(date: LocalDate, score: Int)

    @Query("SELECT * FROM recommendation_items")
    suspend fun getAllRecommendationResults(): List<RecommendationItem>
}