package com.ossalali.daysremaining.v2.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.Event
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.Interaction
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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

    override fun onInteraction(interaction: Interaction) {
        when (interaction) {
            Interaction.Init -> showEvents()
            is Interaction.AddEventItem -> addEventItem()
            is Interaction.Select -> handleEventItemSelection(interaction.eventId)
            is Interaction.OpenEventItemDetails -> TODO()
            is Interaction.EventItemAdded -> eventItemAdded(interaction.eventItem)
        }
    }

    private fun eventItemAdded(eventItem: EventItem) {
        launch(ioDispatcher) {
            eventRepo.insertEvent(eventItem)
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
            stateMutable.value = State.ShowEventsGrid(events)
        }
    }

    private fun addEventItem() {
        stateMutable.value = State.ShowAddEventScreen
    }

    sealed interface State {
        data object Init : State
        data object ShowAddEventScreen : State
        data class ShowEventsGrid(val eventItems: List<EventItem>) : State
        data class Selected(val eventItemId: Int) : State
    }

    sealed interface Interaction {
        data object Init : Interaction
        data object AddEventItem : Interaction
        data class EventItemAdded(val eventItem: EventItem) : Interaction
        data class OpenEventItemDetails(val eventId: Int) : Interaction
        data class Select(val eventId: Int) : Interaction
    }

    sealed interface Event {
        data class EventItemCreated(val eventItem: EventItem) : Event
        data class EventItemDeleted(val eventId: Int) : Event
        data class EventItemArchived(val eventId: Int) : Event
    }
}