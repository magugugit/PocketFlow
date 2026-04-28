package com.pocketflow.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Light colour scheme — Uber-style monochrome with a single green accent.
 * Buttons / CTAs are pure black, surfaces are white on an off-white background.
 */
private val LightColorScheme = lightColorScheme(
    primary          = UberBlack,
    onPrimary        = UberWhite,
    secondary        = UberDarkGrey,
    onSecondary      = UberWhite,
    background       = UberOffWhite,
    onBackground     = UberBlack,
    surface          = UberWhite,
    onSurface        = UberBlack,
    surfaceVariant   = UberLightGrey,
    onSurfaceVariant = UberDarkGrey,
    outline          = UberBorderGrey,
    error            = UberRed,
    onError          = UberWhite
)

/**
 * Typography — every text role uses Inter so the entire app reads with the
 * same Uber-Move-like character. Weights are mapped to match Uber's hierarchy:
 *   - Bold:      hero numbers, screen titles
 *   - SemiBold:  section headers, button labels
 *   - Medium:    secondary headers, form labels
 *   - Normal:    body text, captions
 */
private val AppTypography: Typography
    get() {
        val base = Typography()
        return Typography(
            displayLarge   = base.displayLarge.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.Bold,     fontSize = 32.sp, letterSpacing = (-0.5).sp),
            displayMedium  = base.displayMedium.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.Bold,     fontSize = 28.sp, letterSpacing = (-0.4).sp),
            displaySmall   = base.displaySmall.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.Bold,     fontSize = 24.sp),
            headlineLarge  = base.headlineLarge.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.Bold,     fontSize = 22.sp),
            headlineMedium = base.headlineMedium.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
            headlineSmall  = base.headlineSmall.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
            titleLarge     = base.titleLarge.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
            titleMedium    = base.titleMedium.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
            titleSmall     = base.titleSmall.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium,   fontSize = 14.sp),
            bodyLarge      = base.bodyLarge.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.Normal,   fontSize = 16.sp),
            bodyMedium     = base.bodyMedium.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.Normal,   fontSize = 14.sp),
            bodySmall      = base.bodySmall.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.Normal,   fontSize = 12.sp),
            labelLarge     = base.labelLarge.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
            labelMedium    = base.labelMedium.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium,   fontSize = 12.sp),
            labelSmall     = base.labelSmall.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium,   fontSize = 11.sp)
        )
    }

@Composable
fun PocketFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Currently light-only; the dark scheme would invert UberBlack/UberWhite.
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = AppTypography,
        content     = content
    )
}

