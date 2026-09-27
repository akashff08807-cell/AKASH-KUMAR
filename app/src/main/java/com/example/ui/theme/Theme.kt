package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TikTokColorScheme = darkColorScheme(
    primary = TikTokRed,
    onPrimary = TikTokWhite,
    secondary = TikTokCyan,
    onSecondary = TikTokBlack,
    tertiary = TikTokYellow,
    background = TikTokBlack,
    onBackground = TikTokWhite,
    surface = TikTokDarkSurface,
    onSurface = TikTokWhite,
    surfaceVariant = TikTokCard,
    onSurfaceVariant = TikTokTextSecondary,
    outline = TikTokBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TikTokColorScheme,
        typography = Typography,
        content = content
    )
}
