package com.ossalali.daysremaining.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.settings.SettingsRepository
import com.ossalali.daysremaining.settings.usecases.EnableAutoArchiveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    private val repo: SettingsRepository,
    private val enableAutoArchiveUseCase: EnableAutoArchiveUseCase,
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

    fun toggleDarkMode(enabled: Boolean) =
        viewModelScope.launch { repo.setDarkMode(enabled = enabled) }

    fun toggleNotifications(enabled: Boolean) =
        viewModelScope.launch { repo.setNotifications(enabled = enabled) }

    fun toggleAutoArchive(enabled: Boolean) {
        enableAutoArchiveUseCase(enabled)
    }
}
