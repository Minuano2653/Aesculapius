package com.example.aesculapius.ui.tests

sealed interface TestsEvent {
    data class OnInsertNewMetrics(val userId: String, val first: Float, val second: Float, val third: Float) : TestsEvent
    data class OnUpdateNewMetrics(val userId: String, val first: Float, val second: Float, val third: Float) : TestsEvent
    data class OnUpdateSummaryScore(val userId: String, val score: Int, val isAstTest: Boolean) : TestsEvent
}
