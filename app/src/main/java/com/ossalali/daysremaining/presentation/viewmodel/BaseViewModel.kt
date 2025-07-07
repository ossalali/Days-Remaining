package com.ossalali.daysremaining.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseViewModel<I> : ViewModel() {
    abstract fun onInteraction(interaction: I)
}

fun BaseViewModel<*>.launch(block: suspend CoroutineScope.() -> Unit) =
    viewModelScope.launch { block() }

fun BaseViewModel<*>.launch(
    dispatcher: CoroutineDispatcher,
    block: suspend CoroutineScope.() -> Unit,
) = viewModelScope.launch(dispatcher) { block() }
