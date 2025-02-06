package com.ossalali.numbered.presentation

import androidx.lifecycle.ViewModel
import com.ossalali.numbered.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor() : ViewModel() {
    // You could add repository or other dependencies via constructor injection.

    fun createEvent(title: String, date: String, description: String): Event? {
        return try {
            val eventDate = java.time.LocalDate.parse(date)
            Event(
                id = (1..1_000_000).random(),
                title = title,
                date = eventDate,
                description = description
            )
        } catch (e: java.time.format.DateTimeParseException) {
            null // or handle error accordingly.
        }
    }
}
