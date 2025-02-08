package com.ossalali.daysremaining.presentation.event

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepo: EventRepo,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val allEvents: Flow<List<Event>> = eventRepo.allEvents


    private val _confirmDeleteDialog = mutableStateOf(false)
    val confirmDeleteDialog: State<Boolean> = _confirmDeleteDialog

    private val _currentEvent = mutableStateOf<Event?>(null)
    val currentEvent: State<Event?> = _currentEvent


    fun showDeleteDialog(event: Event) {
        _currentEvent.value = event
        _confirmDeleteDialog.value = true
    }

    fun dismissDeleteDialog() {
        _confirmDeleteDialog.value = false
    }

    fun deleteEvent() {
        _confirmDeleteDialog.value = false
        _currentEvent.value?.let { event ->
            viewModelScope.launch(ioDispatcher) {
                eventRepo.deleteEvent(event)
            }
        }
    }
}