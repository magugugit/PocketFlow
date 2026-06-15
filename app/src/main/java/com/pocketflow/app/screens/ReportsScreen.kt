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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketflow.app.data.Analytics
import com.pocketflow.app.data.AppState
import com.pocketflow.app.data.SpendingCategory
import com.pocketflow.app.data.TransactionType
import com.pocketflow.app.ui.components.ToggleTab
import com.pocketflow.app.ui.theme.*
import java.time.YearMonth
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(onBack: () -> Unit) {
    var tabIndex by remember { mutableStateOf(0) }  // 0=Spend vs Goals 1=Breakdown 2=Weekly

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

            // Visual display of how well the user is staying between their
            // min/max spending goals over the past month (final POE requirement).
            GoalBandGauge()

            // User-selectable period (assignment requirement)
            PeriodFilterRow()

            // Tab switcher
            ToggleTab(
                options       = listOf("Spend vs Goals", "Breakdown", "Weekly"),
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
                        0 -> CategoryGoalChart()
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

// ─── Spend-vs-Goals tab (bar chart with min/max goal lines) ────────────────────

/**
 * Bar chart of expense spend **per category** over the user-selectable period,
 * with the user's **minimum and maximum monthly goals** drawn as horizontal
 * reference lines across the plot (final POE requirement: "graph showing the
 * amount spent per category over a user-selectable period ... must also display
 * the minimum and maximum goals").
 *
 * Bars and goal lines share a single y-scale so the comparison is honest.
 */
@Composable
private fun CategoryGoalChart() {
    // Spend per category over the currently-selected range (sorted high → low).
    val data = AppState.rangedTransactions
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.category }
        .map { (cat, txs) ->
            SpendingCategory(
                label  = cat,
                amount = txs.sumOf { it.amount },
                color  = txs.first().categoryColor
            )
        }
        .sortedByDescending { it.amount }

    val minGoal = AppState.minMonthlySpend
    val maxGoal = AppState.maxMonthlySpend

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Spending by Category vs Goals — ${AppState.selectedRange.label}",
            fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary
        )
        Text(
            "Min goal R %,.0f  ·  Max goal R %,.0f".format(minGoal, maxGoal),
            fontSize = 12.sp, color = TextSecondary
        )

        if (data.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No expenses in this period.", fontSize = 13.sp, color = TextSecondary)
            }
            return@Column
        }

        // Shared scale: tall enough to show the biggest bar AND the max goal line.
        val largestBar = data.maxOf { it.amount }
        val scaleMax   = max(largestBar, maxGoal).coerceAtLeast(1.0)
        val minFrac    = Analytics.fractionOfRange(minGoal, 0.0, scaleMax)
        val maxFrac    = Analytics.fractionOfRange(maxGoal, 0.0, scaleMax)

        // Plot area: bars (Compose layout) with the two goal lines overlaid (Canvas).
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment     = Alignment.Bottom
            ) {
                data.forEach { cat ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight((cat.amount / scaleMax).toFloat().coerceIn(0.02f, 1f))
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(cat.color)
                    )
                }
            }
            // Min (green) and Max (red) goal lines.
            Canvas(modifier = Modifier.fillMaxSize()) {
                val dashes = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                val yMin   = size.height * (1f - minFrac)
                val yMax   = size.height * (1f - maxFrac)
                drawLine(
                    color = SafeGreen, start = Offset(0f, yMin), end = Offset(size.width, yMin),
                    strokeWidth = 3f, pathEffect = dashes
                )
                drawLine(
                    color = DangerRed, start = Offset(0f, yMax), end = Offset(size.width, yMax),
                    strokeWidth = 3f, pathEffect = dashes
                )
            }
        }

        // Category axis labels (aligned 1:1 with the bars above).
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            data.forEach { cat ->
                Text(
                    cat.label.take(8),
                    fontSize  = 9.sp,
                    color     = TextSecondary,
                    maxLines  = 1,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier  = Modifier.weight(1f)
                )
            }
        }

        // Legend for the goal lines + per-category amounts.
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            GoalLineKey(color = SafeGreen, label = "Min goal")
            GoalLineKey(color = DangerRed, label = "Max goal")
        }
        data.forEach { cat ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(cat.color)
                    )
                    Text(cat.label, fontSize = 13.sp, color = TextPrimary)
                }
                Text("R ${"%,.0f".format(cat.amount)}", fontSize = 13.sp,
                    color = TextSecondary, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun GoalLineKey(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .width(18.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Text(label, fontSize = 11.sp, color = TextSecondary)
    }
}

// ─── Goal-band gauge (past-month adherence to min/max) ─────────────────────────

/**
 * Horizontal gauge that shows, in a single glance, how this calendar month's
 * spending sits inside the user's minimum–maximum goal band:
 *
 *   0 ──[ UNDER | WITHIN | OVER ]── scaleMax
 *
 * A marker is placed at the current month's spend and the whole card is colour-
 * coded green / amber / red via [Analytics.goalBand].
 */
@Composable
private fun GoalBandGauge() {
    val minGoal = AppState.minMonthlySpend
    val maxGoal = AppState.maxMonthlySpend
    val spend   = Analytics.monthSpend(AppState.transactions, YearMonth.now())

    val band = Analytics.goalBand(spend, minGoal, maxGoal)
    val statusColor = when (band) {
        Analytics.GoalBand.UNDER  -> SafeGreen
        Analytics.GoalBand.WITHIN -> WarningYellow
        Analytics.GoalBand.OVER   -> DangerRed
    }
    val statusLabel = when (band) {
        Analytics.GoalBand.UNDER  -> "Under your minimum — saving well"
        Analytics.GoalBand.WITHIN -> "Within your goal band — on track"
        Analytics.GoalBand.OVER   -> "Over your maximum — review spending"
    }

    // Scale the gauge so the max goal sits ~80% across, leaving headroom for overspend.
    val scaleMax  = max(maxGoal * 1.25, spend * 1.1).coerceAtLeast(1.0)
    val minFrac   = Analytics.fractionOfRange(minGoal, 0.0, scaleMax)
    val maxFrac   = Analytics.fractionOfRange(maxGoal, 0.0, scaleMax)
    val spendFrac = Analytics.fractionOfRange(spend, 0.0, scaleMax)

    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("This Month vs Goal Band", fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp, color = TextPrimary)
                Box(
                    modifier = Modifier.size(12.dp).clip(CircleShape).background(statusColor)
                )
            }
            Text(
                "Spent R %,.0f of your R %,.0f–R %,.0f goal".format(spend, minGoal, maxGoal),
                fontSize = 12.sp, color = TextSecondary
            )

            // The gauge track: grey base, amber "within" band, status-coloured spend
            // fill, and tick markers at the min and max goals.
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            ) {
                val h      = size.height
                val w      = size.width
                val radius = h / 2f

                // Grey base track.
                drawRoundRect(
                    color = BorderLight,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
                )
                // "Within band" segment between the min and max goals.
                drawRect(
                    color   = WarningYellow.copy(alpha = 0.40f),
                    topLeft = Offset(w * minFrac, 0f),
                    size    = Size(w * (maxFrac - minFrac).coerceAtLeast(0f), h)
                )
                // Current-month spend fill, coloured by goal-band status.
                drawRoundRect(
                    color = statusColor.copy(alpha = 0.85f),
                    size  = Size(w * spendFrac.coerceAtLeast(0.01f), h),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
                )
                // Min (green) and max (red) goal tick markers.
                drawLine(
                    color = SafeGreen, strokeWidth = 3f,
                    start = Offset(w * minFrac, 0f), end = Offset(w * minFrac, h)
                )
                drawLine(
                    color = DangerRed, strokeWidth = 3f,
                    start = Offset(w * maxFrac, 0f), end = Offset(w * maxFrac, h)
                )
            }
            Text(statusLabel, fontSize = 12.sp, color = statusColor, fontWeight = FontWeight.Medium)
        }
    }
}
