package com.ossalali.daysremaining.v2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<S, E, I>(initialState: S) : ViewModel() {
    protected val stateMutable = MutableStateFlow(initialState)
    protected val eventChannel: Channel<E> = Channel(1)

    val state = stateMutable.asStateFlow()
    val events: Flow<E> = eventChannel.receiveAsFlow()

    abstract fun onInteraction(interaction: I)

    fun <T> Flow<T>.launchCollect(block: FlowCollector<T>): Job = viewModelScope.launch {
        collect(block)
    }

    @Suppress("unused")
    inline fun <reified T> whenState(crossinline block: (t: T) -> Unit) =
        whenState(state.value, block)
}

fun BaseViewModel<*, *, *>.launch(block: suspend CoroutineScope.() -> Unit) =
    viewModelScope.launch { block() }

fun BaseViewModel<*, *, *>.launch(
    dispatcher: CoroutineDispatcher,
    block: suspend CoroutineScope.() -> Unit
) = viewModelScope.launch(dispatcher) { block() }

inline fun <reified T> whenState(state: Any?, block: (t: T) -> Unit) {
    when (state) {
        is T -> block(state)
        else -> Unit
    }
}
