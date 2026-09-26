package com.weatherapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.weatherapp.presentation.R
import com.weatherapp.presentation.details.WeatherDetailsScreen
import com.weatherapp.presentation.favorites.FavoritesScreen
import com.weatherapp.presentation.home.HomeScreen
import com.weatherapp.presentation.search.SearchScreen
import com.weatherapp.presentation.settings.SettingsScreen
import com.weatherapp.presentation.settings.SettingsViewModel
import com.weatherapp.presentation.theme.NimbusTheme
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NimbusApp() {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val themeMode = settingsViewModel.themeMode.collectAsStateWithLifecycle().value
    NimbusTheme(themeMode = themeMode) {
        WeatherNavHost()
    }
}

@Composable
private fun WeatherNavHost() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val showBar = backStack?.destination?.hasRoute<DetailsRoute>() != true
    Scaffold(
        bottomBar = {
            if (showBar) {
                NimbusBottomBar(navController)
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(padding),
        ) {
            composable<HomeRoute> {
                HomeScreen(onOpenSearch = { navController.navigate(SearchRoute) })
            }
            composable<SearchRoute> {
                SearchScreen(onCitySelected = { city -> navController.navigate(city.toDetailsRoute()) })
            }
            composable<FavoritesRoute> {
                FavoritesScreen(onCitySelected = { city -> navController.navigate(city.toDetailsRoute()) })
            }
            composable<SettingsRoute> {
                SettingsScreen()
            }
            composable<DetailsRoute> {
                WeatherDetailsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

private data class Tab(val route: Any, val label: Int, val icon: ImageVector, val selected: (androidx.navigation.NavDestination?) -> Boolean)

@Composable
private fun NimbusBottomBar(navController: NavHostController) {
    val destination = navController.currentBackStackEntryAsState().value?.destination
    val tabs = listOf(
        Tab(HomeRoute, R.string.nav_home, Icons.Filled.Home) { it?.hasRoute<HomeRoute>() == true },
        Tab(SearchRoute, R.string.nav_search, Icons.Filled.Search) { it?.hasRoute<SearchRoute>() == true },
        Tab(FavoritesRoute, R.string.nav_favorites, Icons.Filled.Favorite) { it?.hasRoute<FavoritesRoute>() == true },
        Tab(SettingsRoute, R.string.nav_settings, Icons.Filled.Settings) { it?.hasRoute<SettingsRoute>() == true },
    )
    NavigationBar {
        tabs.forEach { tab ->
            val label = stringResource(tab.label)
            NavigationBarItem(
                selected = tab.selected(destination),
                onClick = {
                    navController.navigate(tab.route) {
                        popUpTo(HomeRoute) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(tab.icon, contentDescription = label) },
                label = { Text(label) },
            )
        }
    }
}
