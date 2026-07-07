package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = EmeraldGreen,
    secondary = LightEmerald,
    tertiary = PaleEmerald,
    background = DarkCanvas,
    surface = DarkSlate,
    onBackground = TextPrimary,
    onSurface = TextPrimary
  )

private val LightColorScheme = DarkColorScheme // Always maintain premium dark theme for luxury branding

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force premium dark theme
  dynamicColor: Boolean = false, // Keep handcrafted color grading consistent
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
