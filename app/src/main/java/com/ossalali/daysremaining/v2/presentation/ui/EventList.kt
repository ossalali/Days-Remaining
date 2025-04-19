package com.ossalali.daysremaining.v2.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.v2.presentation.ui.theme.DefaultPreviews
import com.ossalali.daysremaining.v2.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.Event
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.Interaction
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt

/**
 * Main event list screen that displays a grid of events and handles search functionality
 */
@Composable
internal fun EventList(
    modifier: Modifier = Modifier,
    viewModel: EventListViewModel = hiltViewModel(),
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.onInteraction(Interaction.Init)
    }

    LaunchedEffect(navController, viewModel) {
        viewModel.navigationEvent.collect { route ->
            navController.navigate(route)
        }
    }

    EventListImpl(
        onInteraction = viewModel::onInteraction,
        stateflow = viewModel.state,
        eventsFlow = viewModel.events,
        selectedEventIds = viewModel.selectedEventItemIds,
        currentEventItems = viewModel.currentEventItems,
        modifier = modifier
    )
}

/**
 * Implementation of the event list screen with all UI components
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventListImpl(
    onInteraction: (Interaction) -> Unit,
    stateflow: StateFlow<State>,
    eventsFlow: Flow<Event>,
    selectedEventIds: List<Int>,
    currentEventItems: List<EventItem>,
    modifier: Modifier = Modifier
) {
    CollectEvents(eventsFlow) { event: Event ->
        when (event) {
            is Event.EventItemArchived -> TODO()
            is Event.EventItemDeleted -> TODO()
            else -> {}
        }
    }
    val state by stateflow.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { true }
    )
    var showBottomSheet by remember { mutableStateOf(false) }

    // Local search state
    var isSearching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var filteredEvents by remember { mutableStateOf(currentEventItems) }

    // Create animation state
    val searchAnimState = rememberSearchAnimationState()

    // Bottom bar animation
    val density = LocalDensity.current
    val bottomBarHeight = with(density) { Dimensions.quintuple.toPx() }
    val bottomBarOffsetY by animateFloatAsState(
        targetValue = if (isSearching && searchAnimState.bottomBarAnimStarted) bottomBarHeight else 0f,
        animationSpec = tween(durationMillis = 250, delayMillis = 0),
        label = "BottomBarAnimation"
    )

    // Show bottom sheet when in AddEventScreen state
    LaunchedEffect(state) {
        if (state is State.ShowAddEventScreen) {
            showBottomSheet = true
            sheetState.expand()
        } else {
            if (showBottomSheet) {
                sheetState.hide()
                showBottomSheet = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                // Only show SearchBar when search is active
                if (isSearching) {
                    AnimatedSearchBar(
                        isSearching = isSearching,
                        searchText = searchText,
                        onSearchTextChanged = {
                            searchText = it
                            onInteraction(Interaction.SearchTextChanged(it))
                            // Filter locally for immediate response
                            filteredEvents = if (it.isEmpty()) {
                                currentEventItems
                            } else {
                                currentEventItems.filter { event ->
                                    event.title.contains(it, ignoreCase = true)
                                }
                            }
                        },
                        onSearchActiveChanged = { active ->
                            if (!active) {
                                isSearching = false
                                onInteraction(Interaction.ToggleSearch)
                            } else {
                                isSearching = true
                                onInteraction(Interaction.ToggleSearch)
                            }
                        },
                        searchAnimState = searchAnimState,
                        filteredEvents = filteredEvents,
                        onEventClicked = { eventId ->
                            onInteraction(Interaction.OpenEventItemDetails(eventId))
                        }
                    )
                }
            },
            bottomBar = {
                // Always show the bottom bar placeholder to maintain layout consistency during animation
                Box(modifier = Modifier.height(Dimensions.quadruple)) {}
            },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {}
        ) { paddingValues ->
            Surface(modifier = modifier) {
                when (val currentState = state) {
                    State.Init -> onInteraction(Interaction.Init)
                    is State.ShowEventsGrid -> {
                        // Only show the main grid when not searching
                        if (!isSearching) {
                            EventListGrid(
                                onEventItemClick = { eventItemId ->
                                    onInteraction(
                                        Interaction.OpenEventItemDetails(
                                            eventItemId
                                        )
                                    )
                                },
                                onEventItemSelection = { onInteraction(Interaction.Select(it)) },
                                events = currentState.eventItems,
                                selectedEventIds = selectedEventIds,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                            )
                        } else {
                            // Empty placeholder when searching - content is in the SearchBar
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                            )
                        }
                    }

                    is State.ShowAddEventScreen -> {
                        EventListGrid(
                            onEventItemClick = { eventItemId ->
                                onInteraction(
                                    Interaction.OpenEventItemDetails(
                                        eventItemId
                                    )
                                )
                            },
                            onEventItemSelection = { onInteraction(Interaction.Select(it)) },
                            events = currentEventItems,
                            selectedEventIds = selectedEventIds,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        )
                    }
                }
            }
        }

        // Add draggable bottom bar that shows sheet when dragged
        // Apply animation offset to the bottom bar
        DraggableBottomBarWithFAB(
            onClick = { 
                isSearching = !isSearching
                onInteraction(Interaction.ToggleSearch) 
            },
            onDragUp = { onInteraction(Interaction.AddEventItem) },
            onShowDeleted = { /* TODO: Implement show deleted events */ },
            onShowArchived = { /* TODO: Implement show archived events */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset { IntOffset(0, bottomBarOffsetY.roundToInt()) },
            fabPositionCallback = searchAnimState.updateSearchButtonPosition
        )

        // Show bottom sheet for add event screen
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    onInteraction(Interaction.Init)
                },
                sheetState = sheetState,
                dragHandle = { DragHandle() }
            ) {
                AddEventScreen(
                    onEventCreated = { event ->
                        onInteraction(Interaction.EventItemAdded(event))
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onInteraction(Interaction.Init)
                            }
                        }
                    },
                    onCancel = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onInteraction(Interaction.Init)
                            }
                        }
                    }
                )
            }
        }
    }
}

@DefaultPreviews
@Composable
internal fun EventListPreview(
    @PreviewParameter(EventListPreviewParameterProvider::class)
    state: State
) {
    EventListImpl(
        onInteraction = {},
        stateflow = MutableStateFlow(state),
        eventsFlow = emptyFlow(),
        selectedEventIds = emptyList(),
        currentEventItems = emptyList()
    )
}

internal class EventListPreviewParameterProvider : CollectionPreviewParameterProvider<State>(
    listOf(
        State.Init,
        State.ShowEventsGrid(
            listOf(
                EventItem(
                    id = 0,
                    title = "Event 1",
                    description = "Event 1 Description",
                    date = LocalDate.now().plusDays(5)
                )
            )
        )
    )
)