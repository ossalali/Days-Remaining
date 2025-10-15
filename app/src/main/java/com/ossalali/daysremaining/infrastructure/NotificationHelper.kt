package com.ossalali.daysremaining.infrastructure

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for managing notifications and notification permissions.
 * Centralizes notification channel creation and permission checking.
 */
@Singleton
class NotificationHelper @Inject constructor(private val logger: Logger) {

  companion object {
    const val CHANNEL_ID = "event_reminders"
    const val CHANNEL_NAME = "Event Reminders"
    const val CHANNEL_DESCRIPTION = "Notifications for upcoming events"
  }

  /**
   * Creates the notification channel for event reminders.
   * Should be called during app initialization.
   * Safe to call multiple times - Android will ignore duplicate channel creation.
   */
  fun createNotificationChannel(context: Context) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channel =
        NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
          description = CHANNEL_DESCRIPTION
        }

    notificationManager.createNotificationChannel(channel)
    logger.i("NotificationHelper: Notification channel created")
  }

  /**
   * Checks if the app has permission to post notifications.
   * On Android 13+ (API 33+), this requires POST_NOTIFICATIONS permission.
   * On earlier versions, this always returns true.
   */
  fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
          PackageManager.PERMISSION_GRANTED
    } else {
      // Pre-Android 13 doesn't require runtime permission
      true
    }
  }

  /**
   * Checks if the app can schedule exact alarms.
   * On Android 12+ (API 31+), this requires SCHEDULE_EXACT_ALARM permission.
   * On earlier versions, this always returns true.
   */
  fun canScheduleExactAlarms(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
      alarmManager.canScheduleExactAlarms()
    } else {
      // Pre-Android 12 doesn't have restrictions
      true
    }
  }

  /**
   * Checks if notifications are enabled for this app at the system level.
   * This is different from permission - even with permission granted,
   * user might have disabled notifications in system settings.
   */
  fun areNotificationsEnabled(context: Context): Boolean {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    return notificationManager.areNotificationsEnabled()
  }

  /**
   * Checks if the notification channel is enabled.
   * User might have disabled the specific channel even if notifications are enabled.
   */
  fun isChannelEnabled(context: Context): Boolean {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = notificationManager.getNotificationChannel(CHANNEL_ID)
    return channel?.importance != NotificationManager.IMPORTANCE_NONE
  }

  /**
   * Comprehensive check to determine if notifications can be shown.
   * Checks both permission and system settings.
   */
  fun canShowNotifications(context: Context): Boolean {
    return hasNotificationPermission(context) &&
        areNotificationsEnabled(context) &&
        isChannelEnabled(context)
  }

  /**
   * Logs the current notification permission status for debugging.
   */
  fun logNotificationStatus(context: Context) {
    logger.i(
        """
      NotificationHelper Status:
        - Has POST_NOTIFICATIONS permission: ${hasNotificationPermission(context)}
        - Notifications enabled: ${areNotificationsEnabled(context)}
        - Channel enabled: ${isChannelEnabled(context)}
        - Can schedule exact alarms: ${canScheduleExactAlarms(context)}
        - Can show notifications: ${canShowNotifications(context)}
    """
            .trimIndent())
  }
}
