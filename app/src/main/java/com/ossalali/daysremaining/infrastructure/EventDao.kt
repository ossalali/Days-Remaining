package com.ossalali.daysremaining.infrastructure

import androidx.room.Dao
import androidx.room.Delete
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

    @Delete
    fun deleteEvent(event: Event)

    @Query("UPDATE event SET isArchived = 1 WHERE id = :eventId")
    fun archiveEvent(eventId: Int)
}