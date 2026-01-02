package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.model.Reminder
import javax.inject.Inject

class ReminderDataSource @Inject constructor(private val reminderDao: ReminderDao) {
    suspend fun upsert(reminder: Reminder) {
        reminderDao.upsert(reminder)
    }

    suspend fun delete(reminder: Reminder) {
        reminderDao.delete(reminder)
    }
}
