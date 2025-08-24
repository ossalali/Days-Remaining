package com.ossalali.daysremaining.settings.usecases

import android.content.Context
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.settings.AutoArchiver
import com.ossalali.daysremaining.settings.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class EnableAutoArchiveUseCase
@Inject
constructor(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val repo: SettingsRepository,
    private val autoArchiver: AutoArchiver,
    @param:ApplicationContext private val appContext: Context,
) {
    private val scope = CoroutineScope(ioDispatcher)

    operator fun invoke(enabled: Boolean) {
        scope.launch {
            repo.setAutoArchive(enabled = enabled)
            if (enabled) {
                autoArchiver.start(appContext)
            } else {
                autoArchiver.stop(appContext)
            }
        }
    }
}
