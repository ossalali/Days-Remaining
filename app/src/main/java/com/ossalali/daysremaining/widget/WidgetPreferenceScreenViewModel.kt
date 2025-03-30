package com.ossalali.daysremaining.widget

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.EventItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WidgetPreferenceScreenViewModel @Inject constructor(
    private val eventRepo: EventRepo
) : ViewModel() {
    fun getEvents(): StateFlow<List<EventItem>> {
        return eventRepo.allEventsAsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    private val _selectedEventIds = mutableStateListOf<Int>()
    val selectedEventIds: List<Int> get() = _selectedEventIds

    fun toggleSelection(eventId: Int) {
        if (_selectedEventIds.contains(eventId)) {
            _selectedEventIds.remove(eventId)
        } else {
            _selectedEventIds.add(eventId)
        }
    }
}