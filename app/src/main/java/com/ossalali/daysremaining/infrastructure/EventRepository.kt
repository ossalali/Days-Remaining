package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.model.EventItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EventRepository
@Inject
constructor(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val eventDataSource: EventDataSource,
) {
    val activeEventsAsFlow: Flow<List<EventItem>> = eventDataSource.activeEventsAsFlow

    val archivedEventsAsFlow: Flow<List<EventItem>> = eventDataSource.archivedEventsAsFlow

    suspend fun getAllEvents(): List<EventItem> {
        return eventDataSource.getAllEvents()
    }

    suspend fun upsertEvent(eventItem: EventItem) =
        withContext(ioDispatcher) { eventDataSource.upsertEvent(eventItem) }

    suspend fun deleteEvents(eventIds: List<Int>) =
        withContext(ioDispatcher) { eventDataSource.deleteEvents(eventIds) }

    suspend fun deleteEvent(eventId: Int) =
        withContext(ioDispatcher) { eventDataSource.deleteEvent(eventId) }

    suspend fun archiveEvent(eventId: Int) =
        withContext(ioDispatcher) { eventDataSource.archiveEvent(eventId) }

    suspend fun archiveEvents(eventIds: List<Int>) =
        withContext(ioDispatcher) { eventDataSource.archiveEvents(eventIds) }

    suspend fun unarchiveEvent(eventId: Int) =
        withContext(ioDispatcher) { eventDataSource.unarchiveEvent(eventId) }

    suspend fun unarchiveEvents(eventId: List<Int>) =
        withContext(ioDispatcher) { eventDataSource.unarchiveEvents(eventId) }

    suspend fun insertEvents(eventItemList: List<EventItem>) =
        withContext(ioDispatcher) { eventDataSource.insertEvents(eventItemList) }

    suspend fun getEventById(eventId: Int): EventItem? =
        withContext(ioDispatcher) {
            return@withContext eventDataSource.getEventById(eventId)
        }

    suspend fun getEventByIdFlow(eventId: Int): Flow<EventItem> =
        withContext(ioDispatcher) {
            return@withContext eventDataSource.getEventByIdFlow(eventId)
        }

    suspend fun getActiveEventsByIds(eventIds: List<Int>): List<EventItem> =
        withContext(ioDispatcher) {
            return@withContext if (eventIds.isEmpty()) {
                emptyList()
            } else {
                eventDataSource.getActiveEventsByIds(eventIds)
            }
        }
}
