package com.ossalali.daysremaining.presentation.mainscreen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.archive.ArchiveScreen
import com.ossalali.daysremaining.presentation.settings.SettingsScreen
import com.ossalali.daysremaining.presentation.topbar.TopAppBarWithSearch
import com.ossalali.daysremaining.presentation.topbar.appdrawer.AppDrawer
import com.ossalali.daysremaining.presentation.topbar.appdrawer.DebugScreen
import com.ossalali.daysremaining.presentation.topbar.appdrawer.DrawerViewModel
import com.ossalali.daysremaining.presentation.topbar.options.AppDrawerOptions
import com.ossalali.daysremaining.v2.presentation.ui.EventList
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel

@Composable
fun MainScreen(
    mainViewModel: MainScreenViewModel = hiltViewModel(),
    drawerViewModel: DrawerViewModel = hiltViewModel(),
    eventListViewModel: EventListViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    val isDarkMode = isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme

    val statusBarColor = if (isDarkMode) {
        colorScheme.inverseOnSurface  // Light color for dark mode
    } else {
        colorScheme.onSurface  // Dark color for light mode
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = !isDarkMode
        )
    }

    // Get search state from MainScreenViewModel
    val searchText by mainViewModel.searchText.collectAsState()
    val isSearching by mainViewModel.isSearching.collectAsState()
    val currentEvents by mainViewModel.currentEventItems.collectAsState()
    val filteredEvents by mainViewModel.filteredEventsList.collectAsState()
    val selectedEventIds = mainViewModel.selectedEventIds

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = currentBackStackEntry?.destination?.route ?: AppDrawerOptions.Home.name

    val isDrawerOpen = drawerViewModel.isDrawerOpen.collectAsState().value
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(isDrawerOpen) {
        if (isDrawerOpen && drawerState.isClosed) {
            drawerState.open()
        } else if (!isDrawerOpen && drawerState.isOpen) {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentScreen = currentScreen,
                onScreenSelected = { route ->
                    if (route != currentScreen) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                    drawerViewModel.toggleDrawer()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (currentScreen == AppDrawerOptions.Home.name) {
                    TopAppBarWithSearch(
                        isSearching = isSearching,
                        searchText = searchText,
                        onSearchTextChange = mainViewModel::onSearchTextChange,
                        onStartSearch = { mainViewModel.toggleSearch() },
                        onCloseSearch = { mainViewModel.toggleSearch() },
                        onDrawerClick = { drawerViewModel.toggleDrawer() },
                        eventsList = getEventsListForTopBar(
                            isSearching,
                            filteredEvents,
                            currentEvents
                        ),
                        selectedEventIds = selectedEventIds,
                        onArchive = { mainViewModel.showArchiveDialog() },
                        onDelete = { mainViewModel.showDeleteDialog() }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = AppDrawerOptions.Home.name,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(AppDrawerOptions.DEBUG.name) { DebugScreen() }
                composable(AppDrawerOptions.Home.name) {
                    EventList(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = eventListViewModel,
                        onDrawerClick = { drawerViewModel.toggleDrawer() }
                    )
                }
                composable(AppDrawerOptions.Archive.name) { ArchiveScreen() }
                composable(AppDrawerOptions.Settings.name) { SettingsScreen() }
            }
        }
    }
}

private fun getEventsListForTopBar(
    isSearching: Boolean,
    filteredEvents: List<EventItem>,
    currentEvents: List<EventItem>
): MutableList<EventItem> {
    return if (isSearching) {
        filteredEvents.toMutableList()
    } else {
        currentEvents.toMutableList()
    }
}