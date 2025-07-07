package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel.Interaction
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun EventListScreen(
    viewModel: EventListViewModel = hiltViewModel(),
    onNavigateToEventDetails: (Int) -> Unit = {},
) {
    EventListImpl(
        modifier = Modifier.fillMaxWidth(),
        onInteraction = viewModel::onInteraction,
        eventUiState = viewModel.eventUiState,
        selectedEvents = viewModel.selectedEventItems,
        activeFilterEnabled = viewModel.activeFilterEnabled,
        archivedFilterEnabled = viewModel.archivedFilterEnabled,
        onNavigateToEventDetails = onNavigateToEventDetails,
        onArchiveEvents = { viewModel.archiveEvents(it) },
        onDeleteEvents = { viewModel.deleteEvents(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EventListImpl(
    modifier: Modifier = Modifier,
    onInteraction: (Interaction) -> Unit,
    eventUiState: StateFlow<List<EventItem>>,
    selectedEvents: List<EventItem>,
    activeFilterEnabled: StateFlow<Boolean>,
    archivedFilterEnabled: StateFlow<Boolean>,
    onNavigateToEventDetails: (Int) -> Unit = {},
    onArchiveEvents: (List<Int>) -> Unit = {},
    onDeleteEvents: (List<Int>) -> Unit = {},
) {
    val events by eventUiState.collectAsStateWithLifecycle()
    val activeFilterState by activeFilterEnabled.collectAsState()
    val archivedFilterState by archivedFilterEnabled.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimensions.quarter),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AddChips(
                        activeFilterEnabled = activeFilterState,
                        archivedFilterEnabled = archivedFilterState,
                        onToggleActiveFilter = { onInteraction(Interaction.ToggleActiveFilter) },
                        onToggleArchivedFilter = { onInteraction(Interaction.ToggleArchivedFilter) },
                    )
                    if (selectedEvents.isEmpty().not()) {
                        Spacer(Modifier.weight(1f))
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(
                                onClick = { onArchiveEvents(selectedEvents.map { it.id }) },
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .widthIn(min = 32.dp, max = 64.dp)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline,
                                            shape = IconButtonDefaults.smallSquareShape,
                                        ),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Archive,
                                    contentDescription = "Archive selected Events",
                                )
                            }
                            Spacer(Modifier.width(4.dp))
                            IconButton(
                                onClick = { onArchiveEvents(selectedEvents.map { it.id }) },
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .widthIn(min = 32.dp, max = 64.dp)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline,
                                            shape = IconButtonDefaults.smallSquareShape,
                                        ),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Inbox,
                                    contentDescription = "Unarchive selected Events",
                                )
                            }
                            Spacer(Modifier.width(4.dp))
                            IconButton(
                                onClick = { onDeleteEvents(selectedEvents.map { it.id }) },
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .widthIn(min = 32.dp, max = 64.dp)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline,
                                            shape = IconButtonDefaults.smallSquareShape,
                                        ),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete selected Events",
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {},
        ) { paddingValues ->
            Surface(modifier = modifier) {
                EventListGrid(
                    onEventItemClick = { eventItemId -> onNavigateToEventDetails(eventItemId) },
                    onEventItemSelection = { onInteraction(Interaction.Select(it.id)) },
                    events = events,
                    selectedEvents = selectedEvents,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }
        }
    }
}

@Composable
fun AddChips(
    activeFilterEnabled: Boolean,
    archivedFilterEnabled: Boolean,
    onToggleActiveFilter: () -> Unit,
    onToggleArchivedFilter: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.Start) {
        FilterChip(
            selected = activeFilterEnabled,
            onClick = onToggleActiveFilter,
            label = { Text(text = "Active") },
            leadingIcon =
                if (activeFilterEnabled) {
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

        Spacer(Modifier.width(4.dp))

        FilterChip(
            selected = archivedFilterEnabled,
            onClick = onToggleArchivedFilter,
            label = { Text(text = "Archived") },
            leadingIcon =
                if (archivedFilterEnabled) {
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
