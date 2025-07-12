package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.model.EventItem
import kotlinx.coroutines.flow.Flow

class EventRepo(private val eventDao: EventDao) {
    val activeEventsAsFlow: Flow<List<EventItem>> = eventDao.getAllActiveEventsAsFlow()

    val archivedEventsAsFlow: Flow<List<EventItem>> = eventDao.getAllArchivedEventsAsFlow()

    suspend fun getAllEvents(): List<EventItem> {
        return eventDao.getAllEvents()
    }

    suspend fun insertEvent(eventItem: EventItem) {
        eventDao.upsertEvent(eventItem)
    }

    suspend fun deleteEvents(eventIds: List<Int>) {
        eventDao.deleteEvents(eventIds)
    }

    suspend fun archiveEvents(eventIds: List<Int>) {
        eventDao.archiveEvents(eventIds)
    }

    suspend fun unarchiveEvents(eventId: List<Int>) {
        eventDao.unarchiveEvents(eventId)
    }

    suspend fun insertEvents(eventItemList: List<EventItem>) {
        eventDao.upsertEvents(eventItemList)
    }

    suspend fun getEventById(eventId: Int): EventItem {
        return eventDao.getEventById(eventId)
    }

    suspend fun getEventsByIds(eventIds: List<Int>): List<EventItem> {
        return eventDao.getEventsByIds(eventIds)
    }

    suspend fun getActiveEventsByIds(eventIds: List<Int>): List<EventItem> {
        return if (eventIds.isEmpty()) {
            emptyList()
        } else {
            eventDao.getActiveEventsByIds(eventIds)
        }
    }
}
