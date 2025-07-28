package com.ossalali.daysremaining.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.infrastructure.appLogger
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
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val eventRepo: EventRepo,
) : ViewModel() {

    private val _event = MutableStateFlow<EventItem?>(null)
    val event: StateFlow<EventItem?> = _event.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _isAddMode = MutableStateFlow(false)
    val isAddMode: StateFlow<Boolean> = _isAddMode.asStateFlow()

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges.asStateFlow()

    fun saveEvent(event: EventItem) {
        viewModelScope.launch(ioDispatcher) {
            _isSaving.value = true
            try {
                eventRepo.insertEvent(event)
                _event.value = event
                _hasChanges.value = false
            } catch (e: Exception) {
                appLogger().e(tag = TAG, message = "Couldn't save eventItem $event", throwable = e)
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun initializeForAddMode() {
        _isAddMode.value = true
        _event.value = null
        _isLoading.value = false
        _hasChanges.value = false
    }

    fun initializeForEditMode(eventId: Int) {
        _isAddMode.value = false
        _hasChanges.value = false
        loadEventById(eventId)
    }

    fun loadEventById(eventId: Int) {
        viewModelScope.launch(ioDispatcher) {
            _isLoading.value = true
            try {
                val loadedEvent = eventRepo.getEventById(eventId)
                _event.value = loadedEvent
            } catch (e: Exception) {
                appLogger().e(message = "Couldn't load eventItem with id $eventId", throwable = e)
                _event.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun trackChanges(hasChanges: Boolean) {
        if (!_isAddMode.value) {
            _hasChanges.value = hasChanges
        }
    }

    fun eventDeletedHandled() {
        _event.value = null
    }

    companion object {
        private const val TAG = "EventDetailsViewModel"
    }
}
