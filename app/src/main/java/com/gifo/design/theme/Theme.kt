package com.gifo.design.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val DarkColors = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),

    inversePrimary = Color(0xFF0061A4),

    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF3B4858),
    onSecondaryContainer = Color(0xFFD7E3F7),

    tertiary = Color(0xFFD6BEE5),
    onTertiary = Color(0xFF3B2947),
    tertiaryContainer = Color(0xFF523F5F),
    onTertiaryContainer = Color(0xFFF2DAFF),

    background = Color(0xFF101417),
    onBackground = Color(0xFFE1E4E8),

    surface = Color(0xFF101417),
    onSurface = Color(0xFFE1E4E8),
    surfaceVariant = Color(0xFF41484D),
    onSurfaceVariant = Color(0xFFC1C7CE),

    surfaceTint = Color(0xFF9ECAFF),

    inverseSurface = Color(0xFFE1E4E8),
    inverseOnSurface = Color(0xFF2E3133),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color(0xFF8B9198),
    outlineVariant = Color(0xFF41484D),

    scrim = Color(0xFFFFFFFF),

    surfaceBright = Color(0xFF2C2F31),
    surfaceDim = Color(0xFF1A1D1F),
    surfaceContainer = Color(0xFF202325),
    surfaceContainerHigh = Color(0xFF272A2C),
    surfaceContainerHighest = Color(0xFF2E3133),
    surfaceContainerLow = Color(0xFF181A1C),
    surfaceContainerLowest = Color(0xFF111315),
)

val LightColors = lightColorScheme(
    primary = Color(0xFF0061A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),

    inversePrimary = Color(0xFF9ECAFF),

    secondary = Color(0xFF535F70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD7E3F7),
    onSecondaryContainer = Color(0xFF101C2B),

    tertiary = Color(0xFF6B5778),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF2DAFF),
    onTertiaryContainer = Color(0xFF251431),

    background = Color(0xFFFBFCFF),
    onBackground = Color(0xFF191C1E),

    surface = Color(0xFFFBFCFF),
    onSurface = Color(0xFF191C1E),
    surfaceVariant = Color(0xFFDDE3EA),
    onSurfaceVariant = Color(0xFF41484D),

    surfaceTint = Color(0xFF0061A4),

    inverseSurface = Color(0xFF2E3133),
    inverseOnSurface = Color(0xFFF0F0F3),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    outline = Color(0xFF72787E),
    outlineVariant = Color(0xFFC1C7CE),

    scrim = Color(0xFF000000),

    surfaceBright = Color(0xFFFBFCFF),
    surfaceDim = Color(0xFFE3E6EA),
    surfaceContainer = Color(0xFFF4F7FA),
    surfaceContainerHigh = Color(0xFFE9ECF0),
    surfaceContainerHighest = Color(0xFFE1E4E8),
    surfaceContainerLow = Color(0xFFF9FBFD),
    surfaceContainerLowest = Color(0xFFFFFFFF),
)

@Composable
fun GifoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}