package com.ossalali.daysremaining.presentation.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.ossalali.daysremaining.infrastructure.EventNotificationTriggerRepository
import com.ossalali.daysremaining.infrastructure.EventRepository
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.settings.SettingsRepository
import com.ossalali.daysremaining.settings.di.WorkerEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.LocalDateTime

class EventNotificationWorker(private val appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val KEY_EVENT_ID = "eventId"
        const val KEY_TRIGGER_ID = "triggerId"
    }

    override suspend fun doWork(): Result {
        val entry = EntryPointAccessors.fromApplication(appContext, WorkerEntryPoint::class.java)
        val repo: EventRepository = entry.eventRepo()
        val triggerRepo: EventNotificationTriggerRepository = entry.triggerRepo()
        val settings: SettingsRepository = entry.settingsRepository()

        val eventId = inputData.getInt(KEY_EVENT_ID, -1)
        val triggerId = inputData.getInt(KEY_TRIGGER_ID, -1)
        if (eventId <= 0 || triggerId <= 0) return Result.failure()

        val event = repo.getEventById(eventId)
        if (event.isArchived) return Result.success()

        val customNotation = settings.customDateNotation.firstOrNull() ?: false
        showEventNotification(appContext, event, customNotation)

        // After posting, compute next for repeating triggers via scheduler
        val scheduler = EventNotificationScheduler(repo, triggerRepo)
        val triggers = triggerRepo.getEnabledTriggersByEvent(eventId)
        val thisTrigger = triggers.find { it.id == triggerId }
        if (thisTrigger != null) {
            if (event.date.isEqual(LocalDate.now())) {
                return Result.success()
            }
            val next = scheduler.computeNextFireDateTime(event.date, thisTrigger)
            if (next != null && !event.date.isBefore(LocalDate.now())) {
                scheduler.cancelForTrigger(appContext, triggerId)
                // Enqueue next occurrence
                val wm = WorkManager.getInstance(appContext)
                val now = LocalDateTime.now()
                val delayMs = java.time.Duration.between(now, next).toMillis().coerceAtLeast(0)
                val request =
                    OneTimeWorkRequestBuilder<EventNotificationWorker>()
                        .setInitialDelay(delayMs, java.util.concurrent.TimeUnit.MILLISECONDS)
                        .setInputData(
                            workDataOf(
                                KEY_EVENT_ID to eventId,
                                KEY_TRIGGER_ID to triggerId
                            )
                        )
                        .build()
                wm.enqueueUniqueWork(
                    EventNotificationScheduler.UNIQUE_PREFIX + triggerId,
                    ExistingWorkPolicy.REPLACE,
                    request,
                )
            }
        }

        return Result.success()
    }
}

private fun showEventNotification(context: Context, event: EventItem, customDateNotation: Boolean) {
    // Delegate to existing NotificationService while ensuring channel and formatting rules.
    // We'll call a simplified static-like helper to avoid Activity dependency in worker.
    val service = WorkerNotificationHelper(context)
    service.createNotification(event, customDateNotation)
}
