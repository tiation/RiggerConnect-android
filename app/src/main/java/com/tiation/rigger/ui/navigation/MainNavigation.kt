package com.tiation.rigger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tiation.rigger.ui.screens.RoleSelectionScreen
import com.tiation.rigger.viewmodel.SharedViewModel

sealed class Screen(val route: String) {
    object RoleSelection : Screen("role_selection")
    object Dashboard : Screen("dashboard")
    object Characters : Screen("characters")
    object Campaign : Screen("campaign")
    object Settings : Screen("settings")
}

@Composable
fun MainNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: SharedViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.RoleSelection.route
    ) {
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(
                onRoleSelected = { isDM ->
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Dashboard.route) {
            // DashboardScreen()
        }
        
        composable(Screen.Characters.route) {
            // CharactersScreen()
        }
        
        composable(Screen.Campaign.route) {
            // CampaignScreen()
        }
        
        composable(Screen.Settings.route) {
            // SettingsScreen()
        }
    }
}
