package com.example.aesculapius.data.tests.local

import com.example.aesculapius.database.ItemDAO
import com.example.aesculapius.ui.tests.MetricsItem
import com.example.aesculapius.ui.tests.RecommendationItem
import com.example.aesculapius.ui.tests.ScoreItem
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalTestDataSource @Inject constructor(private val itemDAO: ItemDAO) {

    suspend fun insertAstTest(date: LocalDate, score: Int) =
        itemDAO.insertASTTestScore(date, score)

    suspend fun getAllAstResultsInRange(startDate: LocalDate, endDate: LocalDate): List<ScoreItem> =
        itemDAO.getAllAstResultsInRange(startDate, endDate)

    suspend fun getAllAstResults(): List<ScoreItem> =
        itemDAO.getAllAstResults()

    suspend fun getColumnPointsAmountOnDates(startDate: LocalDate, endDate: LocalDate): Int =
        itemDAO.getColumnPointsAmountOnDates(startDate, endDate)

    suspend fun insertMetrics(metrics: Float, date: LocalDate) =
        itemDAO.insertMetrics(metrics, date)

    suspend fun updateMetrics(metrics: Float, date: LocalDate) =
        itemDAO.updateMetrics(metrics, date)

    suspend fun getAllMetrics(): List<MetricsItem> =
        itemDAO.getAllMetrics()

    suspend fun getAllMetricsInRange(startDate: LocalDate, endDate: LocalDate): List<MetricsItem> =
        itemDAO.getAllMetricsInRange(startDate, endDate)

    suspend fun getLinePointsAmountOnDates(startDate: LocalDate, endDate: LocalDate): Int =
        itemDAO.getLinePointsAmountOnDates(startDate, endDate)

    suspend fun getAllMetricsWithDate(date: LocalDate): List<MetricsItem> =
        itemDAO.getAllMetricsWithDate(date)

    suspend fun deleteAllMetrics() =
        itemDAO.deleteAllMetrics()

    suspend fun insertRecommendationTest(date: LocalDate, score: Int) =
        itemDAO.insertRecommendationScore(date, score)

    suspend fun getAllRecommendationResults(): List<RecommendationItem> =
        itemDAO.getAllRecommendationResults()
}
