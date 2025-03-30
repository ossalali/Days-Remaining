package com.ossalali.daysremaining.infrastructure

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ossalali.daysremaining.model.EventItem
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert
    fun insertEvent(eventItem: EventItem)

    @Query("SELECT * FROM eventitem WHERE isArchived = 0 order by id desc")
    fun getAllActiveEventsAsFlow(): Flow<List<EventItem>>

    @Query("SELECT * FROM eventitem order by id desc")
    fun getAllEventsAsFlow(): Flow<List<EventItem>>

    @Query("SELECT * FROM eventitem order by id desc")
    suspend fun getAllEvents(): List<EventItem>

    @Query("SELECT * FROM eventitem WHERE isArchived = 0 order by id desc")
    suspend fun getAllActiveEvents(): List<EventItem>

    @Query("DELETE FROM eventitem WHERE id in (:eventIds)")
    fun deleteEvents(eventIds: List<Int>)

    @Query("UPDATE eventitem SET isArchived = 1 WHERE id in (:eventIds)")
    fun archiveEvents(eventIds: List<Int>)

    @Query("SELECT * FROM eventitem ORDER BY id DESC LIMIT 1")
    suspend fun getFirstEvent(): EventItem
}