package com.ossalali.daysremaining.settings

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AutoArchiver @Inject constructor() {
    companion object {
        private const val UNIQUE_WORK_NAME = "auto-archive-work"
        private const val UNIQUE_ONE_TIME_NAME = "auto-archive-once"
    }

    fun start(context: Context) {
        val currentTime = Calendar.getInstance()
        val nextMidnight =
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(currentTime)) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        val initialDelay = nextMidnight.timeInMillis - currentTime.timeInMillis

        val periodicRequest =
            PeriodicWorkRequestBuilder<AutoArchiveWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

        val wm = WorkManager.getInstance(context.applicationContext)
        wm.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicRequest,
        )

        val oneTimeRequest =
            OneTimeWorkRequestBuilder<AutoArchiveWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        wm.enqueueUniqueWork(UNIQUE_ONE_TIME_NAME, ExistingWorkPolicy.REPLACE, oneTimeRequest)
    }

    fun stop(context: Context) {
        val wm = WorkManager.getInstance(context.applicationContext)
        wm.cancelUniqueWork(UNIQUE_WORK_NAME)
        wm.cancelUniqueWork(UNIQUE_ONE_TIME_NAME)
    }
}
