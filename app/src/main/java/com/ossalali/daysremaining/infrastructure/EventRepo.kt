package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.model.EventItem
import kotlinx.coroutines.flow.Flow

class EventRepo(private val eventDao: EventDao) {
    val allEventsAsFlow: Flow<List<EventItem>> = eventDao.getAllEventsAsFlow()

    suspend fun getAllEvents(): List<EventItem> {
        return eventDao.getAllEvents()
    }

    suspend fun getAllActiveEvents(): List<EventItem> {
        return eventDao.getAllActiveEvents()
    }

    suspend fun getAllArchivedEvents(): List<EventItem> {
        return eventDao.getAllArchivedEvents()
    }

    fun insertEvent(eventItem: EventItem) {
        eventDao.insertEvent(eventItem)
    }

    fun deleteEvents(eventIds: List<Int>) {
        eventDao.deleteEvents(eventIds)
    }

    fun archiveEvents(eventIds: List<Int>) {
        eventDao.archiveEvents(eventIds)
    }

    fun unarchiveEvents(eventId: List<Int>) {
        eventDao.unarchiveEvents(eventId)
    }

    fun insertEvents(eventItemList: List<EventItem>) {
        eventItemList.forEach { event ->
            eventDao.insertEvent(event)
        }
    }

    suspend fun getFirstEvent(): EventItem {
        return eventDao.getFirstEvent()
    }

    suspend fun getEventById(eventId: Int): EventItem {
        return eventDao.getEventById(eventId)
    }
}