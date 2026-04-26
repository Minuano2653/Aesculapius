package com.example.aesculapius.domain.tests

import com.example.aesculapius.ui.tests.MetricsItem
import com.example.aesculapius.ui.tests.RecommendationItem
import com.example.aesculapius.ui.tests.ScoreItem
import java.time.LocalDate

interface TestRepository {

    // AST
    suspend fun saveAstTest(userId: String, date: LocalDate, score: Int)
    suspend fun getAllAstResultsInRange(): List<ScoreItem>
    suspend fun getAllAstResults(): List<ScoreItem>
    suspend fun getColumnPointsAmountOnDates(startDate: LocalDate, endDate: LocalDate): Int

    // Peak flow
    suspend fun insertMetrics(userId: String, metrics: Float, date: LocalDate)
    suspend fun updateMetrics(userId: String, metrics: Float, date: LocalDate)
    suspend fun getAllMetrics(): List<MetricsItem>
    suspend fun getAllMetricsInRange(startDate: LocalDate, endDate: LocalDate): List<MetricsItem>
    suspend fun getLinePointsAmountOnDates(startDate: LocalDate, endDate: LocalDate): Int
    suspend fun getAllMetricsWithDate(date: LocalDate): List<MetricsItem>
    suspend fun deleteAllMetrics()

    // Recommendation
    suspend fun saveRecommendationTest(userId: String, date: LocalDate, score: Int)
    suspend fun getAllRecommendationResults(): List<RecommendationItem>
}
