package com.pocketflow.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketflow.app.data.AppState
import com.pocketflow.app.data.BudgetCategory
import com.pocketflow.app.data.BudgetStatus
import com.pocketflow.app.data.MockData
import com.pocketflow.app.ui.components.*
import com.pocketflow.app.ui.theme.*
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(onBack: () -> Unit) {
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddBudgetDialog(
            onDismiss = { showAddDialog = false },
            onAdd     = { name, limit ->
                AppState.addBudget(name, MockData.DEFAULT_CATEGORY_ICON_KEY, AppState.defaultBudgetColor, limit)
                showAddDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Budget", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = TextPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Filled.Add, "Add budget", tint = PrimaryTeal)
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

            // Min / Max monthly spending goal (uses Slider — Compose's SeekBar)
            MonthlyGoalCard()

            // Total budget card
            Card(
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(GradientStart, GradientEnd)),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.Top
                        ) {
                            Column {
                                Text("Total Budget", fontSize = 13.sp, color = Color.White.copy(0.8f))
                                Text(
                                    "R ${"%.2f".format(AppState.totalBudget)}",
                                    fontSize   = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = Color.White
                                )
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(0.2f))
                            ) {
                                Icon(
                                    imageVector        = Icons.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint               = Color.White,
                                    modifier           = Modifier.size(22.dp)
                                )
                            }
                        }

                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Spent: R ${"%.2f".format(AppState.budgetSpent)}",
                                fontSize = 12.sp,
                                color    = Color.White.copy(0.9f)
                            )
                            Text(
                                "Remaining: R ${"%.2f".format(AppState.totalBudget - AppState.budgetSpent)}",
                                fontSize = 12.sp,
                                color    = Color.White.copy(0.9f)
                            )
                        }

                        AnimatedProgressBar(
                            progress        = if (AppState.totalBudget > 0)
                                (AppState.budgetSpent / AppState.totalBudget).toFloat().coerceIn(0f, 1f)
                            else 0f,
                            color           = Color.White,
                            backgroundColor = Color.White.copy(0.3f),
                            height          = 8
                        )
                    }
                }
            }

            // Legend
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LegendDot(color = SafeGreen,     label = "Safe")
                LegendDot(color = WarningYellow, label = "Warning")
                LegendDot(color = DangerRed,     label = "Over")
            }

            // Category cards
            Text("Categories", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextPrimary)

            if (AppState.budgets.isEmpty()) {
                Text(
                    "No budgets yet — tap + to create one.",
                    fontSize = 13.sp,
                    color    = TextSecondary
                )
            } else {
                AppState.budgets.forEach { cat ->
                    BudgetCategoryCard(
                        cat       = cat,
                        onDelete  = { AppState.removeBudget(cat.name) }
                    )
                }
            }

            // Tip card
            Card(
                shape  = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier          = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Lightbulb,
                        contentDescription = null,
                        tint               = PrimaryGreen,
                        modifier           = Modifier.size(20.dp)
                    )
                    Column {
                        Text("Budget Tip", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = PrimaryGreen)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "You're close to your Shopping budget. Consider reviewing your spending for this week.",
                            fontSize = 12.sp,
                            color    = TextSecondary
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BudgetCategoryCard(cat: BudgetCategory, onDelete: () -> Unit) {
    val statusColor = budgetStatusColor(cat.status)
    Card(
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(cat.color.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector        = cat.icon,
                            contentDescription = cat.name,
                            tint               = cat.color,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(cat.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                        Text(
                            "R ${"%.2f".format(cat.spent)} of R ${"%.2f".format(cat.limit)}",
                            fontSize = 12.sp,
                            color    = TextSecondary
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    if (cat.status == BudgetStatus.OVER) {
                        Text("Over!", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DangerRed)
                    } else {
                        Text(
                            "R ${"%.2f".format(cat.remaining)} left",
                            fontSize = 11.sp,
                            color    = TextSecondary
                        )
                    }
                    TextButton(
                        onClick        = onDelete,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text("Delete", fontSize = 11.sp, color = TextLight)
                    }
                }
            }
            AnimatedProgressBar(progress = cat.progress, color = statusColor, height = 6)
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(label, fontSize = 12.sp, color = TextSecondary)
    }
}

/**
 * Card with two RangeSlider thumbs that let the user set their MIN and MAX
 * monthly spending goals. Persists via AppState.setMonthlyGoals.
 *
 * Includes a coloured indicator showing where the current month's spending sits
 * relative to those bounds: green = at/below min, yellow = between, red = over max.
 */
@Composable
private fun MonthlyGoalCard() {
    val savedMin = AppState.minMonthlySpend.toFloat()
    val savedMax = AppState.maxMonthlySpend.toFloat()
    var range by remember(savedMin, savedMax) {
        mutableStateOf(savedMin..savedMax)
    }
    val totalRange = 0f..50000f
    val current = AppState.thisMonthSpend.toFloat()

    val statusColor = when {
        current <= range.start            -> SafeGreen
        current <= range.endInclusive     -> WarningYellow
        else                              -> DangerRed
    }
    val statusLabel = when {
        current <= range.start        -> "Under min — saving well"
        current <= range.endInclusive -> "Within range"
        else                          -> "Over max — review spending"
    }

    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Monthly spend goal", fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp, color = TextPrimary)
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
            }
            Text(
                "Min R %.0f  ·  Max R %.0f".format(range.start, range.endInclusive),
                fontSize = 13.sp, color = TextSecondary
            )
            // RangeSlider is the Compose equivalent of Android's SeekBar with two thumbs
            RangeSlider(
                value           = range,
                onValueChange   = { range = it.start..max(it.endInclusive, it.start + 100f) },
                onValueChangeFinished = {
                    AppState.setMonthlyGoals(
                        min = range.start.toDouble(),
                        max = range.endInclusive.toDouble()
                    )
                },
                valueRange      = totalRange,
                steps           = 49, // R1000 increments
                colors          = SliderDefaults.colors(
                    thumbColor              = PrimaryGreen,
                    activeTrackColor        = PrimaryGreen,
                    inactiveTrackColor      = BorderLight
                )
            )
            Text(
                "This month spent: R %.2f  ·  $statusLabel".format(current.toDouble()),
                fontSize   = 12.sp,
                color      = statusColor,
                fontWeight = FontWeight.Medium
            )
            // Visual marker for current spend position
            val pct = ((current - totalRange.start) / (totalRange.endInclusive - totalRange.start))
                .coerceIn(0f, 1f)
            AnimatedProgressBar(
                progress        = pct,
                color           = statusColor,
                backgroundColor = BorderLight,
                height          = 6
            )
        }
    }
}

@Composable
private fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, limit: Double) -> Unit
) {
    var name  by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf("") }

    val limitValue = limit.toDoubleOrNull() ?: 0.0
    val canSave    = name.isNotBlank() && limitValue > 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title    = { Text("New Budget", fontWeight = FontWeight.SemiBold) },
        text     = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it },
                    label         = { Text("Category name") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value         = limit,
                    onValueChange = { limit = it.filter { c -> c.isDigit() || c == '.' } },
                    label         = { Text("Monthly limit (R)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = canSave,
                onClick = { onAdd(name.trim(), limitValue) }
            ) { Text("Add", color = PrimaryGreen, fontWeight = FontWeight.SemiBold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}
