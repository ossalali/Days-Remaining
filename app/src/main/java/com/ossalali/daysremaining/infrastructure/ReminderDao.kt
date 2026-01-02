package com.ossalali.daysremaining.infrastructure

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import com.ossalali.daysremaining.model.Reminder

@Dao
interface ReminderDao {
    @Upsert
    suspend fun upsert(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)
}
