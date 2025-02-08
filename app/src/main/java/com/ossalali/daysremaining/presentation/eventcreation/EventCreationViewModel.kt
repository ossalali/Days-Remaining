package com.ossalali.daysremaining.presentation.eventcreation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    var title by mutableStateOf("")
    var date by mutableStateOf("")
    var description by mutableStateOf("")
    var isEventCreated by mutableStateOf(false)

    fun onTitleChange(newTitle: String) {
        title = newTitle
    }

    fun onDateChange(newDate: String) {
        date = newDate
    }

    fun onDescriptionChange(newDescription: String) {
        description = newDescription
    }

    fun createEvent() {
        if (title.isBlank() || date.isBlank() || description.isBlank()) {
            return
        }

        viewModelScope.launch(ioDispatcher) {
            val eventDate = java.time.LocalDate.parse(date)
            val event = Event(
                title = title,
                date = eventDate,
                description = description
            )
            eventRepo.insertEvent(event)

            isEventCreated = true
        }
    }

    fun resetEventCreatedState() {
        title = ""
        date = ""
        description = ""
    }
}
