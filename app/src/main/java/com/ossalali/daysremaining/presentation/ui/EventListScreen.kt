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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel.Interaction
import com.ossalali.daysremaining.presentation.viewmodel.SettingsViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun EventListScreen(
    viewModel: EventListViewModel =
        hiltViewModel(
            viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "EventListViewModel"
        ),
    settingsViewModel: SettingsViewModel =
        hiltViewModel(
            viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "SettingsViewModel"
        ),
    onNavigateToEventDetails: (Int) -> Unit = {},
    showFab: Boolean = false,
    selectedEventItems: ImmutableList<EventItem>,
) {
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val customDateNotation = settingsViewModel.customDateNotation.collectAsState().value

    EventListImpl(
        onInteraction = viewModel::onInteraction,
        eventUiState = viewModel.eventUiState,
        selectedEventItems = selectedEventItems,
        activeFilterEnabled = viewModel.activeFilterEnabled,
        archivedFilterEnabled = viewModel.archivedFilterEnabled,
        onNavigateToEventDetails = onNavigateToEventDetails,
        searchText = searchText,
        onSearchTextChanged = { text -> viewModel.onInteraction(Interaction.UpdateSearchText(text)) },
        showFab = showFab,
        customDateNotation = customDateNotation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventListImpl(
    onInteraction: (Interaction) -> Unit = {},
    eventUiState: StateFlow<ImmutableList<EventItem>>,
    selectedEventItems: ImmutableList<EventItem> = persistentListOf(),
    activeFilterEnabled: StateFlow<Boolean>,
    archivedFilterEnabled: StateFlow<Boolean>,
    onNavigateToEventDetails: (Int) -> Unit = {},
    searchText: String = "",
    onSearchTextChanged: (String) -> Unit = {},
    showFab: Boolean = false,
    customDateNotation: Boolean = false
) {
    val events by eventUiState.collectAsStateWithLifecycle()
    val activeFilterState by activeFilterEnabled.collectAsStateWithLifecycle()
    val archivedFilterState by archivedFilterEnabled.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .background(Color.Transparent)
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PaddingSize.default + PaddingSize.half),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AddChips(
                activeFilterEnabled = activeFilterState,
                archivedFilterEnabled = archivedFilterState,
                onToggleActiveFilter = { onInteraction(Interaction.ToggleActiveFilter) },
                onToggleArchivedFilter = { onInteraction(Interaction.ToggleArchivedFilter) },
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            EventListGrid(
                onEventItemClick = { eventItemId -> onNavigateToEventDetails(eventItemId) },
                onEventItemSelection = { onInteraction(Interaction.Select(it)) },
                events = events,
                selectedEventItems = selectedEventItems,
                customDateNotation = customDateNotation,
                modifier = Modifier.fillMaxSize(),
                contentPadding =
                    PaddingValues(
                        start = PaddingSize.default,
                        end = PaddingSize.default,
                        bottom = 80.dp,
                    ),
            )

            EventSearchBar(
                searchText = searchText,
                onSearchTextChanged = onSearchTextChanged,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(
                            start = PaddingSize.default,
                            end = if (showFab) 72.dp + PaddingSize.default else PaddingSize.default,
                            bottom = PaddingSize.default,
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
                            painter = painterResource(R.drawable.check_24px),
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    }
                } else {
                    null
                },
        )

        Spacer(Modifier.width(PaddingSize.quarter))

        FilterChip(
            selected = archivedFilterEnabled,
            onClick = onToggleArchivedFilter,
            label = { Text(text = "Archived") },
            leadingIcon =
                if (archivedFilterEnabled) {
                    {
                        Icon(
                            painter = painterResource(R.drawable.check_24px),
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

@PreviewLightDark
@Composable
fun EventListImplPreview() {
    MyAppTheme {
        Surface {
            EventListImpl(
                eventUiState = MutableStateFlow(persistentListOf()),
                activeFilterEnabled = MutableStateFlow(true),
                archivedFilterEnabled = MutableStateFlow(false),
            )
        }
    }
}