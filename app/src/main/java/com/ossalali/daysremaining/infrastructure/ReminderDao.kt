package com.ossalali.daysremaining.infrastructure

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ossalali.daysremaining.model.Reminder

@Dao
interface ReminderDao {
    @Upsert
    suspend fun upsert(reminder: Reminder)

    @Upsert
    suspend fun upsert(reminders: List<Reminder>)

    @Transaction
    suspend fun replace(reminders: List<Reminder>, eventId: Int) {
        deleteByEventId(eventId)
        upsert(reminders)
    }

    @Query("DELETE FROM reminder WHERE eventItemId = :eventId")
    suspend fun deleteByEventId(eventId: Int)

    @Query("SELECT * FROM reminder WHERE eventItemId = :eventId")
    suspend fun getRemindersForEvent(eventId: Int): List<Reminder>
}
