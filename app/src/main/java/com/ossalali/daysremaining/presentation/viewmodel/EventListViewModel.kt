package com.ossalali.daysremaining.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel.Interaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class EventListViewModel
@Inject
constructor(
  @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
  private val eventRepo: EventRepo,
) : BaseViewModel<Interaction>() {

    private val _activeFilterEnabled = MutableStateFlow(true)
    val activeFilterEnabled: StateFlow<Boolean> = _activeFilterEnabled

    private val _archivedFilterEnabled = MutableStateFlow(false)
    val archivedFilterEnabled: StateFlow<Boolean> = _archivedFilterEnabled

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val allEventsFlow: StateFlow<ImmutableList<EventItem>> =
      combine(
          eventRepo.activeEventsAsFlow,
          eventRepo.archivedEventsAsFlow,
          _activeFilterEnabled,
          _archivedFilterEnabled,
        ) { activeEvents, archivedEvents, showActive, showArchived ->
            val result = mutableListOf<EventItem>()
            if (showActive) result.addAll(activeEvents)
            if (showArchived) result.addAll(archivedEvents)
            result.toPersistentList()
        }
        .stateIn(
          scope = viewModelScope,
          started = SharingStarted.WhileSubscribed(5000L),
          initialValue = persistentListOf(),
        )

    val eventUiState: StateFlow<ImmutableList<EventItem>> =
      combine(allEventsFlow, _searchText) { events, searchQuery ->
            if (searchQuery.isEmpty()) {
                events.toImmutableList()
            } else {
                events
                  .filter { event ->
                      event.title.contains(searchQuery, ignoreCase = true) ||
                        event.description.contains(searchQuery, ignoreCase = true)
                  }
                  .toImmutableList()
            }
        }
        .stateIn(
          scope = viewModelScope,
          started = SharingStarted.WhileSubscribed(5000L),
          initialValue = persistentListOf(),
        )

    private val _selectedEventItems = MutableStateFlow<ImmutableList<EventItem>>(persistentListOf())
    val selectedEventItems: StateFlow<ImmutableList<EventItem>> = _selectedEventItems.asStateFlow()

    override fun onInteraction(interaction: Interaction) {
        when (interaction) {
            is Interaction.Select -> handleEventItemSelection(interaction.eventId)
            is Interaction.ToggleActiveFilter -> toggleActiveFilter()
            is Interaction.ToggleArchivedFilter -> toggleArchivedFilter()
            is Interaction.UpdateSearchText -> updateSearchText(interaction.searchText)
            Interaction.ClearSelection -> _selectedEventItems.value = persistentListOf()
            Interaction.SelectAll -> _selectedEventItems.value = allEventsFlow.value
        }
    }

    private fun updateSearchText(text: String) {
        _searchText.value = text
    }

    private fun toggleActiveFilter() {
        val selectedItemsAtStart = _selectedEventItems.value
        val allEventsFlowAtStart = allEventsFlow.value // Based on filters *before* this toggle operation

        // Check if "Select All" was effectively active before any change
        val wasSelectAllActive = selectedItemsAtStart.isNotEmpty() &&
                                selectedItemsAtStart.size == allEventsFlowAtStart.size &&
                                allEventsFlowAtStart.containsAll(selectedItemsAtStart)

        val previousActiveFilterState = _activeFilterEnabled.value

        // Original logic for toggling the active filter
        if (archivedFilterEnabled.value) {
            _activeFilterEnabled.value = !_activeFilterEnabled.value
        }
        // If archivedFilterEnabled.value is false, _activeFilterEnabled.value does not change here.

        val activeFilterDidChange = previousActiveFilterState != _activeFilterEnabled.value

        if (wasSelectAllActive && activeFilterDidChange) {
            // _activeFilterEnabled has changed, so allEventsFlow.value will now reflect the new filter state
            _selectedEventItems.value = allEventsFlow.value
        }
    }

    private fun toggleArchivedFilter() {
        val selectedItemsAtStart = _selectedEventItems.value
        val allEventsFlowAtStart = allEventsFlow.value // Based on filters *before* this toggle operation

        // Check if "Select All" was effectively active before any change
        val wasSelectAllActive = selectedItemsAtStart.isNotEmpty() &&
                                selectedItemsAtStart.size == allEventsFlowAtStart.size &&
                                allEventsFlowAtStart.containsAll(selectedItemsAtStart)

        val previousArchivedFilterState = _archivedFilterEnabled.value
        val previousActiveFilterState = _activeFilterEnabled.value // Active filter might also change

        // Original logic for toggling the archived filter
        _archivedFilterEnabled.value = !_archivedFilterEnabled.value
        if (!_archivedFilterEnabled.value && !_activeFilterEnabled.value) {
            // This ensures at least one filter remains active, potentially changing _activeFilterEnabled
            _activeFilterEnabled.value = true
        }

        val archivedFilterDidChange = previousArchivedFilterState != _archivedFilterEnabled.value
        val activeFilterDidChangeByThisOperation = previousActiveFilterState != _activeFilterEnabled.value
        val anyRelevantFilterChanged = archivedFilterDidChange || activeFilterDidChangeByThisOperation

        if (wasSelectAllActive && anyRelevantFilterChanged) {
            // _archivedFilterEnabled (and possibly _activeFilterEnabled) has changed,
            // so allEventsFlow.value will reflect the new filter state
            _selectedEventItems.value = allEventsFlow.value
        }
    }

    private fun handleEventItemSelection(eventId: Int) {
        val currentSelection = _selectedEventItems.value.toMutableList()
        val existingItem = currentSelection.find { item -> item.id == eventId }

        if (existingItem != null) {
            currentSelection.remove(existingItem)
            _selectedEventItems.value = currentSelection.toPersistentList()
        } else {
            viewModelScope.launch(ioDispatcher) {
                val eventById = eventRepo.getEventById(eventId)
                currentSelection.add(eventById)
                _selectedEventItems.value = currentSelection.toPersistentList()
            }
        }
    }

    fun unarchiveEvents(eventItems: ImmutableList<EventItem>) {
        viewModelScope.launch(ioDispatcher) {
            eventRepo.unarchiveEvents(eventItems.map { it.id })
            val currentSelection = _selectedEventItems.value.toMutableList()
            currentSelection.removeAll(eventItems)
            _selectedEventItems.value = currentSelection.toPersistentList()
        }
    }

    fun archiveEvents(eventItems: ImmutableList<EventItem>) {
        viewModelScope.launch(ioDispatcher) {
            eventRepo.archiveEvents(eventItems.map { it.id })
            val currentSelection = _selectedEventItems.value.toMutableList()
            currentSelection.removeAll(eventItems)
            _selectedEventItems.value = currentSelection.toPersistentList()
        }
    }

    fun deleteEvents(eventItems: ImmutableList<EventItem>) {
        viewModelScope.launch(ioDispatcher) {
            eventRepo.deleteEvents(eventItems.map { it.id })
            val currentSelection = _selectedEventItems.value.toMutableList()
            currentSelection.removeAll(eventItems)
            _selectedEventItems.value = currentSelection.toPersistentList()
        }
    }

    fun hasArchivedEventItems(): Boolean {
        return _selectedEventItems.value.any { it.isArchived }
    }

    fun hasUnarchivedEventItems(): Boolean {
        return _selectedEventItems.value.any { !it.isArchived }
    }

    sealed interface Interaction {

        data class Select(val eventId: Int) : Interaction

        data object ToggleActiveFilter : Interaction

        data object ToggleArchivedFilter : Interaction

        data class UpdateSearchText(val searchText: String) : Interaction

        data object ClearSelection : Interaction
        data object SelectAll : Interaction
    }
}
