package com.example.aesculapius.ui.tests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.domain.tests.usecases.CalculateAstScoreUseCase
import com.example.aesculapius.domain.tests.usecases.CalculateRecommendationScoreUseCase
import com.example.aesculapius.domain.tests.usecases.GetAllMetricsWithDateUseCase
import com.example.aesculapius.domain.tests.usecases.InsertMetricsUseCase
import com.example.aesculapius.domain.tests.usecases.SaveAstTestUseCase
import com.example.aesculapius.domain.tests.usecases.SaveRecommendationTestUseCase
import com.example.aesculapius.domain.tests.usecases.UpdateMetricsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val calculateAstScoreUseCase: CalculateAstScoreUseCase,
    private val calculateRecommendationScoreUseCase: CalculateRecommendationScoreUseCase
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
                val score = if (event.isAstTest)
                    calculateAstScoreUseCase(event.answers)
                else
                    calculateRecommendationScoreUseCase(event.answers)
                _summaryScore.value = score
                if (event.isAstTest)
                    saveAstTestUseCase(event.userId, LocalDate.now(), score)
                else
                    saveRecommendationTestUseCase(event.userId, LocalDate.now(), score)
            }
        }
    }

}
