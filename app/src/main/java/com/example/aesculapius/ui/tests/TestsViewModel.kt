package com.example.aesculapius.ui.tests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.domain.tests.usecases.GetAllAstResultsUseCase
import com.example.aesculapius.domain.tests.usecases.GetAllMetricsWithDateUseCase
import com.example.aesculapius.domain.tests.usecases.GetLinePointsAmountUseCase
import com.example.aesculapius.domain.tests.usecases.InsertMetricsUseCase
import com.example.aesculapius.domain.tests.usecases.SaveAstTestUseCase
import com.example.aesculapius.domain.tests.usecases.SaveRecommendationTestUseCase
import com.example.aesculapius.domain.tests.usecases.UpdateMetricsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TestsViewModel @Inject constructor(
    private val saveAstTestUseCase: SaveAstTestUseCase,
    private val saveRecommendationTestUseCase: SaveRecommendationTestUseCase,
    private val insertMetricsUseCase: InsertMetricsUseCase,
    private val updateMetricsUseCase: UpdateMetricsUseCase,
    private val getAllMetricsWithDateUseCase: GetAllMetricsWithDateUseCase,
    private val getAllAstResultsUseCase: GetAllAstResultsUseCase,
    private val getLinePointsAmountUseCase: GetLinePointsAmountUseCase
) : ViewModel() {

    private var _summaryScore = MutableStateFlow(0)
    val summaryScore: StateFlow<Int> = _summaryScore

    fun onTestsEvent(event: TestsEvent) = viewModelScope.launch {
        when (event) {
            is TestsEvent.OnInsertNewMetrics -> {
                insertMetricsUseCase(
                    event.userId,
                    (event.first + event.second + event.third) / 3,
                    LocalDate.now()
                )
            }

            is TestsEvent.OnUpdateNewMetrics -> {
                val avg = (event.first + event.second + event.third) / 3
                if (getAllMetricsWithDateUseCase(LocalDate.now()).isEmpty())
                    insertMetricsUseCase(event.userId, avg, LocalDate.now())
                else
                    updateMetricsUseCase(event.userId, avg, LocalDate.now())
            }

            is TestsEvent.OnUpdateSummaryScore -> {
                _summaryScore.value = event.score
                if (event.isAstTest)
                    saveAstTestUseCase(event.userId, LocalDate.now(), event.score)
                else
                    saveRecommendationTestUseCase(event.userId, LocalDate.now(), event.score)
            }
        }
    }

    suspend fun getTestsScore(): Pair<Double, Double> = viewModelScope.async {
        val successMetrics = getLinePointsAmountUseCase(
            LocalDate.now().minusMonths(1), LocalDate.now()
        ) * 2.0 / 60.0

        val astResults = getAllAstResultsUseCase()
        val successScores =
            if (astResults.isEmpty()) 0.0
            else if (astResults.last().date >= LocalDate.now().minusMonths(1))
                astResults.last().score / 25.0
            else 0.0

        Pair(successScores, successMetrics)
    }.await()
}
