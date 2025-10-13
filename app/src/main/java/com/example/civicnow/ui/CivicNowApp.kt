package com.example.civicnow.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.civicnow.R
import com.example.civicnow.ui.screens.CivicNowViewModel
import com.example.civicnow.ui.screens.ElectionScreen
import com.example.civicnow.ui.screens.EventsScreen
import com.example.civicnow.ui.screens.OfficeholdersScreen
import com.example.civicnow.ui.screens.WebViewScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

sealed class Destination (val route: String, val label: String, val icon: ImageVector)
{
    object OfficeHolders: Destination ("officeHolders", "Officeholders", Icons.Filled.Gavel)
    object Events: Destination ("events", "Events", Icons.Filled.Campaign)
    object Elections : Destination ("elections", "Elections", Icons.Filled.Ballot)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CivicNowApp() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Destination.OfficeHolders.route
    val currentScreen = when (currentRoute) {
        Destination.Elections.route -> Destination.Elections.label
        Destination.OfficeHolders.route -> Destination.OfficeHolders.label
        else -> Destination.Events.label
    }

    val civicNowViewModel: CivicNowViewModel = viewModel()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CivicNowTopAppBar(
                currentScreen = currentScreen,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Destination.OfficeHolders.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Destination.OfficeHolders.route) {
                    OfficeholdersScreen(
                        civicNowUiState = civicNowViewModel.civicNowUiState,
                        navController = navController,
                        fetchOfficeHoldersForZip = civicNowViewModel::fetchOfficeHoldersForZip
                    )
                }

                composable(Destination.Events.route) {
                    EventsScreen(
                        civicNowUiState = civicNowViewModel.civicNowUiState,
                        navController = navController,
                        fetchEventsForJurisdiction = civicNowViewModel::fetchEventsForJurisdiction
                    )
                }

                composable(Destination.Elections.route) {
                    ElectionScreen(civicNowUiState = civicNowViewModel.civicNowUiState)
                }

                composable("webview_route/{url}") { backStackEntry ->
                    val encodedUrl = backStackEntry.arguments?.getString("url")
                    val url = encodedUrl?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                    Log.d("WebViewScreen", "Loading URL: $url")

                    WebViewScreen(url)
                }
            }
        }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CivicNowTopAppBar(scrollBehavior: TopAppBarScrollBehavior, modifier: Modifier = Modifier, currentScreen: String) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(R.string.app_name) + " - " + currentScreen,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        modifier = modifier
    )
}


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        val navItems = listOf(
            Destination.OfficeHolders,
            Destination.Events,
            Destination.Elections,
        )

        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route, //
                onClick = {
                    navController.navigate(item.route) {
                        // Avoid multiple copies of the same destination when reselecting
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Only have one copy of the current destination in the backstack
                        launchSingleTop = true
                        restoreState = true // Restore state when navigating back
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) }, //
                label = { Text(text = item.label) } //
            )
        }
    }
}
