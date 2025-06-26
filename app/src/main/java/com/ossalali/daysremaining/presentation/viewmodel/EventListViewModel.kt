package com.ossalali.daysremaining.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel.Event
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel.Interaction
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class EventListViewModel
@Inject
constructor(
    private val eventRepo: EventRepo,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel<State, Event, Interaction>(State.Init) {
    val scope = CoroutineScope(ioDispatcher + SupervisorJob())

    val eventUiState: StateFlow<List<EventItem>> =
        eventRepo.allActiveEventsAsFlow.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList(),
        )

    private val _selectedEventItemIds = mutableStateListOf<Int>()
    val selectedEventItemIds: List<Int>
        get() = _selectedEventItemIds

    private val _currentEventItems = mutableStateListOf<EventItem>()

    // Search functionality
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _filteredEventsList = MutableStateFlow<List<EventItem>>(emptyList())
    val filteredEventsList: StateFlow<List<EventItem>> = _filteredEventsList

    override fun onInteraction(interaction: Interaction) {
        when (interaction) {
            Interaction.Init -> showEvents()
            is Interaction.Select -> handleEventItemSelection(interaction.eventId)
            is Interaction.EventItemAdded -> eventItemAdded(interaction.eventItem)
            is Interaction.SearchTextChanged -> onSearchTextChange(interaction.text)
            Interaction.ToggleSearch -> onToggleSearch()
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
            _filteredEventsList.value =
                _currentEventItems.filter {
                    it.title.contains(
                        _searchText.value,
                        ignoreCase = true
                    )
                }
        }
    }

    private fun setActiveItems(eventItems: List<EventItem>) {
        _currentEventItems.clear()
        _currentEventItems.addAll(eventItems)
        filterEvents()
    }

    private fun eventItemAdded(eventItem: EventItem) {
        launch(ioDispatcher) {
            eventRepo.insertEvent(eventItem)
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
            setActiveItems(events)
            stateMutable.value = State.ShowEventsGrid(events)
        }
    }

    fun archiveEvents(eventIds: List<Int>) {
        eventRepo.archiveEvents(eventIds)
    }

    fun deleteEvents(eventIds: List<Int>) {
        eventRepo.deleteEvents(eventIds)
    }

    sealed interface State {
        data object Init : State

        data class ShowEventsGrid(val eventItems: List<EventItem>) : State
    }

    sealed interface Interaction {
        data object Init : Interaction

        data class EventItemAdded(val eventItem: EventItem) : Interaction

        data class Select(val eventId: Int) : Interaction

        data class SearchTextChanged(val text: String) : Interaction

        data object ToggleSearch : Interaction
    }

    sealed interface Event {}
}
