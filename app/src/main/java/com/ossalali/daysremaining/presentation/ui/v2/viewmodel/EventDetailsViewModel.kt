package com.ossalali.daysremaining.presentation.ui.v2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.infrastructure.EventRepository
import com.ossalali.daysremaining.presentation.ui.v2.eventdetails.PendingRemindersHolder
import com.ossalali.daysremaining.presentation.ui.v2.model.EventUiModel
import com.ossalali.daysremaining.presentation.ui.v2.model.toEvent
import com.ossalali.daysremaining.presentation.ui.v2.model.toEventUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val pendingRemindersHolder: PendingRemindersHolder
) :
    ViewModel() {
    private val _state = MutableStateFlow<DetailsState>(DetailsState.Loading)
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    fun init(eventId: Int) {
        viewModelScope.launch {
            val eventItem = eventRepository.getEventById(eventId)?.toEventUiModel()
            if (eventItem != null) {
                _state.value = DetailsState.Loaded(eventItem)
            } else {
                _state.value = DetailsState.Error
            }
        }
    }

    fun init() {
        viewModelScope.launch { _state.value = DetailsState.AddMode }
    }

    fun updateDate(date: LocalDate) {
        if (_state.value is DetailsState.Loaded) {
            (_state.value as DetailsState.Loaded).event.copy(date = date.toString())
        }
    }

    fun updateEvent(uiModel: EventUiModel) {
        val eventItem = uiModel.toEvent()
        viewModelScope.launch { eventRepository.upsertEvent(eventItem) }
    }

    fun deleteEvent(eventId: Int) {
        viewModelScope.launch { eventRepository.deleteEvent(eventId) }
    }

    fun archiveEvent(eventId: Int) {
        viewModelScope.launch { eventRepository.archiveEvent(eventId) }
    }

    fun unarchiveEvent(eventId: Int) {
        viewModelScope.launch { eventRepository.unarchiveEvent(eventId) }
    }

    sealed interface DetailsState {
        data object Loading : DetailsState

        data object AddMode : DetailsState

        data class Loaded(val event: EventUiModel) : DetailsState

        data object Error : DetailsState
    }
}
