package com.ossalali.daysremaining.infrastructure

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.model.EventNotificationTrigger
import com.ossalali.daysremaining.model.NotificationTriggerType
import com.ossalali.daysremaining.model.RelativeUnit
import java.time.ZoneId
import javax.inject.Inject

class EventNotificationScheduler
@Inject
constructor(
    private val eventRepository: EventRepository,
    private val triggerRepository: EventNotificationTriggerRepository,
    private val notificationHelper: NotificationHelper,
) {
    suspend fun rescheduleAllForEvent(context: Context, eventId: Int) {
        cancelAlarmsForEvent(context, eventId)
        val event =
            try {
                eventRepository.getEventById(eventId)
            } catch (e: Exception) {
                appLogger().e(
                    "EventNotificationScheduler: failed to get the event with id $eventId",
                    e
                )
                return
            }

        val triggers = triggerRepository.getEnabledTriggersByEvent(eventId)

        triggers.forEach { trigger ->
            val notificationTime = calculateNotificationTime(event, trigger)
            if (notificationTime != null && notificationTime > System.currentTimeMillis()) {
                scheduleAlarm(context, eventId, trigger.id, notificationTime)
            }
        }
    }

    private suspend fun cancelAlarmsForEvent(context: Context, eventId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val allTriggers =
            try {
                triggerRepository.getAllTriggersByEvent(eventId)
            } catch (e: Exception) {
                appLogger().e(
                    "EventNotificationScheduler: failed to get triggers for event $eventId", e
                )
                return
            }

        allTriggers.forEach { trigger ->
            val pendingIntent = createPendingIntent(context, eventId, trigger.id)
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun calculateNotificationTime(
        event: EventItem,
        trigger: EventNotificationTrigger
    ): Long? {
        return when (trigger.type) {
            NotificationTriggerType.RELATIVE -> {
                val step = trigger.step ?: return null
                val unit = trigger.unit ?: return null

                val eventDateTime = event.date.atStartOfDay()
                val notificationDateTime =
                    when (unit) {
                        RelativeUnit.DAYS -> eventDateTime.minusDays(step.toLong())
                        RelativeUnit.WEEKS -> eventDateTime.minusWeeks(step.toLong())
                        RelativeUnit.MONTHS -> eventDateTime.minusMonths(step.toLong())
                    }

                notificationDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }

            NotificationTriggerType.ON_COMPLETION -> {
                event.date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }
        }
    }

    private suspend fun scheduleAlarm(context: Context, eventId: Int, triggerId: Int, time: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val event =
            try {
                eventRepository.getEventById(eventId)
            } catch (e: Exception) {
                appLogger().e(
                    "EventNotificationScheduler: failed to get event $eventId for scheduling", e
                )
                return
            }

        // Check if we have notification permission
        if (!notificationHelper.hasNotificationPermission(context)) {
            appLogger().w(
                "EventNotificationScheduler: Cannot schedule alarm for event $eventId - POST_NOTIFICATIONS permission not granted"
            )
            return
        }

        val pendingIntent =
            createPendingIntent(context, eventId, triggerId, event.title, event.description)

        // Try to schedule exact alarm, fall back to inexact if not available
        try {
            if (notificationHelper.canScheduleExactAlarms(context)) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
                appLogger().i(
                    "EventNotificationScheduler: Scheduled EXACT alarm for event $eventId, trigger $triggerId at $time"
                )
            } else {
                // Fallback to inexact alarm
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
                appLogger().w(
                    "EventNotificationScheduler: Scheduled INEXACT alarm for event $eventId, trigger $triggerId at $time (exact alarms not available)"
                )
            }
        } catch (e: SecurityException) {
            appLogger().e(
                "EventNotificationScheduler: SecurityException scheduling alarm for event $eventId",
                e
            )
            // Try one more time with inexact alarm as absolute fallback
            try {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
                appLogger().w(
                    "EventNotificationScheduler: Fell back to INEXACT alarm for event $eventId after SecurityException"
                )
            } catch (fallbackException: Exception) {
                appLogger().e(
                    "EventNotificationScheduler: Complete failure to schedule alarm for event $eventId",
                    fallbackException
                )
            }
        }
    }

    /**
     * Creates a PendingIntent for scheduling/canceling alarms. The PendingIntent must be identical
     * for both scheduling and canceling to work correctly.
     */
    private fun createPendingIntent(
        context: Context,
        eventId: Int,
        triggerId: Int,
        eventTitle: String = "",
        eventDescription: String = ""
    ): PendingIntent {
        val intent =
            Intent(context, EventNotificationReceiver::class.java).apply {
                putExtra(EventNotificationReceiver.EXTRA_EVENT_ID, eventId)
                putExtra(EventNotificationReceiver.EXTRA_TRIGGER_ID, triggerId)
                putExtra(EventNotificationReceiver.EXTRA_EVENT_TITLE, eventTitle)
                putExtra(EventNotificationReceiver.EXTRA_EVENT_DESCRIPTION, eventDescription)
            }

        // Generate unique request code using bit shifting to avoid collisions
        // This allows for eventIds up to 32767 and triggerIds up to 65535
        val requestCode = ((eventId and 0xFFFF) shl 16) or (triggerId and 0xFFFF)

        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
