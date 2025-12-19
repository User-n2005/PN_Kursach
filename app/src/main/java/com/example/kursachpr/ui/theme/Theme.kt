package com.example.kursachpr.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryColor = Color(0xFF7A518D)
val SecondaryColor = Color(0xFFC0B2D6)
val AccentColor = Color(0xFFEE7338)
val BackgroundColor = Color(0xFFFAF8FC)
val SurfaceColor = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF6B6B6B)
val CardBackground = Color(0xFF5D4A66)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    secondary = SecondaryColor,
    onSecondary = TextPrimary,
    tertiary = AccentColor,
    onTertiary = Color.White,
    background = BackgroundColor,
    onBackground = TextPrimary,
    surface = SurfaceColor,
    onSurface = TextPrimary,
    surfaceVariant = SecondaryColor,
    onSurfaceVariant = TextPrimary
)

@Composable
fun KursachTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
