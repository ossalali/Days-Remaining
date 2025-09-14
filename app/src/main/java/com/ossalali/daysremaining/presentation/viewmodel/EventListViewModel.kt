package com.ossalali.daysremaining.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepository
import com.ossalali.daysremaining.infrastructure.appLogger
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel.Interaction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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

@HiltViewModel
open class EventListViewModel
@Inject
constructor(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val eventRepository: EventRepository,
) : BaseViewModel<Interaction>() {

    private val _activeFilterEnabled = MutableStateFlow(true)
    val activeFilterEnabled: StateFlow<Boolean> = _activeFilterEnabled

    private val _archivedFilterEnabled = MutableStateFlow(false)
    val archivedFilterEnabled: StateFlow<Boolean> = _archivedFilterEnabled

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _pendingDeleteEvents =
        MutableStateFlow<ImmutableList<EventItem>>(persistentListOf())
    val pendingDeleteEvents: StateFlow<ImmutableList<EventItem>> =
        _pendingDeleteEvents.asStateFlow()

    private val allEventsFlow: StateFlow<ImmutableList<EventItem>> =
      combine(
          eventRepository.activeEventsAsFlow,
          eventRepository.archivedEventsAsFlow,
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
      combine(allEventsFlow, _searchText, _pendingDeleteEvents) { events,
                                                                  searchQuery,
                                                                  pendingDeletes ->
            val eventsToShow =
                events.filterNot { event -> pendingDeletes.any { pd -> pd.id == event.id } }
            if (searchQuery.isEmpty()) {
                eventsToShow.toImmutableList()
            } else {
                eventsToShow
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
            is Interaction.UndoDelete -> undoSpecificDeletions(interaction.itemsToUndo)
            Interaction.ClearSelection -> _selectedEventItems.value = persistentListOf()
            Interaction.SelectAll -> _selectedEventItems.value = eventUiState.value
            is Interaction.ConfirmDeletions -> commitSpecificDeletions(interaction.itemsToConfirm)
        }
    }

    private fun setPendingDeleteEvents(eventItems: ImmutableList<EventItem>) {
        _pendingDeleteEvents.value = eventItems
    }

    private fun commitSpecificDeletions(itemsToCommit: ImmutableList<EventItem>) {
        if (itemsToCommit.isEmpty()) return

        viewModelScope.launch(ioDispatcher) {
            try {
                eventRepository.deleteEvents(itemsToCommit.map { it.id })

                if (_pendingDeleteEvents.value == itemsToCommit) {
                    _pendingDeleteEvents.value = persistentListOf()
                }
            } catch (e: Exception) {
                appLogger()
                    .e(
                        tag = TAG,
                        message = "failed to delete events: ${itemsToCommit.map { it.id }}",
                        throwable = e,
                    )
            }
        }
    }

    private fun undoSpecificDeletions(itemsToUndo: ImmutableList<EventItem>) {
        if (_pendingDeleteEvents.value == itemsToUndo) {
            _pendingDeleteEvents.value = persistentListOf()
        }
    }

    private fun updateSearchText(text: String) {
        _searchText.value = text
    }

    private fun toggleActiveFilter() {
        val selectedItemsAtStart = _selectedEventItems.value
        val eventUiStateAtStart = eventUiState.value

        val wasSelectAllActive =
            selectedItemsAtStart.isNotEmpty() &&
            selectedItemsAtStart.size == eventUiStateAtStart.size &&
            eventUiStateAtStart.containsAll(selectedItemsAtStart)

        val previousActiveFilterState = _activeFilterEnabled.value

        if (archivedFilterEnabled.value) {
            _activeFilterEnabled.value = !_activeFilterEnabled.value
        }

        val activeFilterDidChange = previousActiveFilterState != _activeFilterEnabled.value

        if (wasSelectAllActive && activeFilterDidChange) {
            _selectedEventItems.value = eventUiState.value
        }
    }

    private fun toggleArchivedFilter() {
        val selectedItemsAtStart = _selectedEventItems.value
        val eventUiStateAtStart = eventUiState.value

        val wasSelectAllActive =
            selectedItemsAtStart.isNotEmpty() &&
            selectedItemsAtStart.size == eventUiStateAtStart.size &&
            eventUiStateAtStart.containsAll(selectedItemsAtStart)

        val previousArchivedFilterState = _archivedFilterEnabled.value
        val previousActiveFilterState = _activeFilterEnabled.value

        _archivedFilterEnabled.value = !_archivedFilterEnabled.value
        if (!_archivedFilterEnabled.value && !_activeFilterEnabled.value) {
            _activeFilterEnabled.value = true
        }

        val archivedFilterDidChange = previousArchivedFilterState != _archivedFilterEnabled.value
        val activeFilterDidChangeByThisOperation =
            previousActiveFilterState != _activeFilterEnabled.value
        val anyRelevantFilterChanged =
            archivedFilterDidChange || activeFilterDidChangeByThisOperation

        if (wasSelectAllActive && anyRelevantFilterChanged) {
            _selectedEventItems.value = eventUiState.value
        }
    }

    private fun handleEventItemSelection(eventId: Int) {
        val currentSelection = _selectedEventItems.value.toMutableList()
        val eventInUi = eventUiState.value.find { it.id == eventId } ?: return

        val existingItem = currentSelection.find { item -> item.id == eventId }

        if (existingItem != null) {
            currentSelection.remove(existingItem)
            _selectedEventItems.value = currentSelection.toPersistentList()
        } else {
            eventInUi.let {
                currentSelection.add(it)
                _selectedEventItems.value = currentSelection.toPersistentList()
            }
        }
    }

    fun unarchiveEvents(eventItems: ImmutableList<EventItem>) {
        viewModelScope.launch(ioDispatcher) {
            eventRepository.unarchiveEvents(eventItems.map { it.id })
            val currentSelection = _selectedEventItems.value.toMutableList()
            currentSelection.removeAll(eventItems)
            _selectedEventItems.value = currentSelection.toPersistentList()
        }
    }

    fun archiveEvents(eventItems: ImmutableList<EventItem>) {
        viewModelScope.launch(ioDispatcher) {
            eventRepository.archiveEvents(eventItems.map { it.id })
            val currentSelection = _selectedEventItems.value.toMutableList()
            currentSelection.removeAll(eventItems)
            _selectedEventItems.value = currentSelection.toPersistentList()
        }
    }

    fun deleteEvents(eventItems: ImmutableList<EventItem>) {
        if (eventItems.isNotEmpty()) {
            setPendingDeleteEvents(eventItems)
            _selectedEventItems.value =
                _selectedEventItems.value
                    .filterNot { selectedItem -> eventItems.any { it.id == selectedItem.id } }
                    .toPersistentList()
        }
    }

    fun deleteEvent(eventItem: EventItem) {
        val itemsToStage = persistentListOf(eventItem)
        setPendingDeleteEvents(itemsToStage)
        _selectedEventItems.value =
            _selectedEventItems.value.filterNot { it.id == eventItem.id }.toPersistentList()
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

        data class UndoDelete(val itemsToUndo: ImmutableList<EventItem>) : Interaction

        data object ClearSelection : Interaction

        data object SelectAll : Interaction

        data class ConfirmDeletions(val itemsToConfirm: ImmutableList<EventItem>) : Interaction
    }

    companion object {
        private const val TAG = "EventListViewModel"
    }
}
