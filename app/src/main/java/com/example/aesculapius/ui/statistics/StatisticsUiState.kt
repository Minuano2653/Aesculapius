package com.example.aesculapius.ui.statistics

import java.time.LocalDate

data class StatisticsUiState(
    val selectedPeriod: GraphicTypeContent = GraphicTypeContent(),
    val isLineChart: Boolean = true,
    val isReportLoading: Boolean = false,
    val linePointsAmount: Int = 0,
    val columnPointsAmount: Int = 0,
    val dateBegin: LocalDate = LocalDate.now().minusDays(6),
    val dateTextLine: LocalDate = LocalDate.now(),
    val dateTextColumn: LocalDate = LocalDate.now(),
    val pointsAmountText: Int = -1
)
