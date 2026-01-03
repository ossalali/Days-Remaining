package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.model.Reminder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReminderRepository
@Inject
constructor(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val reminderDataSource: ReminderDataSource,
) {
    suspend fun upsert(reminder: Reminder) =
        withContext(ioDispatcher) { reminderDataSource.upsert(reminder) }

    suspend fun upsert(reminders: ImmutableList<Reminder>) =
        withContext(ioDispatcher) { reminderDataSource.upsert(reminders.toList()) }

    suspend fun delete(reminder: Reminder) =
        withContext(ioDispatcher) { reminderDataSource.delete(reminder) }

    suspend fun getRemindersForEvent(eventId: Int) =
        withContext(ioDispatcher) { reminderDataSource.getRemindersForEvent(eventId) }
}
