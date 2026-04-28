package com.pocketflow.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.pocketflow.app.screens.*
import com.pocketflow.app.ui.theme.*

// ─── Route constants ──────────────────────────────────────────────────────────

object Routes {
    const val LOGIN         = "login"
    const val DASHBOARD     = "dashboard"
    const val ADD_TX        = "add_transaction"
    const val REPORTS       = "reports"
    const val BUDGET        = "budget"
    const val GOALS         = "goals"
}

// ─── Bottom nav item descriptor ───────────────────────────────────────────────

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.DASHBOARD,  "Home",    Icons.Filled.Home,        Icons.Outlined.Home),
    BottomNavItem(Routes.REPORTS,    "Reports", Icons.Filled.BarChart,     Icons.Outlined.BarChart),
    BottomNavItem(Routes.ADD_TX,     "+",       Icons.Filled.AddCircle,    Icons.Filled.AddCircle),
    BottomNavItem(Routes.BUDGET,     "Budget",  Icons.Filled.AccountBalance, Icons.Outlined.AccountBalance),
    BottomNavItem(Routes.GOALS,      "Goals",   Icons.Filled.EmojiEvents,  Icons.Outlined.EmojiEvents),
)

// ─── Root Nav Graph ───────────────────────────────────────────────────────────

@Composable
fun PocketFlowNavGraph() {
    val navController = rememberNavController()
    val navBackStack  by navController.currentBackStackEntryAsState()
    val currentRoute  = navBackStack?.destination?.route

    val showBottomBar = currentRoute != Routes.LOGIN

    Scaffold(
        containerColor = com.pocketflow.app.ui.theme.BackgroundMint,
        bottomBar = {
            if (showBottomBar) {
                PocketFlowBottomBar(
                    currentRoute  = currentRoute,
                    navController = navController
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Routes.LOGIN,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                })
            }
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    onAddTransaction = { navController.navigate(Routes.ADD_TX) },
                    onBudget         = { navController.navigate(Routes.BUDGET) },
                    onReports        = { navController.navigate(Routes.REPORTS) },
                    onGoals          = { navController.navigate(Routes.GOALS) }
                )
            }
            composable(Routes.ADD_TX) {
                AddTransactionScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.REPORTS) {
                ReportsScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.BUDGET) {
                BudgetScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.GOALS) {
                GoalsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

// ─── Bottom Bar ───────────────────────────────────────────────────────────────

@Composable
fun PocketFlowBottomBar(currentRoute: String?, navController: NavHostController) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8. dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            val isCenter = item.route == Routes.ADD_TX

            NavigationBarItem(
                selected = selected,
                onClick  = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        tint = when {
                            isCenter -> PrimaryGreen
                            selected -> PrimaryGreen
                            else     -> TextLight
                        }
                    )
                },
                label = {
                    if (!isCenter) {
                        Text(
                            item.label,
                            fontSize = 10.sp,
                            color    = if (selected) PrimaryGreen else TextLight
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = if (isCenter) Color.Transparent else PrimaryGreen.copy(alpha = 0.1f)
                )
            )
        }
    }
}
