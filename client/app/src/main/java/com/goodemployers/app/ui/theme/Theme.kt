package com.goodemployers.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0D47A1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF2E7D32),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB8F5B1),
    onSecondaryContainer = Color(0xFF002106),
    tertiary = Color(0xFF6A1B9A),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEADDFF),
    onTertiaryContainer = Color(0xFF25005A),
    error = Color(0xFFBA1A1A),
    background = Color(0xFFFDFCFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFDFCFF),
    onSurface = Color(0xFF1A1C1E)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD0E4FF),
    secondary = Color(0xFF9DDC93),
    onSecondary = Color(0xFF003911),
    secondaryContainer = Color(0xFF005321),
    onSecondaryContainer = Color(0xFFB8F5B1),
    tertiary = Color(0xFFD0BCFF),
    onTertiary = Color(0xFF3B0072),
    tertiaryContainer = Color(0xFF531B8D),
    onTertiaryContainer = Color(0xFFEADDFF),
    error = Color(0xFFFFB4AB),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE3E2E6)
)

@Composable
fun GoodEmployersTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

val Typography = MaterialTheme.typography
