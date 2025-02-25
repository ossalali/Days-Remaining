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

    fun deleteEvents(eventIds: List<Int>) {
        eventDao.deleteEvents(eventIds)
    }

    fun archiveEvents(eventIds: List<Int>) {
        eventDao.archiveEvents(eventIds)
    }

    fun insertEvents(eventList: MutableList<Event>) {
        eventList.forEach { event ->
            eventDao.insertEvent(event)
        }
    }

    suspend fun getFirstEvent(): Event {
        return eventDao.getFirstEvent()
    }
}