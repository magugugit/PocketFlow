package com.pocketflow.app.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.pocketflow.app.data.db.AppDatabase
import com.pocketflow.app.data.db.UserSettingsEntity
import com.pocketflow.app.ui.theme.OtherBrown
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

/**
 * Compose-readable application state. Backed by the Room [Repository]; UI
 * screens read these snapshot lists / values directly.
 *
 * `init(context)` MUST be called once before any screen reads the state
 * (we do this from MainActivity).
 */
object AppState {

    /** Flag flips to true the first time [init] completes a DB load. */
    var isReady by mutableStateOf(false)
        private set

    val transactions: SnapshotStateList<Transaction>      = mutableStateListOf()
    val budgets:      SnapshotStateList<BudgetCategory>   = mutableStateListOf()
    val goals:        SnapshotStateList<FinancialGoal>    = mutableStateListOf()

    var userXp           by mutableStateOf(0)
        private set
    var userLevel        by mutableStateOf(1)
        private set
    var minMonthlySpend  by mutableStateOf(5000.0)
        private set
    var maxMonthlySpend  by mutableStateOf(20000.0)
        private set

    /** Date range currently selected for the dashboard / reports filter. */
    var selectedRange by mutableStateOf(DateRange.thisMonth())

    private lateinit var repo: Repository
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /** Should be called once on app startup (before MainActivity setContent). */
    fun init(context: Context) {
        if (::repo.isInitialized) return
        Log.d(TAG, "init: opening Room database")
        val db = AppDatabase.get(context)
        repo = Repository(db)

        scope.launch {
            // Seed first launch
            repo.seedIfEmpty()
            isReady = true
        }
        // Observe all Room flows and mirror into Compose snapshot state
        scope.launch {
            repo.observeTransactions().collect { list ->
                transactions.clear()
                transactions.addAll(list)
                recalcBudgetSpent()
            }
        }
        scope.launch {
            repo.observeBudgets().collect { list ->
                budgets.clear()
                budgets.addAll(list)
                recalcBudgetSpent()
            }
        }
        scope.launch {
            repo.observeGoals().collect { list ->
                goals.clear()
                goals.addAll(list)
            }
        }
        scope.launch {
            repo.observeSettings().collect { s: UserSettingsEntity? ->
                s ?: return@collect
                userXp           = s.userXp
                userLevel        = s.userLevel
                minMonthlySpend  = s.minMonthlySpend
                maxMonthlySpend  = s.maxMonthlySpend
            }
        }
    }

    // ─── Derived totals (use selectedRange) ───────────────────────────────

    val rangedTransactions: List<Transaction>
        get() = transactions.filter { selectedRange.contains(it.date) }

    val totalIncome: Double
        get() = rangedTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }

    val totalExpense: Double
        get() = rangedTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    val totalBalance: Double
        get() = totalIncome - totalExpense

    val totalBudget: Double
        get() = budgets.sumOf { it.limit }

    val budgetSpent: Double
        get() = budgets.sumOf { it.spent }

    val nextLevelXp: Int get() = MockData.NEXT_LEVEL_XP

    val defaultBudgetColor: Color get() = OtherBrown

    /**
     * Total spend across every category for the currently-active month.
     * Used by the BudgetScreen min/max-goal indicator.
     */
    val thisMonthSpend: Double
        get() {
            val now = YearMonth.now()
            return transactions
                .filter { it.type == TransactionType.EXPENSE && YearMonth.from(it.date) == now }
                .sumOf { it.amount }
        }

    // ─── Mutations (delegate to repo) ─────────────────────────────────────

    fun addExpense(
        category: ExpenseCategory,
        amount: Double,
        description: String,
        date: LocalDate,
        startMinutes: Int?,
        endMinutes: Int?,
        photoUri: String?
    ) = scope.launch {
        runCatching {
            repo.addExpense(category, amount, description, date, startMinutes, endMinutes, photoUri)
        }.onFailure { Log.e(TAG, "addExpense failed", it) }
    }

    fun addIncome(category: IncomeCategory, amount: Double, description: String, date: LocalDate) =
        scope.launch {
            runCatching { repo.addIncome(category, amount, description, date) }
                .onFailure { Log.e(TAG, "addIncome failed", it) }
        }

    fun addBudget(name: String, iconKey: String, color: Color, limit: Double) = scope.launch {
        runCatching { repo.addBudget(name, iconKey, color, limit) }
            .onFailure { Log.e(TAG, "addBudget failed", it) }
    }

    fun removeBudget(name: String) = scope.launch { repo.deleteBudget(name) }

    fun addGoal(title: String, iconKey: String, target: Double, dueDate: String) = scope.launch {
        runCatching { repo.addGoal(title, iconKey, target, dueDate) }
            .onFailure { Log.e(TAG, "addGoal failed", it) }
    }

    fun updateGoal(id: Long, title: String, target: Double, dueDate: String) = scope.launch {
        runCatching { repo.updateGoal(id, title, target, dueDate) }
            .onFailure { Log.e(TAG, "updateGoal failed", it) }
    }

    fun removeGoal(id: Long) = scope.launch { repo.deleteGoal(id) }

    fun addToGoal(id: Long, amount: Double) = scope.launch {
        runCatching { repo.addToGoal(id, amount) }
            .onFailure { Log.e(TAG, "addToGoal failed", it) }
    }

    fun setMonthlyGoals(min: Double, max: Double) = scope.launch {
        runCatching { repo.setMonthlyGoals(min, max) }
            .onFailure { Log.e(TAG, "setMonthlyGoals failed", it) }
    }

    /**
     * Recompute "spent" per budget category by summing this-month expenses. We keep
     * this in memory only (not persisted) so it always matches the transactions list.
     */
    private fun recalcBudgetSpent() {
        if (budgets.isEmpty()) return
        val now = YearMonth.now()
        val totals = transactions
            .filter { it.type == TransactionType.EXPENSE && YearMonth.from(it.date) == now }
            .groupBy { it.category }
            .mapValues { (_, txs) -> txs.sumOf { it.amount } }
        for (i in budgets.indices) {
            val b = budgets[i]
            val spent = totals[b.name] ?: 0.0
            if (b.spent != spent) budgets[i] = b.copy(spent = spent)
        }
    }

    private const val TAG = "PocketFlowState"
}
