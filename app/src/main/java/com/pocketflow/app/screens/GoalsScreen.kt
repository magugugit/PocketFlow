package com.pocketflow.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import com.pocketflow.app.data.AppState
import com.pocketflow.app.data.FinancialGoal
import com.pocketflow.app.data.MockData
import com.pocketflow.app.ui.components.AnimatedProgressBar
import com.pocketflow.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(onBack: () -> Unit) {
    var showNewGoal     by remember { mutableStateOf(false) }
    var addMoneyToGoal  by remember { mutableStateOf<FinancialGoal?>(null) }
    var editGoal        by remember { mutableStateOf<FinancialGoal?>(null) }

    if (showNewGoal) {
        GoalEditorDialog(
            initial   = null,
            onDismiss = { showNewGoal = false },
            onSave    = { title, target, due ->
                AppState.addGoal(title, MockData.DEFAULT_GOAL_ICON_KEY, target, due)
                showNewGoal = false
            }
        )
    }
    addMoneyToGoal?.let { goal ->
        AddMoneyDialog(
            goal      = goal,
            onDismiss = { addMoneyToGoal = null },
            onAdd     = { amount ->
                AppState.addToGoal(goal.id, amount)
                addMoneyToGoal = null
            }
        )
    }
    editGoal?.let { goal ->
        GoalEditorDialog(
            initial   = goal,
            onDismiss = { editGoal = null },
            onSave    = { title, target, due ->
                AppState.updateGoal(goal.id, title, target, due)
                editGoal = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Financial Goals", fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp, color = TextPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showNewGoal = true }) {
                        Icon(Icons.Filled.Add, "Add goal", tint = PrimaryTeal)
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

            // Level card
            Card(
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(GoalCardStart, GoalCardEnd)),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                    imageVector        = Icons.Filled.EmojiEvents,
                                    contentDescription = null,
                                    tint               = XpGold,
                                    modifier           = Modifier.size(28.dp)
                                )
                                Column {
                                    Text(
                                        "Level ${AppState.userLevel}",
                                        fontSize   = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = Color.White
                                    )
                                    Text("Budget Master", fontSize = 13.sp,
                                        color = Color.White.copy(0.85f))
                                }
                            }
                            Text(
                                "${AppState.nextLevelXp - AppState.userXp} XP to Level ${AppState.userLevel + 1}",
                                fontSize = 12.sp, color = Color.White.copy(0.8f)
                            )
                            AnimatedProgressBar(
                                progress        = AppState.userXp.toFloat() / AppState.nextLevelXp,
                                color           = Color.White,
                                backgroundColor = Color.White.copy(0.3f),
                                height          = 8,
                                modifier        = Modifier.width(200.dp)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector        = Icons.Filled.Bolt,
                                contentDescription = null,
                                tint               = XpGold,
                                modifier           = Modifier.size(28.dp)
                            )
                            Text(
                                "${AppState.userXp}",
                                fontSize   = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Color.White
                            )
                            Text("Total XP", fontSize = 11.sp, color = Color.White.copy(0.8f))
                        }
                    }
                }
            }

            // Active goals header
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Active Goals", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextPrimary)
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector        = Icons.Filled.Star,
                        contentDescription = null,
                        tint               = XpGold,
                        modifier           = Modifier.size(16.dp)
                    )
                    Text(
                        "${AppState.goals.size} Goals",
                        fontSize = 13.sp, color = PrimaryTeal,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Goal cards
            if (AppState.goals.isEmpty()) {
                Text(
                    "No goals yet — tap + to set one.",
                    fontSize = 13.sp,
                    color    = TextSecondary
                )
            } else {
                AppState.goals.forEach { goal ->
                    GoalCard(
                        goal       = goal,
                        onAddMoney = { addMoneyToGoal = goal },
                        onEdit     = { editGoal = goal },
                        onDelete   = { AppState.removeGoal(goal.id) }
                    )
                }
            }

            // Achievement badges
            Text("Achievements", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextPrimary)

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MockData.achievements.forEach { ach ->
                    AchievementBadge(
                        icon     = ach.icon,
                        title    = ach.title,
                        subtitle = ach.subtitle,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun GoalCard(
    goal: FinancialGoal,
    onAddMoney: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ExpenseOrange.copy(0.12f))
                    ) {
                        Icon(
                            imageVector        = goal.icon,
                            contentDescription = goal.title,
                            tint               = ExpenseOrange,
                            modifier           = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(goal.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                        Text("Target: R ${"%.0f".format(goal.target)}", fontSize = 12.sp, color = TextSecondary)
                    }
                }
                // XP reward chip
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(XpGold.copy(0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "+${goal.xpReward} XP",
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = XpGold
                    )
                }
            }

            // Progress row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "R ${"%.0f".format(goal.saved)} saved",
                    fontSize = 12.sp, color = PrimaryGreen, fontWeight = FontWeight.SemiBold
                )
                Text(
                    "R ${"%.0f".format(goal.remaining)} to go",
                    fontSize = 12.sp, color = TextSecondary
                )
            }

            AnimatedProgressBar(
                progress = goal.progress,
                color    = PrimaryGreen,
                height   = 8
            )

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${(goal.progress * 100).toInt()}% Complete",
                    fontSize = 11.sp, color = TextSecondary
                )
                Text("Due: ${goal.dueDate}", fontSize = 11.sp, color = TextSecondary)
            }

            // Actions row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onEdit,
                    shape   = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors  = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Edit", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onDelete,
                    shape   = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors  = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Delete", fontSize = 12.sp)
                }
                Button(
                    onClick  = onAddMoney,
                    shape    = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Add Money", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun AddMoneyDialog(
    goal: FinancialGoal,
    onDismiss: () -> Unit,
    onAdd: (Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    val value = amount.toDoubleOrNull() ?: 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to ${goal.title}", fontWeight = FontWeight.SemiBold) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Saved: R ${"%.0f".format(goal.saved)} of R ${"%.0f".format(goal.target)}",
                    fontSize = 12.sp, color = TextSecondary
                )
                OutlinedTextField(
                    value         = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label         = { Text("Amount (R)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = value > 0.0,
                onClick = { onAdd(value) }
            ) { Text("Add", color = PrimaryGreen, fontWeight = FontWeight.SemiBold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
private fun GoalEditorDialog(
    initial: FinancialGoal?,
    onDismiss: () -> Unit,
    onSave: (title: String, target: Double, dueDate: String) -> Unit
) {
    var title  by remember { mutableStateOf(initial?.title ?: "") }
    var target by remember { mutableStateOf(initial?.target?.let { "%.0f".format(it) } ?: "") }
    var due    by remember { mutableStateOf(initial?.dueDate ?: "") }

    val targetValue = target.toDoubleOrNull() ?: 0.0
    val canSave     = title.isNotBlank() && targetValue > 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "New Goal" else "Edit Goal", fontWeight = FontWeight.SemiBold) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value         = title,
                    onValueChange = { title = it },
                    label         = { Text("Title") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value         = target,
                    onValueChange = { target = it.filter { c -> c.isDigit() || c == '.' } },
                    label         = { Text("Target amount (R)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value         = due,
                    onValueChange = { due = it },
                    label         = { Text("Due date (e.g. Dec 2026)") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = canSave,
                onClick = { onSave(title.trim(), targetValue, due.trim()) }
            ) { Text("Save", color = PrimaryGreen, fontWeight = FontWeight.SemiBold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
private fun AchievementBadge(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(XpGold.copy(0.15f))
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = title,
                tint               = XpGold,
                modifier           = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "$title\n$subtitle",
            fontSize  = 10.sp,
            color     = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
