package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NeonTycoonColorScheme = darkColorScheme(
    primary = NeonGreenPrimary,
    onPrimary = Color(0xFF070B0E),
    primaryContainer = NeonGreenSurface,
    onPrimaryContainer = NeonGreenBright,
    secondary = AccentCyan,
    onSecondary = Color(0xFF070B0E),
    background = DarkBgBase,
    onBackground = TextPrimary,
    surface = DarkBgSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkBgCard,
    onSurfaceVariant = TextSecondary,
    error = AccentDanger,
    onError = Color.White
)

@Composable
fun MoneyTycoonTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NeonTycoonColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MoneyTycoonTheme(content = content)
}
