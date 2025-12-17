package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.model.EventItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EventRepository @Inject constructor(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val eventDataSource: EventDataSource
) {
    val activeEventsAsFlow: Flow<List<EventItem>> = eventDataSource.activeEventsAsFlow

    val archivedEventsAsFlow: Flow<List<EventItem>> = eventDataSource.archivedEventsAsFlow

    suspend fun getAllEvents(): List<EventItem> {
        return eventDataSource.getAllEvents()
    }

    suspend fun insertEvent(eventItem: EventItem) {
        eventDataSource.insertEvent(eventItem)
    }

    suspend fun deleteEvents(eventIds: List<Int>) {
        eventDataSource.deleteEvents(eventIds)
    }

    suspend fun archiveEvents(eventIds: List<Int>) {
        eventDataSource.archiveEvents(eventIds)
    }

    suspend fun unarchiveEvents(eventId: List<Int>) {
        eventDataSource.unarchiveEvents(eventId)
    }

    suspend fun insertEvents(eventItemList: List<EventItem>) {
        eventDataSource.insertEvents(eventItemList)
    }

    suspend fun getEventById(eventId: Int): EventItem {
        return eventDataSource.getEventById(eventId)
    }

    suspend fun getEventByIdFlow(eventId: Int): Flow<EventItem> = withContext(ioDispatcher) {
        return@withContext eventDataSource.getEventByIdFlow(eventId)
    }

    suspend fun getActiveEventsByIds(eventIds: List<Int>): List<EventItem> {
        return if (eventIds.isEmpty()) {
            emptyList()
        } else {
            eventDataSource.getActiveEventsByIds(eventIds)
        }
    }
}
