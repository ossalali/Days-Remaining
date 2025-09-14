package com.ossalali.daysremaining.settings.usecases

import android.content.Context
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.presentation.viewmodel.SettingsViewModel
import com.ossalali.daysremaining.settings.AutoArchiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class EnableAutoArchiveUseCase
@Inject
constructor(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val viewModel: SettingsViewModel,
    private val autoArchiver: AutoArchiver,
    @param:ApplicationContext private val appContext: Context,
) {
  private val scope = CoroutineScope(ioDispatcher)

  operator fun invoke(enabled: Boolean) {
    scope.launch {
      viewModel.toggleAutoArchive(enabled = enabled)
      if (enabled) {
        autoArchiver.start(appContext)
      } else {
        autoArchiver.stop(appContext)
      }
    }
  }
}
