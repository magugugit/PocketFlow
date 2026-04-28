package com.pocketflow.app.data

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.pocketflow.app.data.db.AppDatabase
import com.pocketflow.app.data.db.BudgetEntity
import com.pocketflow.app.data.db.GoalEntity
import com.pocketflow.app.data.db.TransactionEntity
import com.pocketflow.app.data.db.UserSettingsEntity
import com.pocketflow.app.ui.theme.BillsYellow
import com.pocketflow.app.ui.theme.EntertainPurple
import com.pocketflow.app.ui.theme.FoodOrange
import com.pocketflow.app.ui.theme.HealthRed
import com.pocketflow.app.ui.theme.ShoppingPink
import com.pocketflow.app.ui.theme.TransportBlue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Single source of truth for app data. Maps Room entities → UI domain models
 * and provides transactional helpers (e.g. record an expense AND bump the
 * matching budget's "spent" total).
 */
class Repository(private val db: AppDatabase) {

    private val txDao        = db.transactionDao()
    private val budgetDao    = db.budgetDao()
    private val goalDao      = db.goalDao()
    private val settingsDao  = db.userSettingsDao()

    // ─── Observations ──────────────────────────────────────────────────────

    fun observeTransactions(): Flow<List<Transaction>> =
        txDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeBudgets(): Flow<List<BudgetCategory>> =
        budgetDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeGoals(): Flow<List<FinancialGoal>> =
        goalDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeSettings(): Flow<UserSettingsEntity?> =
        settingsDao.observe()

    // ─── Mutations ─────────────────────────────────────────────────────────

    suspend fun addExpense(
        category: ExpenseCategory,
        amount: Double,
        description: String,
        date: LocalDate,
        startMinutes: Int?,
        endMinutes: Int?,
        photoUri: String?
    ) {
        require(amount > 0.0) { "Amount must be positive" }
        Log.d(TAG, "addExpense: cat=${category.label} amount=$amount date=$date")

        val tx = TransactionEntity(
            title         = description.ifBlank { category.label },
            amount        = amount,
            type          = TransactionType.EXPENSE.name,
            categoryName  = category.label,
            iconKey       = category.iconKey,
            colorArgb     = category.color.toArgb(),
            dateEpochDay  = date.toEpochDay(),
            startMinutes  = startMinutes,
            endMinutes    = endMinutes,
            description   = description,
            photoUri      = photoUri
        )
        txDao.insert(tx)
        awardXp(5)
    }

    suspend fun addIncome(category: IncomeCategory, amount: Double, description: String, date: LocalDate) {
        require(amount > 0.0) { "Amount must be positive" }
        Log.d(TAG, "addIncome: cat=${category.label} amount=$amount")

        val tx = TransactionEntity(
            title         = description.ifBlank { category.label },
            amount        = amount,
            type          = TransactionType.INCOME.name,
            categoryName  = category.label,
            iconKey       = category.iconKey,
            colorArgb     = category.color.toArgb(),
            dateEpochDay  = date.toEpochDay(),
            description   = description
        )
        txDao.insert(tx)
        awardXp(10)
    }

    suspend fun addBudget(name: String, iconKey: String, color: Color, limit: Double) {
        require(name.isNotBlank()) { "Budget name required" }
        require(limit > 0.0) { "Limit must be positive" }
        budgetDao.upsert(BudgetEntity(name, iconKey, color.toArgb(), limit))
    }

    suspend fun deleteBudget(name: String) = budgetDao.delete(name)

    suspend fun addGoal(title: String, iconKey: String, target: Double, dueDate: String) {
        require(title.isNotBlank()) { "Goal title required" }
        require(target > 0.0) { "Target must be positive" }
        goalDao.insert(
            GoalEntity(
                title = title, iconKey = iconKey, target = target,
                saved = 0.0, dueDate = dueDate.ifBlank { "—" }, xpReward = 50
            )
        )
    }

    suspend fun updateGoal(id: Long, title: String, target: Double, dueDate: String) {
        val current = goalDao.byId(id) ?: return
        goalDao.update(current.copy(title = title, target = target, dueDate = dueDate))
    }

    suspend fun deleteGoal(id: Long) = goalDao.delete(id)

    suspend fun addToGoal(goalId: Long, amount: Double) {
        require(amount > 0.0) { "Amount must be positive" }
        val current = goalDao.byId(goalId) ?: return
        val newSaved = (current.saved + amount).coerceAtMost(current.target)
        val justCompleted = newSaved >= current.target && current.saved < current.target
        goalDao.update(current.copy(saved = newSaved))
        if (justCompleted) awardXp(current.xpReward)
    }

    suspend fun setMonthlyGoals(min: Double, max: Double) {
        require(min >= 0.0 && max >= min) { "max must be >= min, both >= 0" }
        val current = settingsDao.getOnce() ?: UserSettingsEntity()
        settingsDao.upsert(current.copy(minMonthlySpend = min, maxMonthlySpend = max))
    }

    // ─── Helpers ────────────────────────────────────────────────────────────

    private suspend fun awardXp(amount: Int) {
        val current = settingsDao.getOnce() ?: UserSettingsEntity()
        var newXp    = current.userXp + amount
        var newLevel = current.userLevel
        if (newXp >= MockData.NEXT_LEVEL_XP) {
            newLevel += 1
            newXp = 0
        }
        settingsDao.upsert(current.copy(userXp = newXp, userLevel = newLevel))
    }

    // ─── Mapping helpers ───────────────────────────────────────────────────

    private fun TransactionEntity.toDomain(): Transaction = Transaction(
        id            = id,
        title         = title,
        amount        = amount,
        type          = TransactionType.valueOf(type),
        category      = categoryName,
        categoryIcon  = IconRegistry.iconFor(iconKey),
        categoryColor = Color(colorArgb),
        date          = LocalDate.ofEpochDay(dateEpochDay),
        startMinutes  = startMinutes,
        endMinutes    = endMinutes,
        description   = description,
        photoUri      = photoUri
    )

    private fun BudgetEntity.toDomain(): BudgetCategory = BudgetCategory(
        name  = name,
        icon  = IconRegistry.iconFor(iconKey),
        color = Color(colorArgb),
        spent = 0.0,
        limit = limit
    )

    private fun GoalEntity.toDomain(): FinancialGoal = FinancialGoal(
        id       = id,
        title    = title,
        icon     = IconRegistry.iconFor(iconKey),
        target   = target,
        saved    = saved,
        dueDate  = dueDate,
        xpReward = xpReward
    )

    /**
     * Seed the DB on first launch with realistic starter data so the user
     * has something to look at instead of empty screens.
     */
    suspend fun seedIfEmpty() {
        if ((settingsDao.getOnce()) == null) {
            settingsDao.upsert(UserSettingsEntity())
        }
        if (budgetDao.count() == 0) {
            listOf(
                BudgetEntity("Food & Dining", "FOOD", FoodOrange.toArgb(), 4000.0),
                BudgetEntity("Shopping", "SHOPPING", ShoppingPink.toArgb(), 5000.0),
                BudgetEntity("Transportation", "TRANSPORT", TransportBlue.toArgb(), 1500.0),
                BudgetEntity("Bills & Utilities", "BILLS", BillsYellow.toArgb(), 3000.0),
                BudgetEntity("Entertainment", "ENTERTAIN", EntertainPurple.toArgb(), 800.0),
                BudgetEntity("Healthcare", "HEALTH", HealthRed.toArgb(), 600.0)
            ).forEach { budgetDao.upsert(it) }
        }
        if (goalDao.count() == 0) {
            listOf(
                GoalEntity(0, "Emergency Fund",      "BANK",   50000.0,  0.0, "Dec 2026", 50),
                GoalEntity(0, "Holiday to Mauritius","FLIGHT", 25000.0,  0.0, "Jun 2026", 75),
                GoalEntity(0, "New Laptop",          "LAPTOP", 18000.0,  0.0, "Apr 2026", 30)
            ).forEach { goalDao.insert(it) }
        }
    }

    private companion object {
        const val TAG = "PocketFlowRepo"
    }
}
