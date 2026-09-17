package com.mrtada.memorytoolkit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Primary = Color(0xFF5B6CFF)
private val PrimaryDark = Color(0xFF8C9BFF)
private val Background = Color(0xFFF6F7FB)
private val Surface = Color(0xFFFFFFFF)
private val BackgroundDark = Color(0xFF12141C)
private val SurfaceDark = Color(0xFF1B1E2A)

private val LightColors = lightColorScheme(
    primary = Primary,
    background = Background,
    surface = Surface,
    onPrimary = Color.White
)

private val DarkColors = darkColorScheme(
    primary = PrimaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.Black
)

@Composable
fun MemoryToolkitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
