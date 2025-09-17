package com.ossalali.daysremaining.presentation.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.ossalali.daysremaining.MainActivity
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.widget.EventWidget.Companion.EVENT_ID
import com.ossalali.daysremaining.widget.EventWidget.Companion.VIEW_EVENT_ACTION
import javax.inject.Inject

class NotificationService
@Inject
constructor(
    private val context: Context,
    private val activity: Activity,
    private val doesNotificationChannelExistUseCase: DoesNotificationChannelExistUseCase,
) {
    private val channelId = "Events"

    private fun getNotificationPermission() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100,
            )
        }
    }

    fun createNotification(eventItem: EventItem) {
        ensureEventsChannel()
        val intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                action = VIEW_EVENT_ACTION
                putExtra(EVENT_ID, eventItem.id)
            }
        val pendingEventDetailIntent: PendingIntent =
            PendingIntent.getActivity(
                context,
                eventItem.id,
                intent,
                PendingIntent.FLAG_IMMUTABLE,
            )

        val archiveIntent =
            Intent(context, ArchiveConfirmationActivity::class.java).apply {
                putExtra(ArchiveConfirmationActivity.EXTRA_EVENT_ID, eventItem.id)
            }
        val archivePendingIntent: PendingIntent =
            PendingIntent.getActivity(
                context,
                eventItem.id shl 1,
                archiveIntent,
                PendingIntent.FLAG_IMMUTABLE,
            )

        val eventNotification =
            buildNotification(eventItem, pendingEventDetailIntent, archivePendingIntent)
        with(NotificationManagerCompat.from(context)) {
            getNotificationPermission()
            @SuppressLint("MissingPermission")
            notify(eventNotification.id, eventNotification.notification)
        }
    }

    private fun buildNotification(
        eventItem: EventItem,
        eventDetailIntent: PendingIntent,
        archiveIntent: PendingIntent,
    ): EventNotification {
        val daysText = eventItem.getNumberOfDays(customDateNotation = false)
        val isToday = daysText == "0" || daysText == "0d"
        val contentText =
            if (isToday) {
                context.getString(R.string.event_is_today)
            } else {
                "$daysText ${context.getString(R.string.days_remaining)}"
        }

        val builder =
            NotificationCompat.Builder(context, channelId)
                .setContentTitle(eventItem.title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(eventDetailIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.icon, context.getString(R.string.archive), archiveIntent)

        if (eventItem.description.isNotEmpty()) {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(eventItem.description))
    }

        return EventNotification(eventItem.id, builder.build())
    }

    private fun ensureEventsChannel() {
        if (!doesNotificationChannelExistUseCase(context, channelId)) {
            val channel =
                NotificationChannel(
                    channelId,
                    "Events",
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            val manager = getSystemService(context, NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}

data class EventNotification(val id: Int, val notification: Notification)
