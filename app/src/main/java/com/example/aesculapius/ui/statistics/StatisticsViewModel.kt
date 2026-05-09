package com.example.aesculapius.ui.statistics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aesculapius.R
import com.example.aesculapius.data.pdf.local.PdfFileWriter
import com.example.aesculapius.data.pdf.local.PdfShareIntents
import com.example.aesculapius.domain.pdf.model.PdfDownloadResult
import com.example.aesculapius.domain.pdf.usecases.CheckPdfServerHealthUseCase
import com.example.aesculapius.domain.pdf.usecases.DownloadStatisticsReportUseCase
import com.example.aesculapius.domain.tests.usecases.GetAllAstResultsInRangeUseCase
import com.example.aesculapius.domain.tests.usecases.GetAllMetricsInRangeUseCase
import com.example.aesculapius.domain.tests.usecases.GetColumnPointsAmountUseCase
import com.example.aesculapius.domain.tests.usecases.GetLinePointsAmountUseCase
import com.google.firebase.auth.FirebaseAuth
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getAllAstResultsInRangeUseCase: GetAllAstResultsInRangeUseCase,
    private val getAllMetricsInRangeUseCase: GetAllMetricsInRangeUseCase,
    private val getLinePointsAmountUseCase: GetLinePointsAmountUseCase,
    private val getColumnPointsAmountUseCase: GetColumnPointsAmountUseCase,
    private val checkPdfServerHealthUseCase: CheckPdfServerHealthUseCase,
    private val downloadStatisticsReportUseCase: DownloadStatisticsReportUseCase,
    private val pdfFileWriter: PdfFileWriter
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val _chartEntryModelColumn = MutableStateFlow(ChartEntryModelProducer())
    val chartEntryModelColumn: StateFlow<ChartEntryModelProducer> = _chartEntryModelColumn.asStateFlow()

    private val _chartEntryModelLine = MutableStateFlow(ChartEntryModelProducer())
    val chartEntryModelLine: StateFlow<ChartEntryModelProducer> = _chartEntryModelLine.asStateFlow()

    private val _datesForLineChart = MutableStateFlow(mutableListOf<LocalDate>())
    val datesForLineChart: StateFlow<MutableList<LocalDate>> = _datesForLineChart.asStateFlow()

    private val _datesForColumnChart = MutableStateFlow(mutableListOf<LocalDate>())
    val datesForColumnChart: StateFlow<MutableList<LocalDate>> = _datesForColumnChart.asStateFlow()

    private val _effects = Channel<StatisticsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        warmUpServer()
        loadChart(_uiState.value.selectedPeriod.graphicTypes)
    }

    fun onEvent(event: StatisticsEvent) {
        when (event) {
            is StatisticsEvent.OnPeriodSelected -> {
                _uiState.update { it.copy(selectedPeriod = event.period) }
                loadChart(event.period.graphicTypes)
            }
            StatisticsEvent.OnChartTypeToggled -> {
                _uiState.update { it.copy(isLineChart = !it.isLineChart) }
            }
            StatisticsEvent.OnDownloadReportClick -> downloadReport()
            is StatisticsEvent.OnLineMarkerMoved -> {
                _uiState.update { it.copy(dateTextLine = event.date) }
            }
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

    private fun warmUpServer() {
        viewModelScope.launch {
            runCatching { checkPdfServerHealthUseCase() }
        }
    }

    private fun loadChart(period: GraphicTypes) {
        viewModelScope.launch {
            val columnPoints = withContext(Dispatchers.IO) {
                getColumnPointsAmountUseCase(LocalDate.now().minusYears(1), LocalDate.now())
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
                getLinePointsAmountUseCase(begin, LocalDate.now())
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
        getAllAstResultsInRangeUseCase().forEachIndexed { index, item ->
            tempEntries.add(FloatEntry(index.toFloat(), item.score.toFloat()))
            tempDates.add(item.date)
        }
        _chartEntryModelColumn.value.setEntries(tempEntries)
        _datesForColumnChart.update { tempDates }
    }

    private suspend fun loadLineEntriesShort(startDate: LocalDate, endDate: LocalDate) {
        val tempEntries = mutableListOf<FloatEntry>()
        val tempDates = mutableListOf<LocalDate>()
        getAllMetricsInRangeUseCase(startDate, endDate).forEachIndexed { index, item ->
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
        getAllMetricsInRangeUseCase(startDate, endDate).forEachIndexed { index, item ->
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

    private fun downloadReport() {
        if (_uiState.value.isReportLoading) return
        _uiState.update { it.copy(isReportLoading = true) }
        viewModelScope.launch {
            val result = downloadStatisticsReportUseCase(psvDays = DEFAULT_PSV_DAYS)
            handleDownloadResult(result)
        }
    }

    private suspend fun handleDownloadResult(result: PdfDownloadResult) {
        when (result) {
            is PdfDownloadResult.Success -> {
                val uri = withContext(Dispatchers.IO) {
                    pdfFileWriter.writeReport(result.bytes)
                }
                _uiState.update { it.copy(isReportLoading = false) }
                _effects.send(StatisticsEffect.LaunchPdfChooser(PdfShareIntents.buildSendIntent(uri)))
            }
            is PdfDownloadResult.Error -> {
                _uiState.update { it.copy(isReportLoading = false) }
                _effects.send(StatisticsEffect.ShowErrorToast(errorMessageRes(result)))
            }
        }
    }

    private fun errorMessageRes(error: PdfDownloadResult.Error): Int = when (error) {
        PdfDownloadResult.Error.Unauthorized -> R.string.report_error_unauthorized
        PdfDownloadResult.Error.UserNotFound -> R.string.report_error_user_not_found
        PdfDownloadResult.Error.InvalidParams -> R.string.report_error_invalid_params
        PdfDownloadResult.Error.Network -> R.string.report_error_network
        is PdfDownloadResult.Error.Unknown -> R.string.report_error_unknown
    }

    private companion object {
        const val DEFAULT_PSV_DAYS = 31
    }
}
