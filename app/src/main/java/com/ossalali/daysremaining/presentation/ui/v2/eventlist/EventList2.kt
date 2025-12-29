package com.ossalali.daysremaining.presentation.ui.v2.eventlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.navigation.EventDetailsRoute
import com.ossalali.daysremaining.navigation.EventListRoute
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.ui.v2.model.EventUiModel
import com.ossalali.daysremaining.presentation.ui.v2.viewmodel.EventListViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

fun EntryProviderScope<NavKey>.eventListScreen(backStack: NavBackStack<NavKey>) {
    entry<EventListRoute> { route ->
        val eventListViewModel =
            hiltViewModel<EventListViewModel>(LocalViewModelStoreOwner.current!!)

        val state = eventListViewModel.listState.collectAsStateWithLifecycle()

        when (state.value) {
            EventListViewModel.ListState.Error -> {
                EventListError()
            }

            is EventListViewModel.ListState.Loaded -> {
                EventListLoaded(
                    (state.value as EventListViewModel.ListState.Loaded).eventUiModels,
                    onItemClicked = { eventId ->
                        val hasNone = backStack.none { route -> route is EventDetailsRoute }
                        if (hasNone) {
                            backStack.add(EventDetailsRoute(eventId))
                        }
                    },
                )
            }

            EventListViewModel.ListState.Loading -> {
                EventListLoading()
            }
        }
    }
}

@Composable
fun EventListLoaded(
    eventUiModels: ImmutableList<EventUiModel> = persistentListOf(),
    onItemClicked: (Int) -> Unit = {},
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = eventUiModels, key = { eventItem -> eventItem.id }) { item ->
            Card(
                modifier =
                    Modifier
                        .padding(Dimensions.default)
                        .clickable(onClick = { onItemClicked(item.id) })
            ) {
                Text(modifier = Modifier.padding(Dimensions.half), text = item.title)
                if (item.description.isNotBlank()) {
                    Text(modifier = Modifier.padding(Dimensions.half), text = item.description)
                }
                Text(modifier = Modifier.padding(Dimensions.half), text = item.date)
                if (item.imageUri != null && item.imageUri.isNotBlank()) {
                    Text(modifier = Modifier.padding(Dimensions.half), text = item.imageUri)
                }
                Text(
                    modifier = Modifier.padding(Dimensions.half),
                    text = item.isArchived.toString(),
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
fun EventListLoadedPreview() {
    val eventUiModels =
        buildList {
            add(
                EventUiModel(
                    id = 1,
                    title = "title",
                    description = "description",
                    date = "date",
                )
            )
            add(
                EventUiModel(
                    id = 2,
                    title = "title",
                    description = "description",
                    date = "date",
                )
            )
        }
            .toPersistentList()
    MyAppTheme { Surface { EventListLoaded(eventUiModels) } }
}

@Composable
fun EventListLoading() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun EventListError() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(modifier = Modifier.align(Alignment.Center), text = "ERROR WHILE LOADING LIST")
    }
}
