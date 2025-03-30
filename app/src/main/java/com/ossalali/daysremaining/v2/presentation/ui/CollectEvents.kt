package com.ossalali.daysremaining.v2.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun <E> CollectEvents(
    flow: Flow<E>,
    block: suspend (E) -> Unit
) {
    LaunchedEffect(Unit) {
        flow.collect(block)
    }
}
