package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CeifadorDarkColorScheme = darkColorScheme(
    primary = CeifadorRedPrimary,
    onPrimary = Color.White,
    primaryContainer = CeifadorRedMuted,
    onPrimaryContainer = Color(0xFFFFCDD2),
    secondary = CeifadorCyanGlow,
    onSecondary = Color(0xFF032B43),
    secondaryContainer = Color(0xFF0F3A5A),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = CeifadorEmerald,
    onTertiary = Color.White,
    background = CeifadorDarkBg,
    onBackground = TextPrimary,
    surface = CeifadorSurface,
    onSurface = TextPrimary,
    surfaceVariant = CeifadorSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CeifadorBorder,
    outlineVariant = CeifadorBorderActive
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    // Keep consistent pro esports dark theme regardless of OS dynamic theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CeifadorDarkColorScheme,
        typography = Typography,
        content = content
    )
}
