package com.ossalali.daysremaining.infrastructure

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ossalali.daysremaining.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert
    fun insertEvent(event: Event)

    @Query("SELECT * FROM event order by id desc")
    fun getAllEventsAsFlow(): Flow<List<Event>>

    @Query("SELECT * FROM event order by id desc")
    suspend fun getAllEvents(): List<Event>

    @Query("DELETE FROM event WHERE id in (:eventIds)")
    fun deleteEvents(eventIds: List<Int>)

    @Query("UPDATE event SET isArchived = 1 WHERE id in (:eventIds)")
    fun archiveEvents(eventIds: List<Int>)
}