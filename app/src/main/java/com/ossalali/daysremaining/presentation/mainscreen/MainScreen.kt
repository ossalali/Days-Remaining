package com.ossalali.daysremaining.presentation.mainscreen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.archive.ArchiveScreen
import com.ossalali.daysremaining.presentation.event.EventDetails
import com.ossalali.daysremaining.presentation.settings.SettingsScreen
import com.ossalali.daysremaining.presentation.topbar.TopAppBarWithSearch
import com.ossalali.daysremaining.presentation.topbar.appdrawer.AppDrawer
import com.ossalali.daysremaining.presentation.topbar.appdrawer.DebugScreen
import com.ossalali.daysremaining.presentation.topbar.appdrawer.DrawerViewModel
import com.ossalali.daysremaining.v2.presentation.ui.EventList
import com.ossalali.daysremaining.v2.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel

// Screen Definitions
object Destinations {
    const val EVENT_LIST = "eventList"
    const val EVENT_DETAILS = "eventDetails/{eventId}"
    const val EVENT_DETAILS_ARG_ID = "eventId"
    fun eventDetailsRoute(eventId: Int) = "eventDetails/$eventId"
    const val ARCHIVE = "archive"
    const val SETTINGS = "settings"
    const val DEBUG = "debug"
}

@OptIn(ExperimentalAnimationApi::class)
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
    val filteredEvents by eventListViewModel.filteredEventsList.collectAsState()
    val selectedEventIds = mainViewModel.selectedEventIds

    // Sync search state between view models
    LaunchedEffect(searchText, isSearching) {
        if (eventListViewModel.searchText.value != searchText) {
            eventListViewModel.onInteraction(
                EventListViewModel.Interaction.SearchTextChanged(searchText)
            )
        }

        if (eventListViewModel.isSearching.value != isSearching) {
            eventListViewModel.onInteraction(
                EventListViewModel.Interaction.ToggleSearch
            )
        }
    }

    val navController = rememberAnimatedNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = currentBackStackEntry?.destination?.route ?: Destinations.EVENT_LIST

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
        modifier = Modifier.padding(Dimensions.quarter),
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentScreen = currentScreen,
                onScreenSelected = { route ->
                    if (route != currentScreen) {
                        navController.navigate(route) {
                            popUpTo(Destinations.EVENT_LIST) { inclusive = false }
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
                if (currentScreen == Destinations.EVENT_LIST) {
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
            },
            floatingActionButton = {
                if (currentScreen == Destinations.EVENT_LIST) {
                    FloatingActionButton(
                        modifier = Modifier.padding(bottom = Dimensions.quadruple),
                        onClick = {
                            eventListViewModel.onInteraction(EventListViewModel.Interaction.AddEventItem)
                        }
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Event")
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { paddingValues ->
            AnimatedNavHost(
                navController = navController,
                startDestination = Destinations.EVENT_LIST,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Destinations.DEBUG) { DebugScreen() }
                composable(
                    route = Destinations.EVENT_LIST,
                    enterTransition = { fadeIn(animationSpec = tween(300)) },
                    exitTransition = { fadeOut(animationSpec = tween(300)) }
                ) {
                    EventList(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = eventListViewModel,
                        navController = navController
                    )
                }
                composable(
                    route = Destinations.ARCHIVE,
                    enterTransition = { fadeIn(animationSpec = tween(300)) },
                    exitTransition = { fadeOut(animationSpec = tween(300)) }
                ) { ArchiveScreen() }
                composable(
                    route = Destinations.SETTINGS,
                    enterTransition = { fadeIn(animationSpec = tween(300)) },
                    exitTransition = { fadeOut(animationSpec = tween(300)) }
                ) { SettingsScreen() }
                composable(
                    route = Destinations.EVENT_DETAILS,
                    arguments = listOf(navArgument(Destinations.EVENT_DETAILS_ARG_ID) {
                        type = NavType.IntType
                    }),
                    enterTransition = { fadeIn(animationSpec = tween(300)) },
                    exitTransition = { fadeOut(animationSpec = tween(300)) }
                ) { backStackEntry ->
                    val eventId =
                        backStackEntry.arguments?.getInt(Destinations.EVENT_DETAILS_ARG_ID)

                    if (eventId != null) {
                        EventDetails(
                            eventId = eventId,
                            onBackClick = { navController.popBackStack() }
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            navController.popBackStack()
                        }
                    }
                }
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