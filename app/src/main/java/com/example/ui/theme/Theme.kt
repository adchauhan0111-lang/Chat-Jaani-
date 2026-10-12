package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val JaaniDarkColorScheme = darkColorScheme(
    primary = WhatsAppGreen,
    onPrimary = Color.White,
    primaryContainer = WhatsAppTeal,
    onPrimaryContainer = Color.White,
    secondary = WhatsAppGreenLight,
    onSecondary = Color.Black,
    secondaryContainer = WhatsAppSurfaceVariant,
    onSecondaryContainer = WhatsAppTextPrimary,
    tertiary = WhatsAppPink,
    onTertiary = Color.White,
    background = WhatsAppDarkBackground,
    onBackground = WhatsAppTextPrimary,
    surface = WhatsAppSurface,
    onSurface = WhatsAppTextPrimary,
    surfaceVariant = WhatsAppSurfaceVariant,
    onSurfaceVariant = WhatsAppTextSecondary,
    outline = WhatsAppDivider
)

@Composable
fun JaaniChatTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JaaniDarkColorScheme,
        typography = Typography,
        content = content
    )
}
