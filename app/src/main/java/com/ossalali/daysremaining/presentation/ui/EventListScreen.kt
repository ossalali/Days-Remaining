package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel.Interaction
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel.State
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

/** Main event list screen that displays a grid of events and handles search functionality */
@Composable
internal fun EventListScreen(
    viewModel: EventListViewModel = hiltViewModel(),
    onNavigateToEventDetails: (Int) -> Unit = {},
) {
    LaunchedEffect(Unit) { viewModel.onInteraction(Interaction.Init) }

    EventListImpl(
        modifier = Modifier.fillMaxWidth(),
        onInteraction = viewModel::onInteraction,
        stateflow = viewModel.state,
        eventUiState = viewModel.eventUiState,
        selectedEventIds = viewModel.selectedEventItemIds,
        onNavigateToEventDetails = onNavigateToEventDetails,
        onArchiveEvents = { viewModel::archiveEvents },
        onDeleteEvents = { viewModel::deleteEvents },
    )
}

/** Implementation of the event list screen with all UI components */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventListImpl(
    modifier: Modifier = Modifier,
    onInteraction: (Interaction) -> Unit,
    stateflow: StateFlow<State>,
    eventUiState: StateFlow<List<EventItem>>,
    selectedEventIds: List<Int>,
    onNavigateToEventDetails: (Int) -> Unit = {},
    onArchiveEvents: (List<Int>) -> Unit = {},
    onDeleteEvents: (List<Int>) -> Unit = {},
) {
    val events by eventUiState.collectAsStateWithLifecycle()
    val state by stateflow.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.quarter)) {
                    AddChips()
                    IconButton(onClick = { onArchiveEvents(selectedEventIds) }) {
                        Icon(
                            imageVector = Icons.Outlined.Archive,
                            contentDescription = "Archive selected Events",
                        )
                    }
                    IconButton(onClick = { onDeleteEvents(selectedEventIds) }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete selected Events",
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {},
        ) { paddingValues ->
            Surface(modifier = modifier) {
                when (val currentState = state) {
                    State.Init -> onInteraction(Interaction.Init)
                    is State.ShowEventsGrid -> {
                        EventListGrid(
                            onEventItemClick = { eventItemId ->
                                onNavigateToEventDetails(eventItemId)
                            },
                            onEventItemSelection = { onInteraction(Interaction.Select(it)) },
                            events = currentState.eventItems,
                            selectedEventIds = selectedEventIds,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddChips() {
    var filterActiveSelected by remember { mutableStateOf(false) }
    var filterArchivedSelected by remember { mutableStateOf(false) }
    var filterDeletedSelected by remember { mutableStateOf(false) }

    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        FilterChip(
            selected = filterActiveSelected,
            onClick = { filterActiveSelected = !filterActiveSelected },
            label = { Text(text = "Active") },
            leadingIcon =
                if (filterActiveSelected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                }
                } else {
                    null
                },
        )
        FilterChip(
            selected = filterArchivedSelected,
            onClick = { filterArchivedSelected = !filterArchivedSelected },
            label = { Text(text = "Archived") },
            leadingIcon =
                if (filterArchivedSelected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    }
                } else {
                    null
                },
        )
        FilterChip(
            selected = filterDeletedSelected,
            onClick = { filterDeletedSelected = !filterDeletedSelected },
            label = { Text(text = "Deleted") },
            leadingIcon =
                if (filterDeletedSelected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    }
                } else {
                    null
            },
        )
    }
}

@Preview
@Composable
internal fun EventListPreview(
    @PreviewParameter(EventListPreviewParameterProvider::class) state: State
) {
    // EventListImpl(
    //  onInteraction = {},
    //  stateflow = kotlinx.coroutines.flow.MutableStateFlow(state),
    //  eventUiState = flowOf(List<EventItem>(),
    //  selectedEventIds = emptyList(),
    // )
}

internal class EventListPreviewParameterProvider :
    CollectionPreviewParameterProvider<State>(
    listOf(
        State.Init,
        State.ShowEventsGrid(
            listOf(
                EventItem(
                    id = 0,
                    title = "Event 1",
                    description = "Event 1 Description",
                    date = LocalDate.now().plusDays(5),
                )
            )
        ),
    )
    )
