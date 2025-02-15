package com.ossalali.daysremaining.presentation.event

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepo: EventRepo,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _selectedEventIds = mutableStateListOf<Int>()
    val selectedEventIds: List<Int> get() = _selectedEventIds

    fun toggleSelection(eventId: Int) {
        if (_selectedEventIds.contains(eventId)) {
            _selectedEventIds.remove(eventId)
        } else {
            _selectedEventIds.add(eventId)
        }
    }

    private val _allEvents: Flow<List<Event>> = eventRepo.allEventsAsFlow

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    val eventsList = searchText
        .combine(_allEvents) { text, events ->
            if (text.isBlank()) {
                @Suppress("UNUSED_EXPRESSION")
                events
            }
            events.filter { event ->
                event.title.contains(text.trim(), ignoreCase = true)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onToggleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            onSearchTextChange("")
        }
    }

    private val _confirmDeleteDialog = mutableStateOf(false)
    val confirmDeleteDialog: State<Boolean> = _confirmDeleteDialog

    private val _confirmArchiveDialog = mutableStateOf(false)
    val confirmArchiveDialog: State<Boolean> = _confirmArchiveDialog

    fun showArchiveDialog() {
        _confirmArchiveDialog.value = true
    }

    fun dismissArchiveDialog() {
        _confirmArchiveDialog.value = false
    }

    fun showDeleteDialog() {
        _confirmDeleteDialog.value = true
    }

    fun dismissDeleteDialog() {
        _confirmDeleteDialog.value = false
    }

    fun deleteEvents() {
        viewModelScope.launch(ioDispatcher) {
            eventRepo.deleteEvents(selectedEventIds)
            dismissDeleteDialog()
        }
    }

    fun archiveEvents() {
        viewModelScope.launch(ioDispatcher) {
            eventRepo.archiveEvents(selectedEventIds)
            dismissArchiveDialog()
        }
    }
}