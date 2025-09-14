package com.ossalali.daysremaining.presentation.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.ossalali.daysremaining.BuildConfig
import com.ossalali.daysremaining.infrastructure.appLogger
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.navigation.AddEventRoute
import com.ossalali.daysremaining.navigation.DebugRoute
import com.ossalali.daysremaining.navigation.EventDetailsRoute
import com.ossalali.daysremaining.navigation.EventListRoute
import com.ossalali.daysremaining.navigation.SettingsRoute
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel.Interaction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    eventListViewModel: EventListViewModel =
        hiltViewModel(LocalViewModelStoreOwner.current!!, "EventListViewModel"),
    eventId: Long? = null,
    shouldNavigateToAddEvent: Boolean = false,
) {
  val backStack = rememberNavBackStack(EventListRoute)
  val isIntentLaunch = remember { eventId != null || shouldNavigateToAddEvent }

  LaunchedEffect(eventId, shouldNavigateToAddEvent) {
    if (eventId != null) {
      backStack.add(EventDetailsRoute(eventId.toInt()))
    } else if (shouldNavigateToAddEvent) {
      backStack.add(AddEventRoute)
    }
  }

  NavDisplay(
      backStack = backStack,
      entryDecorators =
          listOf(rememberSceneSetupNavEntryDecorator(), rememberSavedStateNavEntryDecorator()),
      transitionSpec = {
        if (isIntentLaunch) {
          EnterTransition.None togetherWith ExitTransition.None
        } else {
          slideInFromRight() togetherWith slideOutToLeft()
        }
      },
      popTransitionSpec = { slideInFromLeft() togetherWith slideOutToRight() },
      predictivePopTransitionSpec = { slideInFromLeft() togetherWith slideOutToRight() },
      entryProvider =
          entryProvider {
            entry<EventListRoute> {
              MainScreenContent(
                  eventListViewModel = eventListViewModel,
                  isOnEventList = true,
                  navigateToAddEvent = { backStack.add(AddEventRoute) },
                  navigateToEventDetails = { eventId -> backStack.add(EventDetailsRoute(eventId)) },
                  navigateToDebugScreen = { backStack.add(DebugRoute) },
                  navigateToSettingsScreen = { backStack.add(SettingsRoute) },
                  showTopAppBarButtons = true,
              )
            }

            entry<EventDetailsRoute> { route ->
              MainScreenContent(
                  eventListViewModel = eventListViewModel,
                  title = "Event Details",
                  showBackButton = true,
                  onBackClick = { backStack.removeLastOrNull() },
                  content = { paddingValues ->
                    EventDetailsScreen(
                        eventId = route.eventId,
                        onBackClick = { backStack.removeLastOrNull() },
                        onDeleteEvent = { eventItem -> eventListViewModel.deleteEvent(eventItem) },
                        paddingValues = paddingValues,
                    )
                  },
              )
            }

            entry<AddEventRoute> {
              MainScreenContent(
                  eventListViewModel = eventListViewModel,
                  navigateToAddEvent = { backStack.add(AddEventRoute) },
                  title = "Add Event",
                  showBackButton = true,
                  onBackClick = { backStack.removeLastOrNull() },
                  content = { paddingValues ->
                    EventDetailsScreen(
                        eventId = null,
                        onBackClick = { backStack.removeLastOrNull() },
                        paddingValues = paddingValues,
                    )
                  },
              )
            }

            entry<SettingsRoute> {
              MainScreenContent(
                  eventListViewModel = eventListViewModel,
                  navigateToSettingsScreen = { backStack.add(SettingsRoute) },
                  title = "Settings",
                  showBackButton = true,
                  onBackClick = { backStack.removeLastOrNull() },
                  content = { paddingValues -> SettingsScreen(paddingValues = paddingValues) },
              )
            }

            entry<DebugRoute> {
              MainScreenContent(
                  eventListViewModel = eventListViewModel,
                  navigateToDebugScreen = { backStack.add(DebugRoute) },
                  title = "Debug",
                  showBackButton = true,
                  onBackClick = { backStack.removeLastOrNull() },
                  content = { paddingValues ->
                    DebugScreen(
                        paddingValues = paddingValues,
                        onClose = { backStack.removeLastOrNull() },
                    )
                  },
              )
            }
          },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(
    eventListViewModel: EventListViewModel,
    isOnEventList: Boolean = false,
    navigateToAddEvent: () -> Unit = {},
    navigateToEventDetails: (Int) -> Unit = {},
    navigateToDebugScreen: () -> Unit = {},
    navigateToSettingsScreen: () -> Unit = {},
    title: String = "Days Remaining",
    showBackButton: Boolean = false,
    showTopAppBarButtons: Boolean = false,
    onBackClick: () -> Unit = {},
    content: @Composable ((PaddingValues) -> Unit)? = null,
) {
  val selectedEventItems by eventListViewModel.selectedEventItems.collectAsStateWithLifecycle()
  val pendingDeleteEventsState by
      eventListViewModel.pendingDeleteEvents.collectAsStateWithLifecycle()
  var showDeleteConfirmDialog by remember { mutableStateOf(false) }
  val snackBarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(pendingDeleteEventsState) {
    val itemsForThisSnackbar = pendingDeleteEventsState

    if (itemsForThisSnackbar.isNotEmpty()) {
      val snackBarMessage =
          if (itemsForThisSnackbar.size == 1) "${itemsForThisSnackbar.first().title} deleted"
          else "${itemsForThisSnackbar.size} events deleted"

      coroutineScope.launch {
        snackBarHostState.currentSnackbarData?.dismiss()

        val result =
            snackBarHostState.showSnackbar(
                message = snackBarMessage,
                actionLabel = "Undo",
                duration = SnackbarDuration.Short,
            )
        if (result == SnackbarResult.ActionPerformed) {
          eventListViewModel.onInteraction(Interaction.UndoDelete(itemsForThisSnackbar))
        } else {
          eventListViewModel.onInteraction(Interaction.ConfirmDeletions(itemsForThisSnackbar))
        }
      }
    }
  }

  if (showDeleteConfirmDialog && selectedEventItems.isNotEmpty()) {
    if (selectedEventItems.size == 1) {
      DeleteAlertDialog(
          eventTitle = selectedEventItems.first().title,
          onConfirm = {
            eventListViewModel.deleteEvents(selectedEventItems)
            showDeleteConfirmDialog = false
          },
          onDismiss = { showDeleteConfirmDialog = false },
      )
    } else {
      DeleteAlertDialog(
          numberOfEventsToBeDeleted = selectedEventItems.size,
          onConfirm = {
            eventListViewModel.deleteEvents(selectedEventItems)
            showDeleteConfirmDialog = false
          },
          onDismiss = { showDeleteConfirmDialog = false },
      )
    }
  }

  Scaffold(
      modifier = Modifier.background(Color.Transparent),
      topBar = {
        SetupTopAppBar(
            selectedEventItems,
            title,
            showBackButton,
            showTopAppBarButtons,
            onBackClick,
            navigateToDebugScreen,
            navigateToSettingsScreen,
            eventListViewModel,
            onDeleteAction = { showDeleteConfirmDialog = true },
        )
      },
      floatingActionButton = {
        if (isOnEventList) {
          FloatingActionButton(
              modifier = Modifier.imePadding(),
              onClick = { navigateToAddEvent() },
          ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Event")
          }
        }
      },
      snackbarHost = { SnackbarHost(snackBarHostState) },
      floatingActionButtonPosition = FabPosition.End,
  ) { paddingValues ->
    appLogger().d(tag = "NAV3_Content", message = "content: ${content == null}")
    if (content != null) {
      content(paddingValues)
    } else {
      Box(modifier = Modifier
          .padding(paddingValues)
          .fillMaxSize()) {
        EventListScreen(
            viewModel = eventListViewModel,
            onNavigateToEventDetails = navigateToEventDetails,
            showFab = isOnEventList,
            selectedEventItems = selectedEventItems,
        )
      }
    }
  }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SetupTopAppBar(
    selectedEventItems: ImmutableList<EventItem>,
    title: String,
    showBackButton: Boolean,
    showTopAppBarButtons: Boolean,
    onBackClick: () -> Unit,
    navigateToDebugScreen: () -> Unit,
    navigateToSettingsScreen: () -> Unit,
    eventListViewModel: EventListViewModel,
    onDeleteAction: () -> Unit,
) {
  if (selectedEventItems.isEmpty()) {
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
            if (showTopAppBarButtons) {
                IconButton(onClick = { navigateToSettingsScreen() }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Open Settings screen",
                    )
                }
                if (BuildConfig.DEBUG) {
                    IconButton(
                        modifier = Modifier.padding(horizontal = Dimensions.quarter),
                        onClick = { navigateToDebugScreen() },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BugReport,
                            contentDescription = "Open Debug screen",
                        )
                    }
            }
          }
        },
    )
  } else {
    TopAppBar(
        title = {},
        actions = {
          Row(
              modifier = Modifier.padding(horizontal = Dimensions.default),
              verticalAlignment = Alignment.CenterVertically,
          ) {
            IconButton(onClick = { eventListViewModel.onInteraction(Interaction.ClearSelection) }) {
              Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Back",
              )
            }
            Spacer(Modifier.width(Dimensions.half))
            Text(
                text = "${selectedEventItems.size}",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.weight(1f))
            if (eventListViewModel.hasUnarchivedEventItems()) {
              IconButton(onClick = { eventListViewModel.archiveEvents(selectedEventItems) }) {
                Icon(
                    imageVector = Icons.Outlined.Archive,
                    contentDescription = "Archive selected Events",
                )
              }
            }
            if (eventListViewModel.hasArchivedEventItems()) {
              IconButton(onClick = { eventListViewModel.unarchiveEvents(selectedEventItems) }) {
                Icon(
                    imageVector = Icons.Outlined.Inbox,
                    contentDescription = "Unarchive selected Events",
                )
              }
            }
            IconButton(onClick = onDeleteAction) {
              Icon(
                  imageVector = Icons.Filled.Delete,
                  contentDescription = "Delete selected Events",
              )
            }
            IconButton(onClick = { eventListViewModel.onInteraction(Interaction.SelectAll) }) {
              Icon(
                  imageVector = Icons.Filled.SelectAll,
                  contentDescription = "Select all Events",
              )
            }
          }
        },
    )
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
