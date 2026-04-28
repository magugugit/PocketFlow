package com.pocketflow.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Savings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.pocketflow.app.ui.theme.*
import java.time.LocalDate

// ─── Enums ──────────────────────────────────────────────────────────────────

enum class TransactionType { EXPENSE, INCOME }

/**
 * Built-in expense categories. Each has a stable iconKey used for Room persistence.
 */
enum class ExpenseCategory(
    val label: String,
    val iconKey: String,
    val color: Color
) {
    FOOD("Food & Dining", "FOOD", FoodOrange),
    SHOPPING("Shopping", "SHOPPING", ShoppingPink),
    TRANSPORT("Transportation", "TRANSPORT", TransportBlue),
    BILLS("Bills & Utilities", "BILLS", BillsYellow),
    ENTERTAINMENT("Entertainment", "ENTERTAIN", EntertainPurple),
    HEALTHCARE("Healthcare", "HEALTH", HealthRed),
    EDUCATION("Education", "EDUCATION", EducationTeal),
    OTHER("Others", "OTHER", OtherBrown);

    val icon: ImageVector get() = IconRegistry.iconFor(iconKey)
}

enum class IncomeCategory(
    val label: String,
    val iconKey: String,
    val color: Color
) {
    SALARY("Salary", "SALARY", PrimaryGreen),
    FREELANCE("Freelance", "FREELANCE", PrimaryTeal),
    INVESTMENT("Investment", "INVESTMENT", BillsYellow),
    GIFT("Gift", "GIFT", ShoppingPink),
    RENTAL("Rental", "RENTAL", TransportBlue),
    OTHER("Others", "WALLET", OtherBrown);

    val icon: ImageVector get() = IconRegistry.iconFor(iconKey)
}

// ─── Domain models (UI-side, resolved icons) ──────────────────────────────

data class Transaction(
    val id: Long,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val categoryIcon: ImageVector,
    val categoryColor: Color,
    val date: LocalDate,
    val startMinutes: Int? = null,
    val endMinutes: Int? = null,
    val description: String = "",
    val photoUri: String? = null
) {
    /** Render "Today, 14:32" style date+time string for the list rows. */
    val displayDate: String
        get() {
            val today = LocalDate.now()
            val dayPart = when (date) {
                today                -> "Today"
                today.minusDays(1)   -> "Yesterday"
                else                 -> "%02d %s".format(
                    date.dayOfMonth,
                    date.month.name.take(3).lowercase().replaceFirstChar { it.titlecase() }
                )
            }
            val timePart = startMinutes?.let { "%02d:%02d".format(it / 60, it % 60) }
            return if (timePart != null) "$dayPart, $timePart" else dayPart
        }
}

data class BudgetCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val spent: Double,
    val limit: Double
) {
    val remaining get() = limit - spent
    val progress  get() = (spent / limit).coerceIn(0.0, 1.0).toFloat()
    val status get() = when {
        progress >= 1.0f  -> BudgetStatus.OVER
        progress >= 0.8f  -> BudgetStatus.WARNING
        else              -> BudgetStatus.SAFE
    }
}

enum class BudgetStatus { SAFE, WARNING, OVER }

data class FinancialGoal(
    val id: Long,
    val title: String,
    val icon: ImageVector,
    val target: Double,
    val saved: Double,
    val dueDate: String,
    val xpReward: Int
) {
    val progress get() = (saved / target).coerceIn(0.0, 1.0).toFloat()
    val remaining get() = target - saved
}

data class SpendingCategory(val label: String, val amount: Double, val color: Color)

data class Achievement(val icon: ImageVector, val title: String, val subtitle: String)

/**
 * User-selectable date range for filtering reports / lists. Convenience presets
 * are supplied so the filter chips can drop straight into UI.
 */
data class DateRange(val from: LocalDate, val to: LocalDate, val label: String) {

    fun contains(date: LocalDate): Boolean =
        !date.isBefore(from) && !date.isAfter(to)

    companion object {

        fun thisMonth(): DateRange {
            val today = LocalDate.now()
            val first = today.withDayOfMonth(1)
            val last  = today.withDayOfMonth(today.lengthOfMonth())
            return DateRange(first, last, "This month")
        }

        fun last7Days(): DateRange {
            val today = LocalDate.now()
            return DateRange(today.minusDays(6), today, "Last 7 days")
        }

        fun last30Days(): DateRange {
            val today = LocalDate.now()
            return DateRange(today.minusDays(29), today, "Last 30 days")
        }

        fun allTime(): DateRange =
            DateRange(LocalDate.of(2000, 1, 1), LocalDate.now().plusYears(1), "All time")
    }
}

// ─── Static seed / decorative data (achievements only) ────────────────────

object MockData {

    val achievements = listOf(
        Achievement(Icons.Filled.LocalFireDepartment, "Streak", "7 days"),
        Achievement(Icons.Filled.Savings, "Saver", "First R1000"),
        Achievement(Icons.Filled.Flag, "Goal Set", "4 goals"),
        Achievement(Icons.Filled.Analytics, "Analyst", "5 reports"),
    )

    /** Used by the "+ Add" dialogs when the user creates a custom category/goal. */
    const val DEFAULT_CATEGORY_ICON_KEY: String = "OTHER"
    const val DEFAULT_GOAL_ICON_KEY: String = "FLAG"

    /** Each level requires this many XP. */
    const val NEXT_LEVEL_XP: Int = 500
}
