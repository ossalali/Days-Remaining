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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val eventRepo: EventRepo,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _event = MutableStateFlow<EventItem?>(null)
    val event: StateFlow<EventItem?> = _event.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

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

    fun updateEvent(event: EventItem) {
        viewModelScope.launch(ioDispatcher) {
            _isSaving.value = true
            try {
                eventRepo.insertEvent(event) // Room's @Insert with REPLACE strategy handles updates
                _event.value = event
            } catch (_: Exception) {
            } finally {
                _isSaving.value = false
            }
        }
    }
}