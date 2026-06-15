package com.pocketflow.app

import com.pocketflow.app.data.DateRange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Unit tests for the user-selectable [DateRange] presets that drive the Reports
 * and Dashboard period filters. Verifies the boundaries are inclusive and that
 * each preset spans exactly the documented window.
 */
class DateRangeTest {

    @Test
    fun contains_isInclusiveOfBothEnds() {
        val range = DateRange(
            from  = LocalDate.of(2026, 3, 1),
            to    = LocalDate.of(2026, 3, 31),
            label = "March"
        )
        assertTrue(range.contains(LocalDate.of(2026, 3, 1)))   // first day
        assertTrue(range.contains(LocalDate.of(2026, 3, 31)))  // last day
        assertTrue(range.contains(LocalDate.of(2026, 3, 15)))  // middle
    }

    @Test
    fun contains_excludesDatesOutsideWindow() {
        val range = DateRange(
            from  = LocalDate.of(2026, 3, 1),
            to    = LocalDate.of(2026, 3, 31),
            label = "March"
        )
        assertFalse(range.contains(LocalDate.of(2026, 2, 28)))
        assertFalse(range.contains(LocalDate.of(2026, 4, 1)))
    }

    @Test
    fun last7Days_spansSevenInclusiveDays() {
        val range = DateRange.last7Days()
        val today = LocalDate.now()
        assertEquals(today, range.to)
        assertEquals(today.minusDays(6), range.from)
        assertTrue(range.contains(today))
        assertTrue(range.contains(today.minusDays(6)))
        assertFalse(range.contains(today.minusDays(7)))
    }

    @Test
    fun last30Days_spansThirtyInclusiveDays() {
        val range = DateRange.last30Days()
        val today = LocalDate.now()
        assertEquals(today, range.to)
        assertEquals(today.minusDays(29), range.from)
        assertFalse(range.contains(today.minusDays(30)))
    }

    @Test
    fun thisMonth_coversFirstToLastOfCurrentMonth() {
        val range = DateRange.thisMonth()
        val today = LocalDate.now()
        assertEquals(today.withDayOfMonth(1), range.from)
        assertEquals(today.withDayOfMonth(today.lengthOfMonth()), range.to)
        assertTrue(range.contains(today))
    }
}