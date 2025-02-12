package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.model.Event
import kotlinx.coroutines.flow.Flow

class EventRepo(private val eventDao: EventDao) {
    val allEventsAsFlow: Flow<List<Event>> = eventDao.getAllEventsAsFlow()
    suspend fun getAllEvents(): List<Event> {
        return eventDao.getAllEvents()
    }

    fun insertEvent(event: Event) {
        eventDao.insertEvent(event)
    }

    fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event)
    }

    fun archiveEvents(events: List<Event>) {
        events.forEach { event ->
            eventDao.archiveEvent(event.id)
        }
    }

    fun insertEvents(eventList: MutableList<Event>) {
        eventList.forEach { event ->
            eventDao.insertEvent(event)
        }
    }
}