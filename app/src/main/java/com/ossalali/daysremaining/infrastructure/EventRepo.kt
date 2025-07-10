package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.model.EventItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class EventRepo(private val eventDao: EventDao) {
    val activeEventsAsFlow: Flow<List<EventItem>> = eventDao.getAllActiveEventsAsFlow()

    val archivedEventsAsFlow: Flow<List<EventItem>> = eventDao.getAllArchivedEventsAsFlow()

    val allEventsFlow: Flow<Pair<List<EventItem>, List<EventItem>>> =
        combine(activeEventsAsFlow, archivedEventsAsFlow) { active, archived ->
            active to archived
        }

    suspend fun getAllEvents(): List<EventItem> {
        return eventDao.getAllEvents()
    }

    suspend fun getAllActiveEvents(): List<EventItem> {
        return eventDao.getAllActiveEvents()
    }

    suspend fun getAllArchivedEvents(): List<EventItem> {
        return eventDao.getAllArchivedEvents()
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
