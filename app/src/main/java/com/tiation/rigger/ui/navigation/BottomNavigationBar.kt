package com.tiation.rigger.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tiation.rigger.viewmodel.SharedViewModel
import com.tiation.rigger.viewmodel.Theme

sealed class NavigationItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Dashboard : NavigationItem(
        route = Screen.Dashboard.route,
        icon = Icons.Default.Dashboard,
        label = "Dashboard"
    )
    object Characters : NavigationItem(
        route = Screen.Characters.route,
        icon = Icons.Default.Person,
        label = "Characters"
    )
    object Campaign : NavigationItem(
        route = Screen.Campaign.route,
        icon = Icons.Default.Map,
        label = "Campaign"
    )
    object Settings : NavigationItem(
        route = Screen.Settings.route,
        icon = Icons.Default.Settings,
        label = "Settings"
    )
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    viewModel: SharedViewModel
) {
    val items = listOf(
        NavigationItem.Dashboard,
        NavigationItem.Characters,
        NavigationItem.Campaign,
        NavigationItem.Settings
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentTheme by viewModel.currentTheme.collectAsState()
    
    NavigationBar(
        containerColor = when (currentTheme) {
            Theme.HERO -> MaterialTheme.colorScheme.primaryContainer
            Theme.DUNGEON_MASTER -> MaterialTheme.colorScheme.secondaryContainer
        }
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = when (currentTheme) {
                        Theme.HERO -> MaterialTheme.colorScheme.primary
                        Theme.DUNGEON_MASTER -> MaterialTheme.colorScheme.secondary
                    },
                    selectedTextColor = when (currentTheme) {
                        Theme.HERO -> MaterialTheme.colorScheme.primary
                        Theme.DUNGEON_MASTER -> MaterialTheme.colorScheme.secondary
                    }
                )
            )
        }
    }
}
