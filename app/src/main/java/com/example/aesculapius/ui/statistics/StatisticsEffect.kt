package com.example.aesculapius.ui.statistics

import android.content.Intent
import androidx.annotation.StringRes

sealed interface StatisticsEffect {
    data class LaunchPdfChooser(val intent: Intent) : StatisticsEffect
    data class ShowErrorToast(@StringRes val messageRes: Int) : StatisticsEffect
}
