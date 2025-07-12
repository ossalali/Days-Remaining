package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
  focusRequester: FocusRequester = FocusRequester(),
  paddingValues: PaddingValues,
  showFab: Boolean = false,
) {
    val selectedEventItems by viewModel.selectedEventItems.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()

    EventListImpl(
      onInteraction = viewModel::onInteraction,
      eventUiState = viewModel.eventUiState,
      selectedEventItems = selectedEventItems,
      activeFilterEnabled = viewModel.activeFilterEnabled,
      archivedFilterEnabled = viewModel.archivedFilterEnabled,
      onNavigateToEventDetails = onNavigateToEventDetails,
      onUnarchiveEvents = viewModel::unarchiveEvents,
      onArchiveEventItems = viewModel::archiveEvents,
      onDeleteEventItems = viewModel::deleteEvents,
      hasArchivedEventItems = viewModel.hasArchivedEventItems(),
      hasUnarchivedEventItems = viewModel.hasUnarchivedEventItems(),
      searchText = searchText,
      onSearchTextChanged = { text -> viewModel.onInteraction(Interaction.UpdateSearchText(text)) },
      focusRequester = focusRequester,
      paddingValues = paddingValues,
      showFab = showFab,
    )
}

/** Implementation of the event list screen with all UI components */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventListImpl(
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
  showFab: Boolean = false,
) {
    val events by eventUiState.collectAsStateWithLifecycle()
    val activeFilterState by activeFilterEnabled.collectAsStateWithLifecycle()
    val archivedFilterState by archivedFilterEnabled.collectAsStateWithLifecycle()

    Column(
      modifier =
        Modifier.fillMaxSize().padding(paddingValues).imePadding().background(Color.Transparent)
    ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = Dimensions.quarter),
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
                    IconButton(onClick = { onArchiveEventItems(selectedEventItems) }) {
                        Icon(
                          imageVector = Icons.Outlined.Archive,
                          contentDescription = "Archive selected Events",
                        )
                    }
                }
                if (hasArchivedEventItems) {
                    IconButton(onClick = { onUnarchiveEvents(selectedEventItems) }) {
                        Icon(
                          imageVector = Icons.Outlined.Inbox,
                          contentDescription = "Unarchive selected Events",
                        )
                    }
                }
                IconButton(onClick = { onDeleteEventItems(selectedEventItems) }) {
                    Icon(
                      imageVector = Icons.Filled.Delete,
                      contentDescription = "Delete selected Events",
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            EventListGrid(
              onEventItemClick = { eventItemId -> onNavigateToEventDetails(eventItemId) },
              onEventItemSelection = { onInteraction(Interaction.Select(it)) },
              events = events,
              selectedEventItems = selectedEventItems,
              modifier = Modifier.fillMaxSize().padding(horizontal = Dimensions.default),
            )

            EventSearchBar(
              searchText = searchText,
              onSearchTextChanged = onSearchTextChanged,
              focusRequester = focusRequester,
              modifier =
                Modifier.fillMaxWidth()
                  .align(Alignment.BottomCenter)
                  .padding(
                    start = Dimensions.default,
                    end = if (showFab) 72.dp + Dimensions.default else Dimensions.default,
                    bottom = Dimensions.default,
                  ),
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
