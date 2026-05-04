package com.example.aesculapius.ui.diary

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.aesculapius.R

data class SeverityPalette(
    val background: Color,
    val text: Color,
    val border: Color,
    val face: Color
)

@DrawableRes
fun severitySmileRes(level: Int): Int = when (level) {
    0 -> R.drawable.smile_0_none
    1 -> R.drawable.smile_1_mild
    2 -> R.drawable.smile_2_moderate
    3 -> R.drawable.smile_3_severe
    else -> R.drawable.smile_0_none
}

private val none = SeverityPalette(
    background = Color(0x7376CF8A),
    text = Color(0xFF004209),
    border = Color(0x80298646),
    face = Color(0xFF3C6E4B)
)

private val mild = SeverityPalette(
    background = Color(0x73CACC56),
    text = Color(0xFF363600),
    border = Color(0x80787700),
    face = Color(0xFF6E6E32)
)

private val moderate = SeverityPalette(
    background = Color(0x73F7A224),
    text = Color(0xFF5C3100),
    border = Color(0x80A35F00),
    face = Color(0xFFA06941)
)

private val severe = SeverityPalette(
    background = Color(0x66E64343),
    text = Color(0xFF870004),
    border = Color(0x80CC272E),
    face = Color(0xFF9B3C32)
)

fun severityPalette(level: Int): SeverityPalette = when (level) {
    0 -> none
    1 -> mild
    2 -> moderate
    3 -> severe
    else -> none
}
