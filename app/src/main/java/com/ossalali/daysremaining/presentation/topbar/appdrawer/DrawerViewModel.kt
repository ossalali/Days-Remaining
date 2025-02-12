package com.ossalali.daysremaining.presentation.topbar.appdrawer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DrawerViewModel @Inject constructor() : ViewModel() {
    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen: StateFlow<Boolean> = _isDrawerOpen

    fun toggleDrawer() {
        _isDrawerOpen.value = !_isDrawerOpen.value
    }
}