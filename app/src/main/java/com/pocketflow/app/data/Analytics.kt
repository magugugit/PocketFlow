package com.pocketflow.app.data

import java.time.YearMonth

/**
 * Pure, framework-free calculation helpers for the Reports / Budget screens.
 *
 * Everything in here is deliberately free of Android and Compose types so it can be
 * exercised by fast JVM unit tests (see `app/src/test/.../AnalyticsTest.kt`). The UI
 * layer and the GitHub Actions CI both run against this same logic, which keeps the
 * charts honest: if the maths is wrong, the build goes red.
 */
object Analytics {

    /**
     * Where the user's spending sits relative to their chosen min/max monthly band.
     *
     * - [UNDER]  : spending is at or below the minimum goal (saving well).
     * - [WITHIN] : spending is inside the [min, max] band (on track).
     * - [OVER]   : spending has exceeded the maximum goal (overspending).
     */
    enum class GoalBand { UNDER, WITHIN, OVER }

    /** Classify a spend amount against the min/max spending goals. */
    fun goalBand(spend: Double, min: Double, max: Double): GoalBand = when {
        spend <= min -> GoalBand.UNDER
        spend <= max -> GoalBand.WITHIN
        else         -> GoalBand.OVER
    }

    /**
     * The user's level given their total accumulated XP. Level 1 is the floor, and
     * every [perLevel] XP earns another level.
     */
    fun levelForXp(totalXp: Int, perLevel: Int = MockData.NEXT_LEVEL_XP): Int {
        if (perLevel <= 0) return 1
        return 1 + (totalXp.coerceAtLeast(0) / perLevel)
    }

    /** XP accumulated inside the current level (0 until [perLevel]). */
    fun xpIntoLevel(totalXp: Int, perLevel: Int = MockData.NEXT_LEVEL_XP): Int {
        if (perLevel <= 0) return 0
        return totalXp.coerceAtLeast(0) % perLevel
    }

    /** XP still needed to reach the next level. */
    fun xpToNextLevel(totalXp: Int, perLevel: Int = MockData.NEXT_LEVEL_XP): Int {
        if (perLevel <= 0) return 0
        return perLevel - xpIntoLevel(totalXp, perLevel)
    }

    /**
     * Position of [value] within the inclusive range [start]..[end], clamped to 0f..1f.
     * Used to place markers on the goal-band gauge and goal lines on the bar chart.
     */
    fun fractionOfRange(value: Double, start: Double, end: Double): Float {
        if (end <= start) return 0f
        return ((value - start) / (end - start)).toFloat().coerceIn(0f, 1f)
    }

    /**
     * Total expense spend per category for the transactions that fall inside [range].
     * Returned sorted by amount descending so the chart draws tallest-first.
     */
    fun categoryTotals(
        transactions: List<Transaction>,
        range: DateRange
    ): List<Pair<String, Double>> =
        transactions
            .filter { it.type == TransactionType.EXPENSE && range.contains(it.date) }
            .groupBy { it.category }
            .map { (category, txs) -> category to txs.sumOf { it.amount } }
            .sortedByDescending { it.second }

    /** Total expense spend for the calendar month [month]. */
    fun monthSpend(transactions: List<Transaction>, month: YearMonth): Double =
        transactions
            .filter { it.type == TransactionType.EXPENSE && YearMonth.from(it.date) == month }
            .sumOf { it.amount }
}