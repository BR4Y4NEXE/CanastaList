package com.example.canastalist.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val VibrantLightColorScheme = lightColorScheme(
    primary = GreenFresh,
    onPrimary = SurfaceLight,
    primaryContainer = GreenFreshLight,
    onPrimaryContainer = GreenFreshDark,
    secondary = OrangeVibrant,
    onSecondary = SurfaceLight,
    secondaryContainer = OrangeVibrantLight,
    onSecondaryContainer = OrangeVibrantDark,
    tertiary = PinkVibrant,
    onTertiary = SurfaceLight,
    tertiaryContainer = PinkVibrantLight,
    onTertiaryContainer = PinkVibrantDark,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = OnSurfaceLight,
    error = Error,
    onError = SurfaceLight,
    outline = GreenFreshDark.copy(alpha = 0.3f)
)

private val VibrantDarkColorScheme = darkColorScheme(
    primary = GreenFreshLight,
    onPrimary = GreenFreshDark,
    primaryContainer = GreenFreshDark,
    onPrimaryContainer = GreenFreshLight,
    secondary = OrangeVibrantLight,
    onSecondary = OrangeVibrantDark,
    secondaryContainer = OrangeVibrantDark,
    onSecondaryContainer = OrangeVibrantLight,
    tertiary = PinkVibrantLight,
    onTertiary = PinkVibrantDark,
    tertiaryContainer = PinkVibrantDark,
    onTertiaryContainer = PinkVibrantLight,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = OnSurfaceDark,
    error = Error,
    onError = SurfaceLight,
    outline = GreenFreshLight.copy(alpha = 0.3f)
)

@Composable
fun CanastaListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> VibrantDarkColorScheme
        else -> VibrantLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
