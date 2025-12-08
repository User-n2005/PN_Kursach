package com.example.kursachpr.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Цвета приложения "Агрегатор кружков"
val PrimaryColor = Color(0xFF7A518D)       // Фиолетовый
val SecondaryColor = Color(0xFFC0B2D6)     // Светло-фиолетовый
val AccentColor = Color(0xFFEE7338)        // Оранжевый (акцент)
val BackgroundColor = Color(0xFFFAF8FC)    // Светлый фон
val SurfaceColor = Color(0xFFFFFFFF)       // Белый
val TextPrimary = Color(0xFF1A1A1A)        // Чёрный текст
val TextSecondary = Color(0xFF6B6B6B)      // Серый текст
val CardBackground = Color(0xFF5D4A66)     // Тёмный фиолетовый для карточек

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
    // Пока используем только светлую тему
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}


