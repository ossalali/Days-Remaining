package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.model.Reminder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class ReminderDataSource @Inject constructor(private val reminderDao: ReminderDao) {
    suspend fun upsert(reminder: Reminder) {
        reminderDao.upsert(reminder)
    }

    suspend fun upsert(reminders: List<Reminder>) {
        reminderDao.upsert(reminders)
    }

    suspend fun delete(reminder: Reminder) {
        reminderDao.delete(reminder)
    }

    suspend fun getRemindersForEvent(eventId: Int): ImmutableList<Reminder> {
        return reminderDao.getRemindersForEvent(eventId).toImmutableList()
    }
}
