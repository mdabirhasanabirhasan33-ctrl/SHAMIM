package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val Short6t9ColorScheme = darkColorScheme(
  primary = CrimsonPrimary,
  onPrimary = TextPrimary,
  primaryContainer = CrimsonSecondary,
  onPrimaryContainer = DarkBackground,
  secondary = AmberGold,
  onSecondary = DarkBackground,
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = DarkBorder,
  error = ErrorRed,
  onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent bold obsidian/crimson branding
  content: @Composable () -> Unit,
) {
  val colorScheme = Short6t9ColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      window?.let {
        it.statusBarColor = DarkBackground.toArgb()
        it.navigationBarColor = DarkBackground.toArgb()
        WindowCompat.getInsetsController(it, view).apply {
          isAppearanceLightStatusBars = false
          isAppearanceLightNavigationBars = false
        }
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
