package com.ossalali.daysremaining.presentation.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.ossalali.daysremaining.BuildConfig
import com.ossalali.daysremaining.navigation.AddEventRoute
import com.ossalali.daysremaining.navigation.DebugRoute
import com.ossalali.daysremaining.navigation.EventDetailsRoute
import com.ossalali.daysremaining.navigation.EventListRoute
import com.ossalali.daysremaining.navigation.SettingsRoute
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel
import com.ossalali.daysremaining.presentation.viewmodel.MainScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  mainViewModel: MainScreenViewModel = hiltViewModel(),
  eventListViewModel: EventListViewModel = hiltViewModel(),
) {
    val backStack = rememberNavBackStack(EventListRoute)
    val searchText by mainViewModel.searchText.collectAsState()

    NavDisplay(
      backStack = backStack,
      entryDecorators =
        listOf(rememberSceneSetupNavEntryDecorator(), rememberSavedStateNavEntryDecorator()),
      transitionSpec = { slideInFromRight() togetherWith slideOutToLeft() },
      popTransitionSpec = { slideInFromLeft() togetherWith slideOutToRight() },
      predictivePopTransitionSpec = { slideInFromLeft() togetherWith slideOutToRight() },
      entryProvider =
        entryProvider {
            entry<EventListRoute> {
                MainScreenContent(
                  searchText = searchText,
                  mainViewModel = mainViewModel,
                  eventListViewModel = eventListViewModel,
                  isOnEventList = true,
                  navigateToEventDetails = { eventId -> backStack.add(EventDetailsRoute(eventId)) },
                  navigateToAddEvent = { backStack.add(AddEventRoute) },
                  navigateToSettingsScreen = { backStack.add(SettingsRoute) },
                  navigateToDebugScreen = { backStack.add(DebugRoute) },
                )
            }

            entry<EventDetailsRoute> { route ->
                MainScreenContent(
                  mainViewModel = mainViewModel,
                  eventListViewModel = eventListViewModel,
                  navigateToSettingsScreen = { backStack.add(SettingsRoute) },
                  navigateToDebugScreen = { backStack.add(DebugRoute) },
                  title = "Event Details",
                  showBackButton = true,
                  onBackClick = { backStack.removeLastOrNull() },
                  content = {
                      EventDetailsScreen(
                        eventId = route.eventId,
                        onBackClick = { backStack.removeLastOrNull() },
                      )
                  },
                )
            }

            entry<AddEventRoute> {
                MainScreenContent(
                  mainViewModel = mainViewModel,
                  eventListViewModel = eventListViewModel,
                  navigateToAddEvent = { backStack.add(AddEventRoute) },
                  navigateToSettingsScreen = { backStack.add(SettingsRoute) },
                  navigateToDebugScreen = { backStack.add(DebugRoute) },
                  title = "Add Event",
                  showBackButton = true,
                  onBackClick = { backStack.removeLastOrNull() },
                  content = { paddingValues ->
                      AddEventScreen(onClose = { backStack.removeLastOrNull() })
                  },
                )
            }

            entry<SettingsRoute> {
                MainScreenContent(
                  mainViewModel = mainViewModel,
                  eventListViewModel = eventListViewModel,
                  navigateToSettingsScreen = { backStack.add(SettingsRoute) },
                  navigateToDebugScreen = { backStack.add(DebugRoute) },
                  title = "Settings",
                  showBackButton = true,
                  onBackClick = { backStack.removeLastOrNull() },
                  content = { paddingValues -> SettingsScreen(paddingValues = paddingValues) },
                )
            }

            entry<DebugRoute> {
                MainScreenContent(
                  mainViewModel = mainViewModel,
                  eventListViewModel = eventListViewModel,
                  navigateToDebugScreen = { backStack.add(DebugRoute) },
                  navigateToSettingsScreen = { backStack.add(SettingsRoute) },
                  title = "Debug",
                  showBackButton = true,
                  onBackClick = { backStack.removeLastOrNull() },
                  content = { paddingValues -> DebugScreen(paddingValues = paddingValues) },
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MainScreenContent(
  searchText: String = "",
  mainViewModel: MainScreenViewModel,
  eventListViewModel: EventListViewModel,
  isOnEventList: Boolean = false,
  navigateToAddEvent: () -> Unit = {},
  navigateToEventDetails: (Int) -> Unit = {},
  navigateToDebugScreen: () -> Unit = {},
  navigateToSettingsScreen: () -> Unit = {},
  title: String = "Days Remaining",
  showBackButton: Boolean = false,
  onBackClick: () -> Unit = {},
  content: @Composable ((PaddingValues) -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }
    Scaffold(
      topBar = {
          CenterAlignedTopAppBar(
            title = { Text(title) },
            navigationIcon = {
                if (showBackButton) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                          contentDescription = "Back",
                        )
                    }
                }
            },
            actions = {
                IconButton(onClick = { navigateToSettingsScreen() }) {
                    Icon(
                      imageVector = Icons.Filled.Settings,
                      contentDescription = "Open Settings screen",
                    )
                }
                if (BuildConfig.DEBUG) {
                    IconButton(onClick = { navigateToDebugScreen() }) {
                        Icon(
                          imageVector = Icons.Filled.BugReport,
                          contentDescription = "Open Debug screen",
                        )
                    }
                }
            },
          )
      },
      floatingActionButton = {
          if (isOnEventList) {
              FloatingActionButton(onClick = { navigateToAddEvent() }) {
                  Icon(Icons.Filled.Add, contentDescription = "Add Event")
              }
          }
      },
      floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        if (content != null) content(paddingValues)
        else {
            EventListScreen(
              viewModel = eventListViewModel,
              onNavigateToEventDetails = navigateToEventDetails,
              searchText = searchText,
              onSearchTextChanged = { text -> mainViewModel.updateSearchText(text) },
              focusRequester = focusRequester,
              paddingValues,
            )
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
