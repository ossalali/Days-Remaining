package com.ossalali.daysremaining.presentation.ui.v2.eventlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.navigation.AddEventRoute
import com.ossalali.daysremaining.navigation.EventDetailsRoute
import com.ossalali.daysremaining.navigation.EventListRoute
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize
import com.ossalali.daysremaining.presentation.ui.theme.TextSize
import com.ossalali.daysremaining.presentation.ui.v2.eventdetails.EventListEmpty
import com.ossalali.daysremaining.presentation.ui.v2.eventdetails.EventListError
import com.ossalali.daysremaining.presentation.ui.v2.eventdetails.EventListLoading
import com.ossalali.daysremaining.presentation.ui.v2.model.EventUiModel
import com.ossalali.daysremaining.presentation.ui.v2.model.toNumberOfDays
import com.ossalali.daysremaining.presentation.ui.v2.viewmodel.EventListViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import java.time.LocalDate

fun EntryProviderScope<NavKey>.eventListScreen(backStack: NavBackStack<NavKey>) {
    entry<EventListRoute> { route ->
        val eventListViewModel =
            hiltViewModel<EventListViewModel>(LocalViewModelStoreOwner.current!!)

        val state = eventListViewModel.listState.collectAsStateWithLifecycle()

        when (state.value) {
            EventListViewModel.ListState.Empty -> {
                EventListEmpty(
                    onAddEvent = {
                        val hasNone = backStack.none { route -> route is AddEventRoute }
                        if (hasNone) {
                            backStack.add(AddEventRoute)
                        }
                    }
                )
            }
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
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        columns = StaggeredGridCells.Adaptive(150.dp),
    ) {
        items(items = eventUiModels, key = { eventItem -> eventItem.id }) { item ->
            Card(
                modifier =
                    Modifier
                        .padding(PaddingSize.half)
                        .clickable(onClick = { onItemClicked(item.id) })
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = PaddingSize.half),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = item.title)
                    Text(text = item.date.toNumberOfDays(), fontSize = TextSize.double)
                    if (item.description.isNotBlank()) {
                        Text(text = item.description, fontSize = TextSize.subtext)
                    }
                    if (item.imageUri != null && item.imageUri.isNotBlank()) {
                        Text(text = item.imageUri)
                    }
                }
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
                    date = LocalDate.now().plusDays(20).toString(),
                )
            )
            add(
                EventUiModel(
                    id = 2,
                    title = "title",
                    description = "description",
                    date = LocalDate.now().plusDays(2).toString(),
                )
            )
        }
            .toPersistentList()
    MyAppTheme { Surface { EventListLoaded(eventUiModels) } }
}
