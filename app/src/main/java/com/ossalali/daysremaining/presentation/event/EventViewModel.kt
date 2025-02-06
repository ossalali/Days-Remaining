package com.ossalali.daysremaining.presentation.event

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
    fun deleteEvent(event: Event) {
        viewModelScope.launch(ioDispatcher) {
            eventRepo.deleteEvent(event)
        }
    }

    val allEvents: Flow<List<Event>> = eventRepo.allEvents
}