package com.ossalali.daysremaining.presentation.notification

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import javax.inject.Inject

class DoesNotificationChannelExistUseCase @Inject constructor() {
    operator fun invoke(context: Context, channelId: String): Boolean {
        val notificationManager =
            ContextCompat.getSystemService(context, NotificationManagerCompat::class.java)

        return notificationManager?.getNotificationChannel(channelId) != null
    }
}
