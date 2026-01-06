package com.ossalali.daysremaining.presentation.ui.v2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.infrastructure.EventRepository
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.ui.v2.model.EventUiModel
import com.ossalali.daysremaining.presentation.ui.v2.model.toEventUiModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventListViewModel @Inject constructor(private val eventRepository: EventRepository) :
    ViewModel() {

    private val _listState = MutableStateFlow<ListState>(ListState.Empty)
    val listState: StateFlow<ListState> = _listState.asStateFlow()

    private val _activeFilterEnabled = MutableStateFlow(true)
    val activeFilterEnabled = _activeFilterEnabled.asStateFlow()
    private val _archivedFilterEnabled = MutableStateFlow(false)
    val archivedFilterEnabled = _archivedFilterEnabled.asStateFlow()

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

    init {
        viewModelScope.launch {
            allEventsFlow
                .onStart { _listState.value = ListState.Loading }
                .collect { eventItems ->
                    if (eventItems.isEmpty()) {
                        _listState.value = ListState.Empty
                    } else {
                        _listState.value = ListState.Loaded(eventItems.toEventUiModels())
                    }
                }
        }
    }

    fun resetList() {
        _listState.value = ListState.Empty
    }

    fun toggleActiveFilter() {
        _activeFilterEnabled.value = !_activeFilterEnabled.value
    }

    fun toggleArchivedFilter() {
        _archivedFilterEnabled.value = !_archivedFilterEnabled.value
    }

    sealed interface ListState {
        data object Empty : ListState

        data class Loaded(val eventUiModels: ImmutableList<EventUiModel> = persistentListOf()) :
            ListState

        data object Loading : ListState

        data object Error : ListState
    }
}
