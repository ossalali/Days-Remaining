package com.ossalali.daysremaining.presentation.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.ossalali.daysremaining.MainActivity
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.widget.EventWidget.Companion.EVENT_ID
import com.ossalali.daysremaining.widget.EventWidget.Companion.VIEW_EVENT_ACTION

class WorkerNotificationHelper(private val context: Context) {

    companion object {
        private const val EVENTS_CHANNEL_ID = "Events"
        private const val EVENTS_CHANNEL_NAME = "Events"
    }

    fun createNotification(eventItem: EventItem, customDateNotation: Boolean) {
        ensureChannel()

        val eventIntent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                action = VIEW_EVENT_ACTION
                putExtra(EVENT_ID, eventItem.id)
            }
        val detailsPendingIntent =
            PendingIntent.getActivity(
                context,
                eventItem.id,
                eventIntent,
                PendingIntent.FLAG_IMMUTABLE,
            )

        val archiveIntent =
            Intent(context, ArchiveConfirmationActivity::class.java).apply {
                putExtra(ArchiveConfirmationActivity.EXTRA_EVENT_ID, eventItem.id)
            }
        val archivePendingIntent =
            PendingIntent.getActivity(
                context,
                eventItem.id shl 1,
                archiveIntent,
                PendingIntent.FLAG_IMMUTABLE,
            )

        val daysText = eventItem.getNumberOfDays(customDateNotation)
        val isToday = daysText == "0" || daysText == "0d"
        val contentText = if (isToday) "Event is today" else "$daysText remaining"

        val builder =
            NotificationCompat.Builder(context, EVENTS_CHANNEL_ID)
                .setContentTitle(eventItem.title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(detailsPendingIntent)
                .setAutoCancel(true)
                .addAction(
                    R.drawable.icon,
                    context.getString(R.string.archive),
                    archivePendingIntent,
                )

        if (eventItem.description.isNotEmpty()) {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(eventItem.description))
        }

        with(NotificationManagerCompat.from(context)) {
            @SuppressLint("MissingPermission")
            notify(eventItem.id, builder.build())
        }
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = ContextCompat.getSystemService(context, NotificationManager::class.java)
            val channel = manager?.getNotificationChannel(EVENTS_CHANNEL_ID)
            if (channel == null) {
                val newChannel =
                    NotificationChannel(
                        EVENTS_CHANNEL_ID,
                        EVENTS_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT,
                    )
                manager?.createNotificationChannel(newChannel)
            }
        }
    }
}


