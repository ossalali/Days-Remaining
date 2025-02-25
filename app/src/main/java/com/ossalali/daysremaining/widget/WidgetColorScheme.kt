package com.ossalali.daysremaining.widget

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.glance.color.colorProviders
import androidx.glance.unit.ColorProvider

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF625B71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = Color(0xFF7D5260),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD8E4),
    onTertiaryContainer = Color(0xFF31111D),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC),
    onError = Color.White,
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    inverseOnSurface = Color(0xFFF4EFF4),
    inverseSurface = Color(0xFF313033),
    inversePrimary = Color(0xFFD0BCFF)
)

object WidgetColorScheme {
    val colors = colorProviders(
        primary = ColorProvider(LightColorScheme.primary),
        onPrimary = ColorProvider(LightColorScheme.onPrimary),
        primaryContainer = ColorProvider(LightColorScheme.primaryContainer),
        onPrimaryContainer = ColorProvider(LightColorScheme.onPrimaryContainer),
        secondary = ColorProvider(LightColorScheme.secondary),
        onSecondary = ColorProvider(LightColorScheme.onSecondary),
        secondaryContainer = ColorProvider(LightColorScheme.secondaryContainer),
        onSecondaryContainer = ColorProvider(LightColorScheme.onSecondaryContainer),
        tertiary = ColorProvider(LightColorScheme.tertiary),
        onTertiary = ColorProvider(LightColorScheme.onTertiary),
        tertiaryContainer = ColorProvider(LightColorScheme.tertiaryContainer),
        onTertiaryContainer = ColorProvider(LightColorScheme.onTertiaryContainer),
        error = ColorProvider(LightColorScheme.error),
        errorContainer = ColorProvider(LightColorScheme.errorContainer),
        onError = ColorProvider(LightColorScheme.onError),
        onErrorContainer = ColorProvider(LightColorScheme.onErrorContainer),
        background = ColorProvider(LightColorScheme.background),
        onBackground = ColorProvider(LightColorScheme.onBackground),
        surface = ColorProvider(LightColorScheme.surface),
        onSurface = ColorProvider(LightColorScheme.onSurface),
        surfaceVariant = ColorProvider(LightColorScheme.surfaceVariant),
        onSurfaceVariant = ColorProvider(LightColorScheme.onSurfaceVariant),
        outline = ColorProvider(LightColorScheme.outline),
        inverseOnSurface = ColorProvider(LightColorScheme.inverseOnSurface),
        inverseSurface = ColorProvider(LightColorScheme.inverseSurface),
        inversePrimary = ColorProvider(LightColorScheme.inversePrimary),
        widgetBackground = ColorProvider(LightColorScheme.background)
    )
}