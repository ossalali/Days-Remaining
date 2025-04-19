package com.ossalali.daysremaining.v2.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
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
    CollectEvents(eventsFlow) { event ->
        when (event) {
            is Event.EventItemArchived -> TODO()
            is Event.EventItemDeleted -> TODO()
        }
    }
    val state by stateflow.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { true }
    )
    var showBottomSheet by remember { mutableStateOf(false) }

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
            bottomBar = {
                Box(modifier = Modifier.height(Dimensions.quadruple)) {}
            },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {}
        ) { paddingValues ->
            Surface(modifier = modifier) {
                when (val currentState = state) {
                    State.Init -> onInteraction(Interaction.Init)
                    is State.ShowEventsGrid -> {
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
        DraggableBottomBarWithFAB(
            onClick = { onInteraction(Interaction.AddEventItem) },
            onDragUp = { onInteraction(Interaction.AddEventItem) },
            onShowDeleted = { /* TODO: Implement show deleted events */ },
            onShowArchived = { /* TODO: Implement show archived events */ },
            modifier = Modifier.align(Alignment.BottomCenter)
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

@Composable
private fun DraggableBottomBarWithFAB(
    onClick: () -> Unit,
    onDragUp: () -> Unit,
    onShowDeleted: () -> Unit,
    onShowArchived: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Track drag progress with a state
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val dragThreshold = -100f // Threshold to trigger action

    // Reset and trigger action if needed
    LaunchedEffect(dragOffset) {
        if (dragOffset < dragThreshold) {
            onDragUp()
            dragOffset = 0f
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        // Reset on drag end if threshold not met
                        if (dragOffset > dragThreshold) {
                            dragOffset = 0f
                        }
                    },
                    onDragCancel = {
                        dragOffset = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Only track vertical drags (y-axis)
                        dragOffset += dragAmount.y
                        // Limit dragging down
                        if (dragOffset > 0f) dragOffset = 0f
                    }
                )
            }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.quadruple)
                .align(Alignment.BottomCenter),
            color = MaterialTheme.colorScheme.background,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationButton(
                    icon = Icons.Default.Delete,
                    label = "Deleted",
                    onClick = onShowDeleted,
                    modifier = Modifier.weight(1f)
                )

                Box(modifier = Modifier.weight(1f))

                NavigationButton(
                    icon = Icons.Default.Archive,
                    label = "Archived",
                    onClick = onShowArchived,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-30).dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp
            )
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Add Event",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun NavigationButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DragHandle() {
    Surface(
        modifier = Modifier
            .padding(vertical = Dimensions.default)
            .width(Dimensions.double)
            .height(Dimensions.quarter),
        shape = RoundedCornerShape(percent = 50),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    ) {}
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