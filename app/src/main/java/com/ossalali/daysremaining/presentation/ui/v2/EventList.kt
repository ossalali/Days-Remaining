package com.ossalali.daysremaining.presentation.ui.v2

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.glance.text.Text
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ossalali.daysremaining.navigation.EventListRoute
import com.ossalali.daysremaining.presentation.viewmodel.EventListViewModel
import kotlinx.collections.immutable.ImmutableList

fun EntryProviderScope<NavKey>.eventListScreen(backStack: NavBackStack<NavKey>) {
    entry<EventListRoute> { route ->
        val eventListViewModel = viewModel<EventListViewModel>(LocalViewModelStoreOwner.current!!)

        val state = eventListViewModel.listState.collectAsStateWithLifecycle()

        when (state.value) {
            EventListViewModel.ListState.Error -> {
                // TODO: show error
            }

            is EventListViewModel.ListState.Loaded -> {
                EventList((state.value as EventListViewModel.ListState.Loaded).eventUiModels)
            }

            EventListViewModel.ListState.Loading -> {
                // TODO: show Loading
            }
        }
    }
}

@Composable
fun EventList(eventUiModels: ImmutableList<EventUiModel>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = eventUiModels, key = { eventItem -> eventItem.id }) { item ->
            Text(text = item.title)
            Text(text = item.description)
            Text(text = item.date)
            Text(text = item.imageUri ?: "")
            Text(text = item.isArchived.toString())
        }
    }
}
