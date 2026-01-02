package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.model.Reminder
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

    suspend fun delete(reminder: Reminder) =
        withContext(ioDispatcher) { reminderDataSource.delete(reminder) }
}
