package com.ossalali.daysremaining.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.EventItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val eventRepo: EventRepo
) : ViewModel() {

    private val _archivedEvents = MutableStateFlow<List<EventItem>>(emptyList())
    val archivedEvents: StateFlow<List<EventItem>> = _archivedEvents

    init {
        loadArchivedEvents()
    }

    private fun loadArchivedEvents() {
        viewModelScope.launch(ioDispatcher) {
            val events = eventRepo.getAllArchivedEvents()
            _archivedEvents.value = events
        }
    }

    fun unarchiveEvent(eventIds: List<Int>) {
        viewModelScope.launch(ioDispatcher) {
            eventRepo.unarchiveEvents(eventIds)
            loadArchivedEvents()
        }
    }

    fun deleteEvent(eventIds: List<Int>) {
        viewModelScope.launch(ioDispatcher) {
            eventRepo.deleteEvents(eventIds)
            loadArchivedEvents()
        }
    }
}