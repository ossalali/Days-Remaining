package com.ossalali.daysremaining.infrastructure

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ossalali.daysremaining.App
import com.ossalali.daysremaining.MainActivity
import com.ossalali.daysremaining.R

/**
 * Broadcast receiver for handling event notification triggers. Triggered by AlarmManager when it's
 * time to show a reminder notification.
 */
class EventNotificationReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    val eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
    val triggerId = intent.getIntExtra(EXTRA_TRIGGER_ID, -1)
    val eventTitle = intent.getStringExtra(EXTRA_EVENT_TITLE) ?: "Event Reminder"
    val eventDescription = intent.getStringExtra(EXTRA_EVENT_DESCRIPTION) ?: ""

    if (eventId == -1 || triggerId == -1) {
      return
    }

    showNotification(context, eventId, eventTitle, eventDescription)
  }

  private fun showNotification(context: Context, eventId: Int, title: String, description: String) {
    // Get NotificationHelper to check permissions
    val notificationHelper = App.getInstance().notificationHelper

    // Check if we have permission to show notifications
    if (!notificationHelper.hasNotificationPermission(context)) {
      appLogger()
          .w(
              "EventNotificationReceiver: Cannot show notification - POST_NOTIFICATIONS permission not granted")
      return
    }

    // Check if notifications are enabled at system level
    if (!notificationHelper.areNotificationsEnabled(context)) {
      appLogger()
          .w(
              "EventNotificationReceiver: Cannot show notification - notifications disabled in system settings")
      return
    }

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Ensure notification channel exists (safe to call multiple times)
    notificationHelper.createNotificationChannel(context)

    // Intent to open the app when notification is tapped
    val intent =
        Intent(context, MainActivity::class.java).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
          putExtra("eventId", eventId)
        }

    val pendingIntent =
        PendingIntent.getActivity(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    // Build notification
    val notification =
        NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

    notificationManager.notify(eventId, notification)
    appLogger().i("EventNotificationReceiver: Notification shown for event $eventId")
  }

  companion object {
    const val EXTRA_EVENT_ID = "event_id"
    const val EXTRA_TRIGGER_ID = "trigger_id"
    const val EXTRA_EVENT_TITLE = "event_title"
    const val EXTRA_EVENT_DESCRIPTION = "event_description"

    /**
     * Shows a test notification to verify the notification system is working. This is useful for
     * debugging purposes.
     */
    fun showTestNotification(context: Context) {
      val receiver = EventNotificationReceiver()
      receiver.showNotification(
          context = context,
          eventId = 999,
          title = "🧪 Test Notification",
          description =
              "This is a test notification to verify the notification system is working correctly.")
    }
  }
}
