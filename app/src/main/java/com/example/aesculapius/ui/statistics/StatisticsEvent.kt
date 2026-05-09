package com.example.aesculapius.ui.statistics

import java.time.LocalDate

sealed interface StatisticsEvent {
    data class OnPeriodSelected(val period: GraphicTypeContent) : StatisticsEvent
    data object OnChartTypeToggled : StatisticsEvent
    data object OnDownloadReportClick : StatisticsEvent
    data class OnLineMarkerMoved(val date: LocalDate) : StatisticsEvent
    data class OnColumnMarkerMoved(val xIndex: Int, val score: Int) : StatisticsEvent
}
