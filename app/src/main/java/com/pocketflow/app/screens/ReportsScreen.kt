package com.pocketflow.app.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketflow.app.data.AppState
import com.pocketflow.app.data.SpendingCategory
import com.pocketflow.app.data.TransactionType
import com.pocketflow.app.ui.components.ToggleTab
import com.pocketflow.app.ui.theme.*
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(onBack: () -> Unit) {
    var tabIndex by remember { mutableStateOf(1) }  // 0=Trends 1=Categories 2=Weekly

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Reports & Analytics", fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp, color = TextPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    TextButton(onClick = {}) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null,
                            tint = TextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("March", color = TextSecondary, fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundMint)
            )
        },
        containerColor = BackgroundMint
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Income / Expense summary
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    label    = "Income",
                    amount   = "R ${"%,.0f".format(AppState.totalIncome)}",
                    change   = "${AppState.rangedTransactions.count { it.type == TransactionType.INCOME }} entries",
                    positive = true,
                    color    = IncomeGreen,
                    bgColor  = Color(0xFF065F46),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label    = "Expenses",
                    amount   = "R ${"%,.0f".format(AppState.totalExpense)}",
                    change   = "${AppState.rangedTransactions.count { it.type == TransactionType.EXPENSE }} entries",
                    positive = false,
                    color    = Color.White,
                    bgColor  = ExpenseOrange,
                    modifier = Modifier.weight(1f)
                )
            }

            // User-selectable period (assignment requirement)
            PeriodFilterRow()

            // Tab switcher
            ToggleTab(
                options       = listOf("Trends", "Categories", "Weekly"),
                selectedIndex = tabIndex,
                onSelect      = { tabIndex = it },
                modifier      = Modifier.fillMaxWidth()
            )

            // Chart card
            Card(
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (tabIndex) {
                        0 -> TrendsPlaceholder()
                        1 -> CategoriesTab()
                        2 -> WeeklyTab()
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─── Summary card ─────────────────────────────────────────────────────────────

@Composable
private fun SummaryCard(
    label: String, amount: String, change: String,
    positive: Boolean, color: Color, bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    imageVector        = if (positive) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                    contentDescription = null,
                    tint               = color,
                    modifier           = Modifier.size(14.dp)
                )
                Text(label, fontSize = 12.sp, color = color.copy(alpha = 0.85f))
            }
            Text(amount, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(change, fontSize = 11.sp, color = color.copy(alpha = 0.75f))
        }
    }
}

// ─── Categories tab (Pie chart) ────────────────────────────────────────────────

@Composable
private fun CategoriesTab() {
    // Use the user-selectable period from AppState
    val expenses = AppState.rangedTransactions.filter { it.type == TransactionType.EXPENSE }
    val data = expenses
        .groupBy { it.category }
        .map { (cat, txs) ->
            SpendingCategory(
                label  = cat,
                amount = txs.sumOf { it.amount },
                color  = txs.first().categoryColor
            )
        }
        .sortedByDescending { it.amount }
    val total = data.sumOf { it.amount }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Spending by Category — ${AppState.selectedRange.label}",
            fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)

        if (data.isEmpty() || total == 0.0) {
            Text(
                "No expenses in this period.",
                fontSize = 13.sp, color = TextSecondary
            )
            return@Column
        }

        // Donut chart
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            DonutChart(data = data, total = total, modifier = Modifier.size(200.dp))
        }

        // Legend
        data.forEach { cat ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(cat.color)
                    )
                    Text(cat.label, fontSize = 13.sp, color = TextPrimary)
                }
                Text(
                    "R ${"%.0f".format(cat.amount)} (${(cat.amount / total * 100).toInt()}%)",
                    fontSize = 13.sp,
                    color    = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun DonutChart(
    data: List<SpendingCategory>,
    total: Double,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val stroke     = 44f
        val radius     = (min(size.width, size.height) / 2f) - stroke
        val center     = Offset(size.width / 2f, size.height / 2f)
        var startAngle = -90f

        data.forEach { cat ->
            val sweep = ((cat.amount / total) * 360f).toFloat()
            drawArc(
                color      = cat.color,
                startAngle = startAngle,
                sweepAngle = sweep - 2f,
                useCenter  = false,
                topLeft    = Offset(center.x - radius, center.y - radius),
                size       = Size(radius * 2, radius * 2),
                style      = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            startAngle += sweep
        }
    }
}

// ─── Weekly tab (Bar chart) ────────────────────────────────────────────────────

/**
 * Bar chart showing daily expense totals across the last 7 days, computed
 * directly from AppState.transactions (so it's always live + accurate).
 */
@Composable
private fun WeeklyTab() {
    val today = java.time.LocalDate.now()
    val days  = (6 downTo 0).map { today.minusDays(it.toLong()) }

    // Build (label, amount) pairs for the last 7 days
    val data = days.map { day ->
        val total = AppState.transactions
            .filter { it.type == TransactionType.EXPENSE && it.date == day }
            .sumOf { it.amount }
        day.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.titlecase() } to total
    }
    val max = (data.maxOfOrNull { it.second } ?: 0.0).coerceAtLeast(1.0)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Weekly Spending (last 7 days)", fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp, color = TextPrimary)
        Text(
            "Total: R ${"%.2f".format(data.sumOf { it.second })}",
            fontSize = 13.sp, color = TextSecondary
        )
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .height(160.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.Bottom
        ) {
            data.forEach { (day, amount) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Text(
                        if (amount >= 1000) "R%.1fk".format(amount / 1000)
                        else "R%.0f".format(amount),
                        fontSize = 9.sp, color = TextSecondary
                    )
                    Spacer(Modifier.height(2.dp))
                    val barFraction = (amount / max).toFloat()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .fillMaxHeight(barFraction.coerceIn(0.02f, 1f))
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(PrimaryGreen)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(day, fontSize = 11.sp, color = TextSecondary)
                }
            }
        }
    }
}

// ─── Trends placeholder ────────────────────────────────────────────────────────

@Composable
private fun TrendsPlaceholder() {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .height(220.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector        = Icons.Filled.TrendingUp,
            contentDescription = null,
            tint               = TextLight,
            modifier           = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text("Trend charts coming soon", fontSize = 14.sp, color = TextSecondary)
    }
}
