package com.example.aesculapius.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorScheme = lightColorScheme(
    surface = surface,
    primary = primary,
    secondary = secondary,
    background = background,
    onBackground = onBackground,
    onPrimary = onPrimary,
    tertiary = tertiary,
    onTertiary = onTertiary,
    inversePrimary = inversePrimary,
    //scrim = scrim,
    inverseOnSurface = inverseOnSurface,
    inverseSurface = inverseSurface,
    surfaceTint = surfaceTint,
    outlineVariant = outlineVariant,
    onErrorContainer = onErrorContainer,
    onSecondaryContainer = onSecondaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    errorContainer = errorContainer,
    secondaryContainer = secondaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    surfaceVariant = surfaceVariant,
    tertiaryContainer = tertiaryContainer,
    onError = onError,
    primaryContainer = primaryContainer,
    onSecondary = onSecondary,
    outline = outline
)

@Composable
fun AesculapiusTheme(content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(color = background)
    }

    val colorScheme = LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = Shapes(
            small = RoundedCornerShape(10.dp),
            medium = RoundedCornerShape(100.dp),
            large = RoundedCornerShape(28.dp)
        )
    )
}