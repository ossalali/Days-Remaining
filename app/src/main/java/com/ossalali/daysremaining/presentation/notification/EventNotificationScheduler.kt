package com.ossalali.daysremaining.presentation.notification

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.ossalali.daysremaining.infrastructure.EventNotificationTrigger
import com.ossalali.daysremaining.infrastructure.EventNotificationTriggerRepository
import com.ossalali.daysremaining.infrastructure.EventRepository
import com.ossalali.daysremaining.model.NotificationTriggerType
import com.ossalali.daysremaining.model.RelativeUnit
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EventNotificationScheduler
@Inject
constructor(
    private val eventRepository: EventRepository,
    private val triggerRepository: EventNotificationTriggerRepository,
) {
    companion object {
        const val UNIQUE_PREFIX = "event-trigger-"
    }

    suspend fun rescheduleAllForEvent(context: Context, eventId: Int) {
        cancelAllForEvent(context, eventId)
        val event = eventRepository.getEventById(eventId)
        if (event.isArchived) return
        val triggers = triggerRepository.getEnabledTriggersByEvent(eventId)
        val today = LocalDate.now()
        for (trigger in triggers) {
            val nextTime = computeNextFireDateTime(event.date, trigger, today) ?: continue
            enqueue(context, eventId, trigger.id, nextTime)
        }
    }

    suspend fun cancelAllForEvent(context: Context, eventId: Int) {
        val wm = WorkManager.getInstance(context.applicationContext)
        val triggers = triggerRepository.getEnabledTriggersByEvent(eventId)
        triggers.forEach { trigger -> wm.cancelUniqueWork(uniqueName(trigger.id)) }
    }

    fun cancelForTrigger(context: Context, triggerId: Int) {
        val wm = WorkManager.getInstance(context.applicationContext)
        wm.cancelUniqueWork(uniqueName(triggerId))
    }

    private fun enqueue(context: Context, eventId: Int, triggerId: Int, fireAt: LocalDateTime) {
        val now = LocalDateTime.now()
        val delayMs = Duration.between(now, fireAt).toMillis().coerceAtLeast(0)
        val request =
            OneTimeWorkRequestBuilder<EventNotificationWorker>()
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        EventNotificationWorker.KEY_EVENT_ID to eventId,
                        EventNotificationWorker.KEY_TRIGGER_ID to triggerId,
                    )
                )
                .build()
        val wm = WorkManager.getInstance(context.applicationContext)
        wm.enqueueUniqueWork(uniqueName(triggerId), ExistingWorkPolicy.REPLACE, request)
    }

    private fun uniqueName(triggerId: Int): String = "$UNIQUE_PREFIX$triggerId"

    fun computeNextFireDateTime(
        eventDate: LocalDate,
        trigger: EventNotificationTrigger,
        fromDate: LocalDate = LocalDate.now(),
    ): LocalDateTime? {
        return when (trigger.type) {
            NotificationTriggerType.ON_COMPLETION -> {
                val candidate = eventDate.atStartOfDay()
                if (!candidate.toLocalDate().isBefore(fromDate)) candidate else null
            }

            NotificationTriggerType.RELATIVE -> {
                val unit = trigger.unit ?: return null
                val step = (trigger.step ?: 1).coerceAtLeast(1)
                val floor = fromDate.atStartOfDay()
                var k = 0
                var result: LocalDateTime? = null
                while (k < 730) { // cap to avoid infinite loop
                    val candidateDate =
                        when (unit) {
                            RelativeUnit.DAYS -> eventDate.minusDays((k * step).toLong())
                            RelativeUnit.WEEKS -> eventDate.minusWeeks((k * step).toLong())
                            RelativeUnit.MONTHS -> eventDate.minusMonths((k * step).toLong())
                        }
                    val candidate = candidateDate.atStartOfDay()
                    if (candidate.isBefore(floor)) {
                        break
                    }
                    result = candidate
                    k++
                }
                result
            }
        }
    }
}
