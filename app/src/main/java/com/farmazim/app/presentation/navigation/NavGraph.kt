package com.farmazim.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.farmazim.app.R
import com.farmazim.app.presentation.ui.crop.CropScreen
import com.farmazim.app.presentation.ui.finance.FinanceScreen
import com.farmazim.app.presentation.ui.input.InputScreen
import com.farmazim.app.presentation.ui.livestock.LivestockScreen
import com.farmazim.app.presentation.ui.paywall.PaywallScreen
import com.farmazim.app.presentation.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Crops : Screen("crops")
    object Inputs : Screen("inputs")
    object Livestock : Screen("livestock")
    object Finance : Screen("finance")
    object Settings : Screen("settings")
    object Paywall : Screen("paywall")
}

data class BottomNavItem(val screen: Screen, val labelRes: Int, val icon: ImageVector)

@Composable
fun FarmaZimNavGraph() {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem(Screen.Crops, R.string.nav_crops, Icons.Default.Grass),
        BottomNavItem(Screen.Inputs, R.string.nav_inputs, Icons.Default.Science),
        BottomNavItem(Screen.Livestock, R.string.nav_livestock, Icons.Default.Pets),
        BottomNavItem(Screen.Finance, R.string.nav_finance, Icons.Default.AttachMoney),
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(stringResource(item.labelRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = Screen.Crops.route) {
            composable(Screen.Crops.route) {
                CropScreen(contentPadding = innerPadding, onUpgradeRequired = { navController.navigate(Screen.Paywall.route) })
            }
            composable(Screen.Inputs.route) { InputScreen(contentPadding = innerPadding) }
            composable(Screen.Livestock.route) {
                LivestockScreen(contentPadding = innerPadding, onUpgradeRequired = { navController.navigate(Screen.Paywall.route) })
            }
            composable(Screen.Finance.route) {
                FinanceScreen(contentPadding = innerPadding, onUpgradeRequired = { navController.navigate(Screen.Paywall.route) })
            }
            composable(Screen.Settings.route) { SettingsScreen(contentPadding = innerPadding) }
            composable(Screen.Paywall.route) { PaywallScreen(onBack = { navController.popBackStack() }) }
        }
    }
}
