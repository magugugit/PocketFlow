package com.pocketflow.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketflow.app.data.AppState
import com.pocketflow.app.data.DateRange
import com.pocketflow.app.data.Transaction
import com.pocketflow.app.data.TransactionType
import com.pocketflow.app.ui.components.*
import com.pocketflow.app.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    onAddTransaction: () -> Unit,
    onBudget: () -> Unit,
    onReports: () -> Unit,
    onGoals: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundMint)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header gradient card ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(GradientStart, GradientMid, GradientEnd))
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                // Top row
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Welcome back!",
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                        Text("March 2026", fontSize = 13.sp, color = Color.White.copy(0.8f))
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings",
                            tint = Color.White)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Balance card
                Card(
                    shape  = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.18f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Balance", fontSize = 12.sp, color = Color.White.copy(0.85f))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            formatRand(AppState.totalBalance),
                            fontSize   = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                            StatColumn(
                                label  = "Income",
                                value  = formatRand(AppState.totalIncome),
                                icon   = Icons.Filled.ArrowUpward,
                                color  = Color(0xFF86EFAC)
                            )
                            StatColumn(
                                label  = "Expenses",
                                value  = formatRand(AppState.totalExpense),
                                icon   = Icons.Filled.ArrowDownward,
                                color  = Color(0xFFFCA5A5)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // XP / Level bar
                Card(
                    shape  = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier              = Modifier.padding(12.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint               = XpGold,
                            modifier           = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Level ${AppState.userLevel} – Budget Master",
                                fontWeight = FontWeight.SemiBold,
                                fontSize   = 13.sp,
                                color      = Color.White
                            )
                            Spacer(Modifier.height(4.dp))
                            XpProgressBar(
                                current   = AppState.userXp,
                                max       = AppState.nextLevelXp,
                                nextLevel = AppState.userLevel + 1
                            )
                        }
                        Icon(
                            imageVector        = Icons.Filled.Bolt,
                            contentDescription = "XP",
                            tint               = XpGold,
                            modifier           = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // ── Quick actions ────────────────────────────────────────────────────
        Card(
            shape  = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(Icons.Filled.Add,            "Add",     PrimaryGreen,    onAddTransaction)
                QuickActionButton(Icons.Filled.AccountBalance, "Budget",  BlueAccent,      onBudget)
                QuickActionButton(Icons.Filled.BarChart,       "Reports", EntertainPurple, onReports)
                QuickActionButton(Icons.Filled.Flag,           "Goals",   ExpenseOrange,   onGoals)
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Period filter ─────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                "Period: ${AppState.selectedRange.label}",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp,
                color      = TextPrimary
            )
            Spacer(Modifier.height(8.dp))
            PeriodFilterRow()
        }

        Spacer(Modifier.height(12.dp))

        // ── Recent Transactions ───────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionHeader(
                title       = "Recent Transactions",
                actionLabel = "",
                onAction    = {}
            )
            Spacer(Modifier.height(12.dp))
            val txs = AppState.rangedTransactions
            if (txs.isEmpty()) {
                Text(
                    "No transactions in this period.",
                    fontSize = 13.sp,
                    color    = TextLight
                )
            } else {
                txs.take(8).forEach { tx ->
                    TransactionRow(tx)
                    Spacer(Modifier.height(10.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

@Composable
private fun StatColumn(label: String, value: String, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = color,
            modifier           = Modifier.size(16.dp)
        )
        Column {
            Text(label, fontSize = 11.sp, color = Color.White.copy(0.75f))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

@Composable
private fun QuickActionButton(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(color)
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = label,
                tint               = Color.White,
                modifier           = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 12.sp, color = TextSecondary)
    }
}

@Composable
fun TransactionRow(tx: Transaction) {
    Card(
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier              = Modifier.padding(12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(tx.categoryColor.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector        = tx.categoryIcon,
                    contentDescription = tx.category,
                    tint               = tx.categoryColor,
                    modifier           = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(tx.title, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextPrimary)
                Text(tx.displayDate, fontSize = 11.sp, color = TextLight)
            }
            Text(
                text  = "${if (tx.type == TransactionType.EXPENSE) "-" else "+"}${formatRand(tx.amount)}",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp,
                color      = if (tx.type == TransactionType.EXPENSE) ExpenseOrange else IncomeGreen
            )
        }
    }
}

/**
 * Horizontal chip row letting the user pick the active date range. Updates
 * AppState.selectedRange which every screen reads.
 */
@Composable
fun PeriodFilterRow() {
    val ranges = listOf(
        DateRange.thisMonth(),
        DateRange.last7Days(),
        DateRange.last30Days(),
        DateRange.allTime()
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ranges.forEach { range ->
            val selected = AppState.selectedRange.label == range.label
            FilterChip(
                selected = selected,
                onClick  = { AppState.selectedRange = range },
                label    = { Text(range.label, fontSize = 12.sp) },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryGreen,
                    selectedLabelColor     = Color.White
                )
            )
        }
    }
}

/**
 * Format a Double as South-African Rand using java.text.NumberFormat (assignment
 * requires the use of NumberFormat). Always shows two decimal places.
 */
fun formatRand(amount: Double): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
    nf.maximumFractionDigits = 2
    nf.minimumFractionDigits = 2
    return nf.format(amount)
}
