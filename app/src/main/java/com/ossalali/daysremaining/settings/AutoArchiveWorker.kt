package com.ossalali.daysremaining.settings

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.settings.di.WorkerEntryPoint
import com.ossalali.daysremaining.widget.refreshWidget
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class AutoArchiveWorker(private val appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val entryPoint =
            EntryPointAccessors.fromApplication(
                context = appContext,
                entryPoint = WorkerEntryPoint::class.java,
            )
        val settings = entryPoint.settingsRepository()
        val isEnabled = settings.autoArchive.first()
        if (isEnabled.not()) {
            return Result.success()
        }

        val repo: EventRepo = entryPoint.eventRepo()
        val allEvents = repo.getAllEvents()
        val today = LocalDate.now()
        val idsToArchive =
            allEvents
                .filter { eventItem -> eventItem.date.isBefore(today) && !eventItem.isArchived }
                .map { eventItem -> eventItem.id }
        if (idsToArchive.isNotEmpty()) {
            repo.archiveEvents(idsToArchive)
            refreshWidget(appContext)
        }
        return Result.success()
    }
}
