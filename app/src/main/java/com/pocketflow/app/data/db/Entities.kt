package com.pocketflow.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted expense / income record. Icon is stored as a string key so we don't
 * have to serialize an ImageVector — we map back to a vector at the UI boundary.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    /** "EXPENSE" or "INCOME" */
    val type: String,
    val categoryName: String,
    val iconKey: String,
    val colorArgb: Int,
    /** epochDay (LocalDate.toEpochDay()) — easy to range-query */
    val dateEpochDay: Long,
    /** minutes from midnight, nullable for incomes that don't track time */
    val startMinutes: Int? = null,
    val endMinutes: Int? = null,
    val description: String = "",
    val photoUri: String? = null
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val name: String,
    val iconKey: String,
    val colorArgb: Int,
    val limit: Double
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val iconKey: String,
    val target: Double,
    val saved: Double,
    val dueDate: String,
    val xpReward: Int
)

/**
 * One row per app install holding the user's monthly min/max spending goal
 * and aggregate XP / level. Single-row pattern via fixed PK.
 */
@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val minMonthlySpend: Double = 5000.0,
    val maxMonthlySpend: Double = 20000.0,
    val userXp: Int = 0,
    val userLevel: Int = 1
)
