package com.example.aesculapius.data.tests

import com.example.aesculapius.data.tests.local.LocalTestDataSource
import com.example.aesculapius.data.tests.remote.RemoteTestDataSource
import com.example.aesculapius.domain.tests.TestRepository
import com.example.aesculapius.ui.tests.MetricsItem
import com.example.aesculapius.ui.tests.RecommendationItem
import com.example.aesculapius.ui.tests.ScoreItem
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestRepositoryImpl @Inject constructor(
    private val local: LocalTestDataSource,
    private val remote: RemoteTestDataSource
) : TestRepository {

    override suspend fun saveAstTest(userId: String, date: LocalDate, score: Int) {
        local.insertAstTest(date, score)
        if (userId.isNotEmpty()) remote.saveAstTest(userId, date, score)
    }

    override suspend fun getAllAstResultsInRange(): List<ScoreItem> =
        local.getAllAstResultsInRange(LocalDate.now().minusYears(1), LocalDate.now())

    override suspend fun getAllAstResults(): List<ScoreItem> =
        local.getAllAstResults()

    override suspend fun getColumnPointsAmountOnDates(startDate: LocalDate, endDate: LocalDate): Int =
        local.getColumnPointsAmountOnDates(startDate, endDate)

    override suspend fun insertMetrics(userId: String, metrics: Float, date: LocalDate) {
        local.insertMetrics(metrics, date)
        if (userId.isNotEmpty()) remote.savePeakFlow(userId, date, metrics)
    }

    override suspend fun updateMetrics(userId: String, metrics: Float, date: LocalDate) {
        local.updateMetrics(metrics, date)
        if (userId.isNotEmpty()) remote.savePeakFlow(userId, date, metrics)
    }

    override suspend fun getAllMetrics(): List<MetricsItem> =
        local.getAllMetrics()

    override suspend fun getAllMetricsInRange(startDate: LocalDate, endDate: LocalDate): List<MetricsItem> =
        local.getAllMetricsInRange(startDate, endDate)

    override suspend fun getLinePointsAmountOnDates(startDate: LocalDate, endDate: LocalDate): Int =
        local.getLinePointsAmountOnDates(startDate, endDate)

    override suspend fun getAllMetricsWithDate(date: LocalDate): List<MetricsItem> =
        local.getAllMetricsWithDate(date)

    override suspend fun deleteAllMetrics() =
        local.deleteAllMetrics()

    override suspend fun saveRecommendationTest(userId: String, date: LocalDate, score: Int) {
        local.insertRecommendationTest(date, score)
        if (userId.isNotEmpty()) remote.saveRecommendationTest(userId, date, score)
    }

    override suspend fun getAllRecommendationResults(): List<RecommendationItem> =
        local.getAllRecommendationResults()
}
