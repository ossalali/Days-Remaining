package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.model.EventItem
import kotlinx.coroutines.flow.Flow

class EventRepo(private val eventDao: EventDao) {
    val activeEventsAsFlow: Flow<List<EventItem>> = eventDao.getAllActiveEventsAsFlow()

    val archivedEventsAsFlow: Flow<List<EventItem>> = eventDao.getAllArchivedEventsAsFlow()

    suspend fun getAllEvents(): List<EventItem> {
        return eventDao.getAllEvents()
    }

    suspend fun getAllActiveEvents(): List<EventItem> {
        return eventDao.getAllActiveEvents()
    }

    fun insertEvent(eventItem: EventItem) {
        eventDao.upsertEvent(eventItem)
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
        eventItemList.forEach { event -> eventDao.upsertEvent(event) }
    }

    suspend fun getEventById(eventId: Int): EventItem {
        return eventDao.getEventById(eventId)
    }

    suspend fun getEventsByIds(eventIds: List<Int>): List<EventItem> {
        return eventDao.getEventsByIds(eventIds)
    }
}
