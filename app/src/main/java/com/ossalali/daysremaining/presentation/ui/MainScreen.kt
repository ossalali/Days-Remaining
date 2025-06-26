package com.ossalali.daysremaining.presentation.ui

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.navigation.AddEventRoute
import com.ossalali.daysremaining.navigation.DebugRoute
import com.ossalali.daysremaining.navigation.EventDetailsRoute
import com.ossalali.daysremaining.navigation.EventListRoute
import com.ossalali.daysremaining.navigation.SettingsRoute
import com.ossalali.daysremaining.presentation.ui.search.SearchAnimationState
import com.ossalali.daysremaining.presentation.ui.search.rememberSearchAnimationState
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel
import com.ossalali.daysremaining.presentation.viewmodel.MainScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainScreenViewModel = hiltViewModel(),
    eventListViewModel: EventListViewModel = hiltViewModel(),
) {
    val backStack = rememberNavBackStack(EventListRoute)
    val systemUiController = rememberSystemUiController()
    val isDarkMode = isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme

    val statusBarColor =
        if (isDarkMode) {
            colorScheme.inverseOnSurface
        } else {
            colorScheme.onSurface
        }

    SideEffect {
        systemUiController.setStatusBarColor(color = statusBarColor, darkIcons = !isDarkMode)
    }

    val searchText by mainViewModel.searchText.collectAsState()
    val isSearching by mainViewModel.isSearching.collectAsState()
    val currentEvents by eventListViewModel.filteredEventsList.collectAsState()

    val searchAnimState = rememberSearchAnimationState()

    LaunchedEffect(searchText, isSearching) {
        if (eventListViewModel.searchText.value != searchText) {
            eventListViewModel.onInteraction(
                EventListViewModel.Interaction.SearchTextChanged(searchText)
            )
        }

        if (eventListViewModel.isSearching.value != isSearching) {
            eventListViewModel.onInteraction(EventListViewModel.Interaction.ToggleSearch)
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = {
            Log.d(
                "MainScreen",
                "System back pressed. Current backStack: ${backStack.map { it::class.simpleName }}",
            )
            val removed = backStack.removeLastOrNull()
            Log.d(
                "MainScreen",
                "Called removeLastOrNull. Removed: ${removed?.let { it::class.simpleName }}. New backStack: ${backStack.map { it::class.simpleName }}",
            )
        },
        entryDecorators =
            listOf(rememberSceneSetupNavEntryDecorator(), rememberSavedStateNavEntryDecorator()),
        transitionSpec = { slideInFromRight() togetherWith slideOutToLeft() },
        popTransitionSpec = { slideInFromLeft() togetherWith slideOutToRight() },
        predictivePopTransitionSpec = { slideInFromLeft() togetherWith slideOutToRight() },
        entryProvider =
            entryProvider {
                entry<EventListRoute> {
                    MainScreenContent(
                        isSearching = isSearching,
                        searchText = searchText,
                        currentEvents = currentEvents,
                        searchAnimState = searchAnimState,
                        mainViewModel = mainViewModel,
                        eventListViewModel = eventListViewModel,
                        isOnEventList = true,
                        navigateToEventDetails = { eventId ->
                            backStack.add(
                                EventDetailsRoute(
                                    eventId
                                )
                            )
                        },
                        navigateAddEvent = { backStack.add(AddEventRoute) },
                    )
                }

                entry<EventDetailsRoute> { route ->
                    MainScreenContent(
                        searchAnimState = searchAnimState,
                        mainViewModel = mainViewModel,
                        eventListViewModel = eventListViewModel,
                        content = {
                            EventDetailsScreen(
                                eventId = route.eventId,
                                onBackClick = { backStack.removeLastOrNull() },
                            )
                        },
                    )
                }

                entry<AddEventRoute> { route ->
                    MainScreenContent(
                        searchAnimState = searchAnimState,
                        mainViewModel = mainViewModel,
                        eventListViewModel = eventListViewModel,
                        navigateAddEvent = { backStack.add(AddEventRoute) },
                        content = { AddEventScreen(onClose = { backStack.removeLastOrNull() }) },
                    )
                }

                entry<SettingsRoute> {
                    MainScreenContent(
                        searchAnimState = searchAnimState,
                        mainViewModel = mainViewModel,
                        eventListViewModel = eventListViewModel,
                        content = { SettingsScreen() },
                    )
                }

                entry<DebugRoute> {
                    MainScreenContent(
                        searchAnimState = searchAnimState,
                        mainViewModel = mainViewModel,
                        eventListViewModel = eventListViewModel,
                        content = { DebugScreen() },
                    )
                }
            },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(
    isSearching: Boolean = false,
    searchText: String = "",
    currentEvents: List<EventItem> = emptyList(),
    searchAnimState: SearchAnimationState,
    mainViewModel: MainScreenViewModel,
    eventListViewModel: EventListViewModel,
    isOnEventList: Boolean = false,
    navigateAddEvent: () -> Unit = {}, // Changed signature
    navigateToEventDetails: (Int) -> Unit = {},
    content: @Composable (() -> Unit)? = null,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                if (isSearching && isOnEventList) {
                    AnimatedSearchBar(
                        isSearching = true,
                        searchText = searchText,
                        onSearchTextChanged = { text -> mainViewModel.updateSearchText(text) },
                        onSearchActiveChanged = { active ->
                            if (!active) {
                                mainViewModel.toggleSearch(false)
                            }
                        },
                        searchAnimState = searchAnimState,
                        filteredEvents = currentEvents,
                        onEventClicked = { eventId -> navigateToEventDetails(eventId) },
                    )
                } else {
                    CenterAlignedTopAppBar(title = { Text("Days Remaining") })
                }
            },
            floatingActionButton = {
                if (isOnEventList && !isSearching) {
                    FloatingActionButton(
                        modifier = Modifier.padding(bottom = Dimensions.quadruple),
                        onClick = { navigateAddEvent() },
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Event")
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End,
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (content != null) content()
                else {
                    EventListScreen(
                        viewModel = eventListViewModel,
                        onNavigateToEventDetails = navigateToEventDetails,
                    )
                }
            }
        }
    }
}

private fun slideInFromRight(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(300),
    ) + fadeIn(animationSpec = tween(300))
}

private fun slideOutToLeft(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(300),
    ) + fadeOut(animationSpec = tween(300))
}

private fun slideInFromLeft(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(300),
    ) + fadeIn(animationSpec = tween(300))
}

private fun slideOutToRight(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(300),
    ) + fadeOut(animationSpec = tween(300))
}
