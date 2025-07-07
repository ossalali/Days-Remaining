package com.ossalali.daysremaining.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel.Interaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
open class EventListViewModel
@Inject
constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val eventRepo: EventRepo,
) : BaseViewModel<Interaction>() {
    val scope = CoroutineScope(ioDispatcher + SupervisorJob())

    private val _activeFilterEnabled = MutableStateFlow(true)
    val activeFilterEnabled: StateFlow<Boolean> = _activeFilterEnabled

    private val _archivedFilterEnabled = MutableStateFlow(false)
    val archivedFilterEnabled: StateFlow<Boolean> = _archivedFilterEnabled

    val eventUiState: StateFlow<List<EventItem>> =
        combine(
            eventRepo.activeEventsAsFlow,
            eventRepo.archivedEventsAsFlow,
            _activeFilterEnabled,
            _archivedFilterEnabled,
        ) { activeEvents, archivedEvents, showActive, showArchived ->
            val result = mutableListOf<EventItem>()
            if (showActive) result.addAll(activeEvents)
            if (showArchived) result.addAll(archivedEvents)
            result
        }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList(),
            )

    private val _selectedEventItemIds = mutableStateListOf<Int>()
    val selectedEventItemIds: List<Int>
        get() = _selectedEventItemIds

    private val _currentEventItems = mutableStateListOf<EventItem>()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _filteredEventsList = MutableStateFlow<List<EventItem>>(emptyList())
    val filteredEventsList: StateFlow<List<EventItem>> = _filteredEventsList

    override fun onInteraction(interaction: Interaction) {
        when (interaction) {
            is Interaction.Select -> handleEventItemSelection(interaction.eventId)
            is Interaction.SearchTextChanged -> onSearchTextChange(interaction.text)
            Interaction.ToggleSearch -> onToggleSearch()
            is Interaction.ToggleActiveFilter -> toggleActiveFilter()
            is Interaction.ToggleArchivedFilter -> toggleArchivedFilter()
        }
    }

    private fun toggleActiveFilter() {
        _activeFilterEnabled.value = !_activeFilterEnabled.value
    }

    private fun toggleArchivedFilter() {
        _archivedFilterEnabled.value = !_archivedFilterEnabled.value
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

    private fun handleEventItemSelection(eventItemId: Int) {
        if (_selectedEventItemIds.contains(eventItemId)) {
            _selectedEventItemIds.remove(eventItemId)
        } else {
            _selectedEventItemIds.add(eventItemId)
        }
    }

    fun archiveEvents(eventIds: List<Int>) {
        eventRepo.archiveEvents(eventIds)
    }

    fun deleteEvents(eventIds: List<Int>) {
        eventRepo.deleteEvents(eventIds)
    }

    sealed interface Interaction {

        data class Select(val eventId: Int) : Interaction

        data class SearchTextChanged(val text: String) : Interaction

        data object ToggleSearch : Interaction

        data object ToggleActiveFilter : Interaction

        data object ToggleArchivedFilter : Interaction
    }
}
