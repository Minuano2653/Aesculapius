package com.example.aesculapius.domain.airquality.model

import androidx.annotation.StringRes
import com.example.aesculapius.R
import com.example.aesculapius.domain.airquality.model.AqiLevel.FAIR
import com.example.aesculapius.domain.airquality.model.AqiLevel.GOOD
import com.example.aesculapius.domain.airquality.model.AqiLevel.MODERATE
import com.example.aesculapius.domain.airquality.model.AqiLevel.POOR
import com.example.aesculapius.domain.airquality.model.AqiLevel.VERY_POOR

enum class Pollutant(
    @StringRes val nameRes: Int,
    @StringRes val descRes: Int,
    val scaleRanges: List<Pair<AqiLevel, String>>
) {
    CO(
        R.string.aqi_co,
        R.string.aqi_co_desc,
        listOf(GOOD to "0–4400", FAIR to "4400–9400", MODERATE to "9400–12400", POOR to "12400–15400", VERY_POOR to "≥15400")
    ),
    NO2(
        R.string.aqi_no2,
        R.string.aqi_no2_desc,
        listOf(GOOD to "0–40", FAIR to "40–70", MODERATE to "70–150", POOR to "150–200", VERY_POOR to "≥200")
    ),
    O3(
        R.string.aqi_o3,
        R.string.aqi_o3_desc,
        listOf(GOOD to "0–60", FAIR to "60–100", MODERATE to "100–140", POOR to "140–180", VERY_POOR to "≥180")
    ),
    SO2(
        R.string.aqi_so2,
        R.string.aqi_so2_desc,
        listOf(GOOD to "0–20", FAIR to "20–80", MODERATE to "80–250", POOR to "250–350", VERY_POOR to "≥350")
    ),
    PM25(
        R.string.aqi_pm25,
        R.string.aqi_pm25_desc,
        listOf(GOOD to "0–10", FAIR to "10–25", MODERATE to "25–50", POOR to "50–75", VERY_POOR to "≥75")
    ),
    PM10(
        R.string.aqi_pm10,
        R.string.aqi_pm10_desc,
        listOf(GOOD to "0–20", FAIR to "20–50", MODERATE to "50–100", POOR to "100–200", VERY_POOR to "≥200")
    );

    fun getLevel(value: Double): AqiLevel = when (this) {
        CO -> when {
            value < 4400 -> GOOD
            value < 9400 -> FAIR
            value < 12400 -> MODERATE
            value < 15400 -> POOR
            else -> VERY_POOR
        }
        NO2 -> when {
            value < 40 -> GOOD
            value < 70 -> FAIR
            value < 150 -> MODERATE
            value < 200 -> POOR
            else -> VERY_POOR
        }
        O3 -> when {
            value < 60 -> GOOD
            value < 100 -> FAIR
            value < 140 -> MODERATE
            value < 180 -> POOR
            else -> VERY_POOR
        }
        SO2 -> when {
            value < 20 -> GOOD
            value < 80 -> FAIR
            value < 250 -> MODERATE
            value < 350 -> POOR
            else -> VERY_POOR
        }
        PM25 -> when {
            value < 10 -> GOOD
            value < 25 -> FAIR
            value < 50 -> MODERATE
            value < 75 -> POOR
            else -> VERY_POOR
        }
        PM10 -> when {
            value < 20 -> GOOD
            value < 50 -> FAIR
            value < 100 -> MODERATE
            value < 200 -> POOR
            else -> VERY_POOR
        }
    }
}
