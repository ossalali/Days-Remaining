package com.ossalali.daysremaining.presentation.eventcreation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventCreationViewModel @Inject constructor(
    private val eventRepo: EventRepo,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    fun createEvent(title: String, date: String, description: String) {
        viewModelScope.launch(ioDispatcher) {
            val eventDate = java.time.LocalDate.parse(date)
            val event = Event(
                title = title,
                date = eventDate,
                description = description
            )
            eventRepo.insertEvent(event)
        }
    }
}
