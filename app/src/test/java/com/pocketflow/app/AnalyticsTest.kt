package com.pocketflow.app

import com.pocketflow.app.data.Analytics
import com.pocketflow.app.data.Analytics.GoalBand
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [Analytics] — the pure calculation layer behind the Reports
 * charts and the min/max goal gauge. These run on the local JVM (no emulator)
 * and are executed by GitHub Actions on every push (`./gradlew testDebugUnitTest`).
 */
class AnalyticsTest {

    // ─── goalBand ──────────────────────────────────────────────────────────

    @Test
    fun goalBand_belowMin_isUnder() {
        assertEquals(GoalBand.UNDER, Analytics.goalBand(spend = 3000.0, min = 5000.0, max = 20000.0))
    }

    @Test
    fun goalBand_exactlyAtMin_isUnder() {
        // The min boundary counts as "under" (still saving well).
        assertEquals(GoalBand.UNDER, Analytics.goalBand(spend = 5000.0, min = 5000.0, max = 20000.0))
    }

    @Test
    fun goalBand_betweenMinAndMax_isWithin() {
        assertEquals(GoalBand.WITHIN, Analytics.goalBand(spend = 12000.0, min = 5000.0, max = 20000.0))
    }

    @Test
    fun goalBand_exactlyAtMax_isWithin() {
        assertEquals(GoalBand.WITHIN, Analytics.goalBand(spend = 20000.0, min = 5000.0, max = 20000.0))
    }

    @Test
    fun goalBand_aboveMax_isOver() {
        assertEquals(GoalBand.OVER, Analytics.goalBand(spend = 25000.0, min = 5000.0, max = 20000.0))
    }

    // ─── levelForXp / xpToNextLevel ────────────────────────────────────────

    @Test
    fun levelForXp_startsAtLevelOne() {
        assertEquals(1, Analytics.levelForXp(0, perLevel = 500))
        assertEquals(1, Analytics.levelForXp(499, perLevel = 500))
    }

    @Test
    fun levelForXp_crossesLevelBoundaries() {
        assertEquals(2, Analytics.levelForXp(500, perLevel = 500))
        assertEquals(3, Analytics.levelForXp(1000, perLevel = 500))
        assertEquals(3, Analytics.levelForXp(1499, perLevel = 500))
    }

    @Test
    fun xpIntoLevel_isRemainder() {
        assertEquals(0, Analytics.xpIntoLevel(500, perLevel = 500))
        assertEquals(250, Analytics.xpIntoLevel(750, perLevel = 500))
    }

    @Test
    fun xpToNextLevel_countsDown() {
        assertEquals(500, Analytics.xpToNextLevel(0, perLevel = 500))
        assertEquals(250, Analytics.xpToNextLevel(250, perLevel = 500))
        assertEquals(500, Analytics.xpToNextLevel(500, perLevel = 500))
    }

    @Test
    fun negativeXp_isTreatedAsZero() {
        assertEquals(1, Analytics.levelForXp(-100, perLevel = 500))
        assertEquals(0, Analytics.xpIntoLevel(-100, perLevel = 500))
    }

    // ─── fractionOfRange ───────────────────────────────────────────────────

    @Test
    fun fractionOfRange_mapsMidpointToHalf() {
        assertEquals(0.5f, Analytics.fractionOfRange(50.0, 0.0, 100.0), 0.0001f)
    }

    @Test
    fun fractionOfRange_clampsBelowAndAbove() {
        assertEquals(0f, Analytics.fractionOfRange(-20.0, 0.0, 100.0), 0.0001f)
        assertEquals(1f, Analytics.fractionOfRange(200.0, 0.0, 100.0), 0.0001f)
    }

    @Test
    fun fractionOfRange_degenerateRangeReturnsZero() {
        assertEquals(0f, Analytics.fractionOfRange(10.0, 50.0, 50.0), 0.0001f)
    }
}