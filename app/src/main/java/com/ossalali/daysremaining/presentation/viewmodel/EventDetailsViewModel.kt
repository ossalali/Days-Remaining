package com.ossalali.daysremaining.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IODispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.EventItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel
@Inject
constructor(
    private val eventRepo: EventRepo,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _event = MutableStateFlow<EventItem?>(null)
    val event: StateFlow<EventItem?> = _event.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    /** Permanently deletes the event with the given [eventId] from the database. */
    fun deleteEvent(eventId: Int) {
        viewModelScope.launch(ioDispatcher) {
            try {
                eventRepo.deleteEvents(listOf(eventId))
                _event.value = null // Clear currently loaded event
            } catch (_: Exception) {
                // Log the exception if necessary
            }
        }
    }

    /** Saves or updates the given [event] in the database. */
    fun saveEvent(event: EventItem) {
        viewModelScope.launch(ioDispatcher) {
            _isSaving.value = true
            try {
                eventRepo.insertEvent(event)
                _event.value = event
            } catch (e: Exception) {
                Log.e("EventDetailsViewModel", "Couldn't save eventItem $event", e)
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun loadEventById(eventId: Int) {
        viewModelScope.launch(ioDispatcher) {
            _isLoading.value = true
            try {
                val loadedEvent = eventRepo.getEventById(eventId)
                _event.value = loadedEvent
            } catch (_: Exception) {
                _event.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}
