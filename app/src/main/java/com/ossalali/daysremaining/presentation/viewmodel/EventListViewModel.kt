package com.ossalali.daysremaining.presentation.viewmodel

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

    private val _selectedEventItems = MutableStateFlow<List<EventItem>>(emptyList())
    val selectedEventItems: StateFlow<List<EventItem>> = _selectedEventItems.asStateFlow()

    override fun onInteraction(interaction: Interaction) {
        when (interaction) {
            is Interaction.Select -> handleEventItemSelection(interaction.eventId)
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
        if (!_archivedFilterEnabled.value && !_activeFilterEnabled.value) {
            _activeFilterEnabled.value = true
        }
    }

    private fun handleEventItemSelection(eventId: Int) {
        val currentSelection = _selectedEventItems.value.toMutableList()
        val existingItem = currentSelection.find { item -> item.id == eventId }

        if (existingItem != null) {
            currentSelection.remove(existingItem)
            _selectedEventItems.value = currentSelection
        } else {
            launch(ioDispatcher) {
                val eventById = eventRepo.getEventById(eventId)
                currentSelection.add(eventById)
                _selectedEventItems.value = currentSelection
            }
        }
    }

    fun unarchiveEvents(eventItems: List<EventItem>) {
        viewModelScope.launch(ioDispatcher) {
            eventRepo.unarchiveEvents(eventItems.map { it.id })
            val currentSelection = _selectedEventItems.value.toMutableList()
            currentSelection.removeAll(eventItems)
            _selectedEventItems.value = currentSelection
        }
    }

    fun archiveEvents(eventItems: List<EventItem>) {
        viewModelScope.launch(ioDispatcher) {
            eventRepo.archiveEvents(eventItems.map { it.id })
            val currentSelection = _selectedEventItems.value.toMutableList()
            currentSelection.removeAll(eventItems)
            _selectedEventItems.value = currentSelection
        }
    }

    fun deleteEvents(eventItems: List<EventItem>) {
        viewModelScope.launch(ioDispatcher) {
            eventRepo.deleteEvents(eventItems.map { it.id })
            val currentSelection = _selectedEventItems.value.toMutableList()
            currentSelection.removeAll(eventItems)
            _selectedEventItems.value = currentSelection
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
    }
}
