package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel.Interaction
import kotlinx.coroutines.flow.StateFlow

/** Main event list screen that displays a grid of events and handles search functionality */
@Composable
internal fun EventListScreen(
  viewModel: EventListViewModel = hiltViewModel(),
  onNavigateToEventDetails: (Int) -> Unit = {},
  searchText: String = "",
  onSearchTextChanged: (String) -> Unit = {},
  focusRequester: FocusRequester = FocusRequester(),
  paddingValues: PaddingValues,
) {
    EventListImpl(
      modifier = Modifier.fillMaxWidth(),
      onInteraction = viewModel::onInteraction,
      eventUiState = viewModel.filteredEventsList,
      selectedEventItems = viewModel.selectedEventItems,
      activeFilterEnabled = viewModel.activeFilterEnabled,
      archivedFilterEnabled = viewModel.archivedFilterEnabled,
      onNavigateToEventDetails = onNavigateToEventDetails,
      onUnarchiveEvents = { eventItems -> viewModel.unarchiveEvents(eventItems) },
      onArchiveEventItems = { eventItems -> viewModel.archiveEvents(eventItems) },
      onDeleteEventItems = { eventItems -> viewModel.deleteEvents(eventItems.map { it.id }) },
      hasArchivedEventItems = viewModel.hasArchivedEventItems(),
      hasUnarchivedEventItems = viewModel.hasUnarchivedEventItems(),
      searchText = searchText,
      onSearchTextChanged = onSearchTextChanged,
      focusRequester = focusRequester,
      paddingValues = paddingValues,
    )
}

/** Implementation of the event list screen with all UI components */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventListImpl(
  modifier: Modifier = Modifier,
  onInteraction: (Interaction) -> Unit,
  eventUiState: StateFlow<List<EventItem>>,
  selectedEventItems: List<EventItem>,
  activeFilterEnabled: StateFlow<Boolean>,
  archivedFilterEnabled: StateFlow<Boolean>,
  onNavigateToEventDetails: (Int) -> Unit = {},
  onUnarchiveEvents: (List<EventItem>) -> Unit = {},
  onArchiveEventItems: (List<EventItem>) -> Unit = {},
  onDeleteEventItems: (List<EventItem>) -> Unit = {},
  hasArchivedEventItems: Boolean,
  hasUnarchivedEventItems: Boolean,
  searchText: String = "",
  onSearchTextChanged: (String) -> Unit = {},
  focusRequester: FocusRequester = FocusRequester(),
  paddingValues: PaddingValues,
) {
    val events by eventUiState.collectAsStateWithLifecycle()
    val activeFilterState by activeFilterEnabled.collectAsState()
    val archivedFilterState by archivedFilterEnabled.collectAsState()

    Scaffold(
      modifier = Modifier.padding(paddingValues),
      topBar = {
          Row(
            modifier = Modifier.fillMaxWidth().padding(Dimensions.quarter),
            verticalAlignment = Alignment.CenterVertically,
          ) {
              AddChips(
                activeFilterEnabled = activeFilterState,
                archivedFilterEnabled = archivedFilterState,
                onToggleActiveFilter = { onInteraction(Interaction.ToggleActiveFilter) },
                onToggleArchivedFilter = { onInteraction(Interaction.ToggleArchivedFilter) },
              )
              if (selectedEventItems.isNotEmpty()) {
                  Spacer(Modifier.weight(1f))
                  if (hasUnarchivedEventItems) {
                      IconButton(
                        modifier =
                          Modifier.border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = ShapeDefaults.Small,
                          ),
                        onClick = { onArchiveEventItems(selectedEventItems) },
                      ) {
                          Icon(
                            imageVector = Icons.Outlined.Archive,
                            contentDescription = "Archive selected Events",
                          )
                      }
                  }
                  if (hasArchivedEventItems) {
                      Spacer(Modifier.width(4.dp))
                      IconButton(
                        modifier =
                          Modifier.border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = ShapeDefaults.Small,
                          ),
                        onClick = { onUnarchiveEvents(selectedEventItems) },
                      ) {
                          Icon(
                            imageVector = Icons.Outlined.Inbox,
                            contentDescription = "Unarchive selected Events",
                          )
                      }
                  }
                  Spacer(Modifier.width(4.dp))
                  IconButton(
                    modifier =
                      Modifier.border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = ShapeDefaults.Small,
                      ),
                    onClick = { onDeleteEventItems(selectedEventItems) },
                  ) {
                      Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete selected Events",
                      )
                  }
              }
          }
      },
      bottomBar = {
          EventSearchBar(
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged,
            focusRequester = focusRequester,
            modifier = Modifier.imePadding().padding(bottom = Dimensions.default),
          )
      },
    ) { paddingValues ->
        Surface(modifier = modifier) {
            EventListGrid(
              onEventItemClick = { eventItemId -> onNavigateToEventDetails(eventItemId) },
              onEventItemSelection = { onInteraction(Interaction.Select(it)) },
              events = events,
              selectedEventItems = selectedEventItems,
              modifier = Modifier.fillMaxSize().padding(paddingValues),
            )
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
