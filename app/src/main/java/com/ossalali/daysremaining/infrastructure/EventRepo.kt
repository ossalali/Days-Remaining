package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.model.Event
import kotlinx.coroutines.flow.Flow

class EventRepo(private val eventDao: EventDao) {
    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

    fun insertEvent(event: Event) {
        eventDao.insertEvent(event)
    }

    fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event)
    }
}