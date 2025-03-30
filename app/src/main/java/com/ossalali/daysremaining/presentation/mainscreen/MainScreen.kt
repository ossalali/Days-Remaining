package com.ossalali.daysremaining.presentation.mainscreen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ossalali.daysremaining.presentation.event.EventViewModel
import com.ossalali.daysremaining.presentation.eventcreation.EventCreationViewModel
import com.ossalali.daysremaining.presentation.topbar.TopAppBarWithSearch
import com.ossalali.daysremaining.presentation.topbar.appdrawer.AppDrawer
import com.ossalali.daysremaining.presentation.topbar.appdrawer.DebugScreen
import com.ossalali.daysremaining.presentation.topbar.appdrawer.DrawerViewModel
import com.ossalali.daysremaining.presentation.topbar.options.AppDrawerOptions
import com.ossalali.daysremaining.v2.presentation.ui.EventList
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel

@Composable
fun MainScreen(
    eventCreationViewModel: EventCreationViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    drawerViewModel: DrawerViewModel = hiltViewModel(),
    eventListViewModel: EventListViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    val isDarkMode = isSystemInDarkTheme()

    val statusBarColor = if (isDarkMode) {
        Color.White
    } else {
        Color.Black
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = !isDarkMode
        )
    }

    val searchText by eventViewModel.searchText.collectAsState()
    val isSearching by eventViewModel.isSearching.collectAsState()
    val eventsList by eventViewModel.eventsList.collectAsState()

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
                TopAppBarWithSearch(
                    isSearching = isSearching,
                    searchText = searchText,
                    onSearchTextChange = eventViewModel::onSearchTextChange,
                    onStartSearch = { eventViewModel.onToggleSearch() },
                    onCloseSearch = { eventViewModel.onToggleSearch() },
                    onDrawerClick = { drawerViewModel.toggleDrawer() },
                    eventViewModel = eventViewModel,
                    eventsList = eventsList.toMutableList()
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        eventCreationViewModel.toggleCreateEventScreen(true)
                        eventCreationViewModel.resetEventCreatedState()
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Event")
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
                        viewModel = eventListViewModel
                    )
                }
                composable(AppDrawerOptions.Archive.name) { /*ArchiveScreen()*/ }
                composable(AppDrawerOptions.Settings.name) { /*SettingsScreen()*/ }
            }
        }
    }
}