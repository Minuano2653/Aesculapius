package com.example.aesculapius.ui.doctor.patientdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.domain.patients.usecases.GetPatientAstResultsUseCase
import com.example.aesculapius.domain.patients.usecases.GetPatientColumnPointsAmountUseCase
import com.example.aesculapius.domain.patients.usecases.GetPatientLinePointsAmountUseCase
import com.example.aesculapius.domain.patients.usecases.GetPatientMetricsRangeUseCase
import com.example.aesculapius.ui.doctor.navigation.PatientDetailScreen
import com.example.aesculapius.ui.statistics.GraphicTypes
import com.example.aesculapius.ui.statistics.StatisticsEvent
import com.example.aesculapius.ui.statistics.StatisticsUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PatientStatsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMetricsRange: GetPatientMetricsRangeUseCase,
    private val getAstResults: GetPatientAstResultsUseCase,
    private val getLinePoints: GetPatientLinePointsAmountUseCase,
    private val getColumnPoints: GetPatientColumnPointsAmountUseCase
) : ViewModel() {

    private val patientId: String =
        savedStateHandle.get<String>(PatientDetailScreen.patientIdArg).orEmpty()

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val _chartEntryModelLine = MutableStateFlow(ChartEntryModelProducer())
    val chartEntryModelLine: StateFlow<ChartEntryModelProducer> = _chartEntryModelLine.asStateFlow()

    private val _chartEntryModelColumn = MutableStateFlow(ChartEntryModelProducer())
    val chartEntryModelColumn: StateFlow<ChartEntryModelProducer> = _chartEntryModelColumn.asStateFlow()

    private val _datesForLineChart = MutableStateFlow(mutableListOf<LocalDate>())
    val datesForLineChart: StateFlow<MutableList<LocalDate>> = _datesForLineChart.asStateFlow()

    private val _datesForColumnChart = MutableStateFlow(mutableListOf<LocalDate>())
    val datesForColumnChart: StateFlow<MutableList<LocalDate>> = _datesForColumnChart.asStateFlow()

    init {
        loadChart(_uiState.value.selectedPeriod.graphicTypes)
    }

    fun onEvent(event: StatisticsEvent) {
        when (event) {
            is StatisticsEvent.OnPeriodSelected -> {
                _uiState.update { it.copy(selectedPeriod = event.period) }
                loadChart(event.period.graphicTypes)
            }
            StatisticsEvent.OnChartTypeToggled ->
                _uiState.update { it.copy(isLineChart = !it.isLineChart) }
            StatisticsEvent.OnDownloadReportClick -> Unit
            is StatisticsEvent.OnLineMarkerMoved ->
                _uiState.update { it.copy(dateTextLine = event.date) }
            is StatisticsEvent.OnColumnMarkerMoved -> {
                val dates = _datesForColumnChart.value
                val date = dates.getOrNull(event.xIndex) ?: return
                _uiState.update {
                    it.copy(
                        dateTextColumn = date,
                        pointsAmountText = event.score
                    )
                }
            }
        }
    }

    fun convertToRussian(points: Int): String = when {
        points % 10 == 1 && points != 11 -> "$points балл"
        (points % 10 in 2..4) && points !in 12..14 -> "$points балла"
        else -> "$points баллов"
    }

    private fun loadChart(period: GraphicTypes) {
        if (patientId.isEmpty()) return
        viewModelScope.launch {
            val columnPoints = withContext(Dispatchers.IO) {
                getColumnPoints(patientId, LocalDate.now().minusYears(1), LocalDate.now())
            }
            loadColumnEntries()

            val begin = computeLineBegin(period)
            val isLongPeriod = period == GraphicTypes.ThreeMonths ||
                period == GraphicTypes.HalfYear ||
                period == GraphicTypes.Year

            withContext(Dispatchers.IO) {
                if (isLongPeriod) loadLineEntriesLong(begin, LocalDate.now())
                else loadLineEntriesShort(begin, LocalDate.now())
            }

            val linePoints = withContext(Dispatchers.IO) {
                getLinePoints(patientId, begin, LocalDate.now())
            }

            _uiState.update {
                it.copy(
                    dateBegin = begin,
                    linePointsAmount = linePoints,
                    columnPointsAmount = columnPoints
                )
            }
        }
    }

    private fun computeLineBegin(period: GraphicTypes): LocalDate = when (period) {
        GraphicTypes.Week -> LocalDate.now().minusDays(6)
        GraphicTypes.Month -> LocalDate.now().minusMonths(1)
        GraphicTypes.ThreeMonths -> LocalDate.now().minusMonths(3)
        GraphicTypes.HalfYear -> LocalDate.now().minusMonths(6)
        GraphicTypes.Year -> LocalDate.now().minusYears(1)
    }

    private suspend fun loadColumnEntries() {
        val tempEntries = mutableListOf<FloatEntry>()
        val tempDates = mutableListOf<LocalDate>()
        getAstResults(patientId).forEachIndexed { index, item ->
            tempEntries.add(FloatEntry(index.toFloat(), item.score.toFloat()))
            tempDates.add(item.date)
        }
        _chartEntryModelColumn.value.setEntries(tempEntries)
        _datesForColumnChart.update { tempDates }
    }

    private suspend fun loadLineEntriesShort(startDate: LocalDate, endDate: LocalDate) {
        val tempEntries = mutableListOf<FloatEntry>()
        val tempDates = mutableListOf<LocalDate>()
        getMetricsRange(patientId, startDate, endDate).forEachIndexed { index, item ->
            tempEntries.add(FloatEntry(index.toFloat(), item.metrics))
            tempDates.add(item.date)
        }
        _chartEntryModelLine.value.setEntries(tempEntries)
        _datesForLineChart.update { tempDates }
    }

    private suspend fun loadLineEntriesLong(startDate: LocalDate, endDate: LocalDate) {
        val tempEntries = mutableListOf<FloatEntry>()
        val tempDates = mutableListOf<LocalDate>()
        var tempCount = 0f
        getMetricsRange(patientId, startDate, endDate).forEachIndexed { index, item ->
            tempCount += item.metrics
            if (index % 7 == 6) {
                tempEntries.add(
                    FloatEntry(
                        ((index + 1) / 7 - 1).toFloat(),
                        String.format("%.1f", tempCount / 7).replace(",", ".").toFloat()
                    )
                )
                tempDates.add(item.date.minusDays(6))
                tempCount = 0f
            }
        }
        _chartEntryModelLine.value.setEntries(tempEntries)
        _datesForLineChart.update { tempDates }
    }
}
