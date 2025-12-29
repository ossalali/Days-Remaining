package com.ossalali.daysremaining.presentation.ui.v2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.infrastructure.EventRepository
import com.ossalali.daysremaining.presentation.ui.v2.model.EventUiModel
import com.ossalali.daysremaining.presentation.ui.v2.model.toEventUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(private val eventRepository: EventRepository) :
    ViewModel() {
    private val _state = MutableStateFlow<DetailsState>(DetailsState.Loading)
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    fun init(eventId: Int, addMode: Boolean) {
        if (addMode) {
            _state.value = DetailsState.AddMode
            return
        }

        viewModelScope.launch {
            val eventItem = eventRepository.getEventById(eventId)?.toEventUiModel()
            if (eventItem != null) {
                _state.value = DetailsState.Loaded(eventItem)
            } else {
                _state.value = DetailsState.Error
            }
        }
    }

    fun updateDate(date: LocalDate) {
        if (_state.value is DetailsState.Loaded) {
            (_state.value as DetailsState.Loaded).event.copy(date = date.toString())
        }
    }

    sealed interface DetailsState {
        data object Loading : DetailsState

        data class Loaded(val event: EventUiModel) : DetailsState

        data object Error : DetailsState

        data object Saving : DetailsState

        data object AddMode : DetailsState
    }
}
