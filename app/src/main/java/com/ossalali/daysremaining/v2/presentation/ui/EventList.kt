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
import com.ossalali.daysremaining.presentation.event.EventDetails
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
) {
    LaunchedEffect(Unit) {
        viewModel.onInteraction(Interaction.Init)
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
    val state = stateflow.collectAsState()

    Scaffold(
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
                    EventListGrid(
                        onEventItemClick = { eventItemId ->
                            onInteraction(
                                Interaction.OpenEventItemDetails(
                                    eventItemId
                                )
                            )
                        },
                        onEventItemSelection = { onInteraction(Interaction.Select(it)) },
                        events = (state.value as State.ShowEventsGrid).eventItems,
                        selectedEventIds = selectedEventIds,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }

                is State.ShowAddEventScreen -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        AddEventScreen(
                            onEventCreated = { event ->
                                onInteraction(Interaction.EventItemAdded(event))
                            },
                            onCancel = {
                                onInteraction(Interaction.Init)
                            }
                        )

                        val eventsToShow = currentEventItems

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

                is State.EventClicked -> EventDetails(
                    event = (state.value as State.EventClicked).eventItem,
                    onBackClick = { onInteraction(Interaction.Init) }
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