package com.ossalali.daysremaining.infrastructure

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ossalali.daysremaining.model.EventItem
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Upsert
    fun upsertEvent(eventItem: EventItem)

    @Query("SELECT * FROM eventitem WHERE isArchived = 0 order by id desc")
    fun getAllActiveEventsAsFlow(): Flow<List<EventItem>>

    @Query("SELECT * FROM eventitem order by id desc")
    fun getAllEventsAsFlow(): Flow<List<EventItem>>

    @Query("SELECT * FROM eventitem order by id desc")
    suspend fun getAllEvents(): List<EventItem>

    @Query("SELECT * FROM eventitem WHERE isArchived = 0 order by id desc")
    suspend fun getAllActiveEvents(): List<EventItem>

    @Query("SELECT * FROM eventitem WHERE isArchived = 1 order by id desc")
    suspend fun getAllArchivedEvents(): List<EventItem>

    @Query("SELECT * FROM eventitem WHERE id = :eventId")
    suspend fun getEventById(eventId: Int): EventItem

    @Query("DELETE FROM eventitem WHERE id in (:eventIds)")
    fun deleteEvents(eventIds: List<Int>)

    @Query("UPDATE eventitem SET isArchived = 1 WHERE id in (:eventIds)")
    fun archiveEvents(eventIds: List<Int>)

    @Query("UPDATE eventitem SET isArchived = 0 WHERE id in (:eventIds)")
    fun unarchiveEvents(eventIds: List<Int>)

    @Query("SELECT * FROM eventitem ORDER BY id DESC LIMIT 1")
    suspend fun getFirstEvent(): EventItem

    @Query("SELECT * FROM eventitem WHERE id IN (:eventIds)")
    suspend fun getEventsByIds(eventIds: List<Int>): List<EventItem>
}