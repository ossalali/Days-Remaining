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

    suspend fun replace(reminders: ImmutableList<Reminder>, eventId: Int) =
        withContext(ioDispatcher) { reminderDataSource.replace(reminders.toList(), eventId) }

    suspend fun getRemindersForEvent(eventId: Int) =
        withContext(ioDispatcher) { reminderDataSource.getRemindersForEvent(eventId) }
}
