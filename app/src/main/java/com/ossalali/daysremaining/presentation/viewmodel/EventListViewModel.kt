package com.ossalali.daysremaining.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
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

    private val _selectedEventItems = mutableStateListOf<EventItem>()
    val selectedEventItems: List<EventItem>
        get() = _selectedEventItems

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    val filteredEventsList: StateFlow<List<EventItem>> =
      combine(eventUiState, _searchText) { events, searchText ->
            if (searchText.isEmpty()) {
                events
            } else {
                events.filter { it.title.contains(searchText, ignoreCase = true) }
            }
        }
        .stateIn(
          scope = scope,
          started = SharingStarted.WhileSubscribed(5000L),
          initialValue = emptyList(),
        )

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
        if (archivedFilterEnabled.value) {
            _activeFilterEnabled.value = !_activeFilterEnabled.value
        }
    }

    private fun toggleArchivedFilter() {
        _archivedFilterEnabled.value = !_archivedFilterEnabled.value
    }

    private fun onToggleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            _searchText.value = ""
        }
    }

    private fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    private fun handleEventItemSelection(eventId: Int) {
        val existingItem = _selectedEventItems.find { item -> item.id == eventId }
        if (existingItem != null) {
            _selectedEventItems.remove(existingItem)
        } else {
            launch {
                val eventById = eventRepo.getEventById(eventId)
                _selectedEventItems.add(eventById)
            }
        }
    }

    fun unarchiveEvents(eventItems: List<EventItem>) {
        viewModelScope.launch(ioDispatcher) {
            eventRepo.unarchiveEvents(eventItems.map { it.id })
            _selectedEventItems.removeAll(eventItems)
        }
    }

    fun archiveEvents(eventItems: List<EventItem>) {
        viewModelScope.launch(ioDispatcher) {
            eventRepo.archiveEvents(eventItems.map { it.id })
            _selectedEventItems.removeAll(eventItems)
        }
    }

    fun deleteEvents(eventIds: List<Int>) {
        viewModelScope.launch(ioDispatcher) { eventRepo.deleteEvents(eventIds) }
    }

    fun hasArchivedEventItems(): Boolean {
        return _selectedEventItems.map { it.isArchived }.contains(true)
    }

    fun hasUnarchivedEventItems(): Boolean {
        return _selectedEventItems.map { it.isArchived }.contains(false)
    }

    sealed interface Interaction {

        data class Select(val eventId: Int) : Interaction

        data class SearchTextChanged(val text: String) : Interaction

        data object ToggleSearch : Interaction

        data object ToggleActiveFilter : Interaction

        data object ToggleArchivedFilter : Interaction
    }
}
