package com.ossalali.daysremaining.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val repo: SettingsRepository,
) : ViewModel() {
  val darkModeEnabled: StateFlow<Boolean> =
      repo.darkMode.stateIn(
          scope = viewModelScope,
          started = SharingStarted.WhileSubscribed(5_000),
          initialValue = false,
      )

  val notificationsEnabled: StateFlow<Boolean> =
      repo.notifications.stateIn(
          scope = viewModelScope,
          started = SharingStarted.WhileSubscribed(5_000),
          initialValue = false,
      )

  val autoArchiveEnabled: StateFlow<Boolean> =
      repo.autoArchive.stateIn(
          scope = viewModelScope,
          started = SharingStarted.WhileSubscribed(5_000),
          initialValue = false,
      )

  val customDateNotation: StateFlow<Boolean> =
      repo.customDateNotation.stateIn(
          scope = viewModelScope,
          started = SharingStarted.WhileSubscribed(5_000),
          initialValue = false,
      )

  fun toggleDarkMode(enabled: Boolean) =
      viewModelScope.launch(ioDispatcher) { repo.setDarkMode(enabled = enabled) }

  fun toggleNotifications(enabled: Boolean) =
      viewModelScope.launch(ioDispatcher) { repo.setNotifications(enabled = enabled) }

  fun toggleAutoArchive(enabled: Boolean) {
    viewModelScope.launch(ioDispatcher) { repo.setAutoArchive(enabled = enabled) }
  }

  fun toggleCustomDateNotation(enabled: Boolean) {
    viewModelScope.launch(ioDispatcher) { repo.setCustomDateNotation(enabled = enabled) }
  }
}
