package com.ossalali.daysremaining.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _darkModeEnabled = MutableStateFlow(false)
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled

    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    private val _autoArchiveEnabled = MutableStateFlow(false)
    val autoArchiveEnabled: StateFlow<Boolean> = _autoArchiveEnabled

    fun toggleDarkMode(enabled: Boolean) {
        _darkModeEnabled.value = enabled
        // Add implementation to save preference
    }

    fun toggleNotifications() {
        _notificationsEnabled.value = !_notificationsEnabled.value
        // Add implementation to save preference
    }

    fun toggleAutoArchive() {
        _autoArchiveEnabled.value = !_autoArchiveEnabled.value
        // Add implementation to save preference
    }
}
