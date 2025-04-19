package com.ossalali.daysremaining.v2.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.mainscreen.Destinations
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.Event
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.Interaction
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class EventListViewModel @Inject constructor(
    private val eventRepo: EventRepo,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<State, Event, Interaction>(State.Init) {

    private val _selectedEventItemIds = mutableStateListOf<Int>()
    val selectedEventItemIds: List<Int>
        get() = _selectedEventItemIds

    private val _currentEventItems = mutableStateListOf<EventItem>()
    val currentEventItems: List<EventItem>
        get() = _currentEventItems

    // Search functionality
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _filteredEventsList = MutableStateFlow<List<EventItem>>(emptyList())
    val filteredEventsList: StateFlow<List<EventItem>> = _filteredEventsList

    // Navigation Events
    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent: SharedFlow<String> = _navigationEvent

    override fun onInteraction(interaction: Interaction) {
        when (interaction) {
            Interaction.Init -> showEvents()
            is Interaction.AddEventItem -> addEventItem()
            is Interaction.Select -> handleEventItemSelection(interaction.eventId)
            is Interaction.OpenEventItemDetails -> handleEventItemClick(interaction.eventId)
            is Interaction.EventItemAdded -> eventItemAdded(interaction.eventItem)
            is Interaction.SearchTextChanged -> onSearchTextChange(interaction.text)
            Interaction.ToggleSearch -> onToggleSearch()
        }
    }

    private fun handleEventItemClick(eventId: Int) {
        // Instead of changing state, emit a navigation event
        viewModelScope.launch {
            _navigationEvent.emit(Destinations.eventDetailsRoute(eventId))
        }
    }

    private fun onToggleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            _searchText.value = ""
            filterEvents()
        }
    }

    private fun onSearchTextChange(text: String) {
        _searchText.value = text
        filterEvents()
    }

    private fun filterEvents() {
        if (_searchText.value.isEmpty()) {
            _filteredEventsList.value = _currentEventItems
        } else {
            _filteredEventsList.value = _currentEventItems.filter {
                it.title.contains(_searchText.value, ignoreCase = true)
            }
        }
    }

    private fun setCurrentEventItems(eventItems: List<EventItem>) {
        _currentEventItems.clear()
        _currentEventItems.addAll(eventItems)
        filterEvents() // Update filtered list when raw data changes
    }

    private fun eventItemAdded(eventItem: EventItem) {
        launch(ioDispatcher) {
            eventRepo.insertEvent(eventItem)
            // After adding the event, refresh the events list
            showEvents()
        }
    }

    private fun handleEventItemSelection(eventItemId: Int) {
        if (_selectedEventItemIds.contains(eventItemId)) {
            _selectedEventItemIds.remove(eventItemId)
        } else {
            _selectedEventItemIds.add(eventItemId)
        }
    }

    private fun showEvents() {
        viewModelScope.launch(ioDispatcher) {
            val events = eventRepo.getAllActiveEvents()
            setCurrentEventItems(events)
            stateMutable.value = State.ShowEventsGrid(events)
        }
    }

    private fun addEventItem() {
        // Save current events if we have them
        val currentState = stateMutable.value
        if (currentState is State.ShowEventsGrid) {
            // Create a custom ShowAddEventScreen with the current events
            stateMutable.value = State.ShowAddEventScreen
        } else {
            // Just show the add screen and load events
            stateMutable.value = State.ShowAddEventScreen
            // Also ensure we load the events
            showEvents()
        }
    }

    sealed interface State {
        data object Init : State
        data object ShowAddEventScreen : State
        data class ShowEventsGrid(val eventItems: List<EventItem>) : State
    }

    sealed interface Interaction {
        data object Init : Interaction
        data object AddEventItem : Interaction
        data class EventItemAdded(val eventItem: EventItem) : Interaction
        data class OpenEventItemDetails(val eventId: Int) : Interaction
        data class Select(val eventId: Int) : Interaction
        data class SearchTextChanged(val text: String) : Interaction
        data object ToggleSearch : Interaction
    }

    sealed interface Event {
        data class EventItemDeleted(val eventId: Int) : Event
        data class EventItemArchived(val eventId: Int) : Event
    }
}