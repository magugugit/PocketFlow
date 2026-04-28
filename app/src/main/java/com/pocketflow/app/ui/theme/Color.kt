package com.pocketflow.app.ui.theme

import androidx.compose.ui.graphics.Color

/* ─── Uber-inspired palette ────────────────────────────────────────────────
 *
 * Uber's design language is intentionally minimal: pure black on near-white,
 * with a single accent green (Uber Eats green) for confirmations and a red
 * for destructive actions. All categories pull from a calm, washed-out scale
 * rather than saturated rainbow colours so the data — not the UI — stands out.
 * ─────────────────────────────────────────────────────────────────────── */

// ── Core monochrome ────────────────────────────────────────────────────────
val UberBlack        = Color(0xFF000000)  // primary text, primary buttons
val UberWhite        = Color(0xFFFFFFFF)  // surfaces / cards
val UberOffWhite     = Color(0xFFF6F6F6)  // app background
val UberLightGrey    = Color(0xFFEEEEEE)  // disabled / inactive chip
val UberBorderGrey   = Color(0xFFE2E2E2)  // borders / dividers
val UberMidGrey      = Color(0xFF757575)  // secondary text
val UberDarkGrey     = Color(0xFF545454)  // bold secondary text
val UberFaintGrey    = Color(0xFFBDBDBD)  // tertiary / placeholder

// ── Accents ────────────────────────────────────────────────────────────────
val UberGreen        = Color(0xFF06C167)  // Uber Eats green — primary CTA / success
val UberGreenDark    = Color(0xFF048A48)
val UberRed          = Color(0xFFE11900)  // destructive / errors
val UberAmber        = Color(0xFFFFC043)  // warnings
val UberBlue         = Color(0xFF276EF1)  // info / link

// ── Semantic aliases (kept under the OLD names so existing code keeps compiling) ─

// Brand
val PrimaryGreen     = UberBlack          // CTAs are now BLACK, like Uber's "Confirm"
val PrimaryTeal      = UberDarkGrey
val GradientStart    = UberBlack
val GradientMid      = Color(0xFF1F1F1F)
val GradientEnd      = Color(0xFF2A2A2A)

// Background / surface
val BackgroundMint   = UberOffWhite
val SurfaceWhite     = UberWhite
val CardBackground   = UberWhite

// Text
val TextPrimary      = UberBlack
val TextSecondary    = UberDarkGrey
val TextLight        = UberMidGrey
val TextOnPrimary    = UberWhite

// Income / expense
val IncomeGreen      = UberGreen
val IncomeBg         = Color(0xFF0B3D26)
val ExpenseOrange    = UberBlack          // expense amounts shown in black, not orange
val ExpenseBg        = UberBlack

// Status (budget chips / sliders)
val SafeGreen        = UberGreen
val WarningYellow    = UberAmber
val DangerRed        = UberRed

// Category accents (muted scale instead of saturated rainbow)
val FoodOrange       = Color(0xFFE87B35)
val ShoppingPink     = Color(0xFFD1487A)
val TransportBlue    = Color(0xFF276EF1)
val BillsYellow      = Color(0xFFE7A615)
val EntertainPurple  = Color(0xFF7356BF)
val HealthRed        = Color(0xFFC62828)
val EducationTeal    = Color(0xFF008489)
val OtherBrown       = Color(0xFF6D4C41)

// Quick-action accent
val BlueAccent        = UberBlue

// Gamification
val XpGold           = UberAmber
val LevelOrange      = UberAmber
val GoalCardStart    = UberBlack
val GoalCardEnd      = Color(0xFF2A2A2A)

// Tabs / borders
val UnselectedTab    = UberLightGrey
val SelectedTab      = UberBlack
val BorderLight      = UberBorderGrey
val DividerColor     = UberBorderGrey
