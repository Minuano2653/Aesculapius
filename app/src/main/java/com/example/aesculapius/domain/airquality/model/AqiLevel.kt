package com.example.aesculapius.domain.airquality.model

import androidx.annotation.StringRes
import com.example.aesculapius.R

enum class AqiLevel(
    val raw: Int,
    @StringRes val labelRes: Int,
    @StringRes val descriptionRes: Int,
    val colorArgb: Long
) {
    GOOD(1, R.string.aqi_good, R.string.aqi_good_desc, 0xFF4CAF50),
    FAIR(2, R.string.aqi_fair, R.string.aqi_fair_desc, 0xFFCDDC39),
    MODERATE(3, R.string.aqi_moderate, R.string.aqi_moderate_desc, 0xFFFFC107),
    POOR(4, R.string.aqi_poor, R.string.aqi_poor_desc, 0xFFFF9800),
    VERY_POOR(5, R.string.aqi_very_poor, R.string.aqi_very_poor_desc, 0xFFF44336);

    companion object {
        fun fromAqi(value: Int): AqiLevel = entries.firstOrNull { it.raw == value } ?: GOOD
    }
}
