package com.ossalali.daysremaining.v2.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.topbar.TopAppBarWithSearch
import com.ossalali.daysremaining.v2.presentation.ui.theme.DefaultPreviews
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.Event
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.Interaction
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import java.time.LocalDate

@Composable
internal fun EventList(
    modifier: Modifier = Modifier,
    viewModel: EventListViewModel = hiltViewModel(),
    onDrawerClick: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        // to refresh the events on showing this screen again
        viewModel.onInteraction(Interaction.Init)
    }

    EventListImpl(
        onInteraction = viewModel::onInteraction,
        stateflow = viewModel.state,
        eventsFlow = viewModel.events,
        selectedEventIds = viewModel.selectedEventItemIds,
        currentEventItems = viewModel.currentEventItems,
        isSearching = viewModel.isSearching.collectAsState().value,
        searchText = viewModel.searchText.collectAsState().value,
        filteredEventsList = viewModel.filteredEventsList.collectAsState().value,
        onDrawerClick = onDrawerClick,
        modifier = modifier
    )
}

@Composable
private fun EventListImpl(
    onInteraction: (Interaction) -> Unit,
    stateflow: StateFlow<State>,
    eventsFlow: Flow<Event>,
    selectedEventIds: List<Int>,
    currentEventItems: List<EventItem>,
    isSearching: Boolean,
    searchText: String,
    filteredEventsList: List<EventItem>,
    onDrawerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CollectEvents(eventsFlow) { event ->
        when (event) {
            is Event.EventItemArchived -> TODO()
            is Event.EventItemDeleted -> TODO()
        }
    }
    val state = stateflow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBarWithSearch(
                isSearching = isSearching,
                searchText = searchText,
                onSearchTextChange = { onInteraction(Interaction.SearchTextChanged(it)) },
                onStartSearch = { onInteraction(Interaction.ToggleSearch) },
                onCloseSearch = { onInteraction(Interaction.ToggleSearch) },
                onDrawerClick = onDrawerClick,
                eventsList = if (isSearching) {
                    filteredEventsList.toMutableList()
                } else {
                    currentEventItems.toMutableList()
                },
                selectedEventIds = selectedEventIds,
                onArchive = { /* TODO: Implement archive functionality */ },
                onDelete = { /* TODO: Implement delete functionality */ }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onInteraction(Interaction.AddEventItem)
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Event")
            }
        }
    ) { paddingValues ->
        Surface(modifier = modifier) {
            when (state.value) {
                State.Init -> onInteraction(Interaction.Init)
                is State.ShowEventsGrid -> {
                    val eventsToShow = if (isSearching) {
                        filteredEventsList
                    } else {
                        (state.value as State.ShowEventsGrid).eventItems
                    }

                    EventListGrid(
                        onEventItemClick = { onInteraction(Interaction.OpenEventItemDetails(it)) },
                        onEventItemSelection = { onInteraction(Interaction.Select(it)) },
                        events = eventsToShow,
                        selectedEventIds = selectedEventIds,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = paddingValues.calculateTopPadding()
                            )
                    )
                }

                is State.Selected -> TODO()
                is State.ShowAddEventScreen -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = paddingValues.calculateTopPadding()
                            )
                    ) {
                        AddEventScreen(
                            onEventCreated = { event ->
                                onInteraction(Interaction.EventItemAdded(event))
                            },
                            onCancel = {
                                onInteraction(Interaction.Init)
                            }
                        )

                        val eventsToShow = if (isSearching) {
                            filteredEventsList
                        } else {
                            currentEventItems
                        }

                        EventListGrid(
                            onEventItemClick = { onInteraction(Interaction.OpenEventItemDetails(it)) },
                            onEventItemSelection = { onInteraction(Interaction.Select(it)) },
                            events = eventsToShow,
                            selectedEventIds = selectedEventIds,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }
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
        currentEventItems = emptyList(),
        isSearching = false,
        searchText = "",
        filteredEventsList = emptyList(),
        onDrawerClick = {}
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
        ),
        State.Selected(0)
    )
)