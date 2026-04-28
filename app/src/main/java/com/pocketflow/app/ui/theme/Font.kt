package com.pocketflow.app.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font as GoogleFontFont
import com.pocketflow.app.R

/**
 * App typography. Uber's brand uses **Uber Move** which is proprietary and not
 * licensed for third-party use, so we ship the closest visual analogue —
 * Inter — via Google Fonts' downloadable-font feature.
 *
 * Inter is a near-identical geometric sans-serif used by many modern Uber-style
 * apps and is free / open-source (SIL OFL).
 */
private val GoogleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

private val InterFont = GoogleFont("Inter")

val InterFontFamily: FontFamily = FontFamily(
    GoogleFontFont(googleFont = InterFont, fontProvider = GoogleFontProvider, weight = FontWeight.Normal),
    GoogleFontFont(googleFont = InterFont, fontProvider = GoogleFontProvider, weight = FontWeight.Medium),
    GoogleFontFont(googleFont = InterFont, fontProvider = GoogleFontProvider, weight = FontWeight.SemiBold),
    GoogleFontFont(googleFont = InterFont, fontProvider = GoogleFontProvider, weight = FontWeight.Bold)
)
