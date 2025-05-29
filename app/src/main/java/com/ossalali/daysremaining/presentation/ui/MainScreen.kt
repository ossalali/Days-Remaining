package com.ossalali.daysremaining.presentation.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ossalali.daysremaining.presentation.ui.search.rememberSearchAnimationState
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.DrawerViewModel
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel
import com.ossalali.daysremaining.presentation.viewmodel.MainScreenViewModel
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
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

    val searchText by mainViewModel.searchText.collectAsState()
    val isSearching by mainViewModel.isSearching.collectAsState()
    val currentEvents by eventListViewModel.filteredEventsList.collectAsState()

    // Create search animation state
    val searchAnimState = rememberSearchAnimationState()
    val scope = rememberCoroutineScope()

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

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                if (isSearching && currentScreen == Destinations.EVENT_LIST) {
                    AnimatedSearchBar(
                        isSearching = isSearching,
                        searchText = searchText,
                        onSearchTextChanged = { text ->
                            mainViewModel.updateSearchText(text)
                        },
                        onSearchActiveChanged = { active ->
                            if (!active) {
                                mainViewModel.toggleSearch(false)
                            }
                        },
                        searchAnimState = searchAnimState,
                        filteredEvents = currentEvents,
                        onEventClicked = { eventId ->
                            eventListViewModel.onInteraction(
                                EventListViewModel.Interaction.OpenEventItemDetails(eventId)
                            )
                        }
                    )
                } else {
                    CenterAlignedTopAppBar(
                        title = { Text("Days Remaining") }
                    )
                }
            },
            floatingActionButton = {
                if (currentScreen == Destinations.EVENT_LIST && !isSearching) {
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
                        navController = navController,
                        mainScreenSearchToggle = {
                            // When search is activated, trigger the animation
                            scope.launch {
                                // First activate search
                                mainViewModel.toggleSearch(true)
                                // Then animate with a small delay for proper sequence
                                searchAnimState.animateToSearchBar()
                            }
                        },
                        updateSearchButtonPosition = searchAnimState.updateSearchButtonPosition
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
                        EventDetailsScreen(
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