package com.ossalali.daysremaining.presentation.ui.v2.eventlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import coil.compose.AsyncImage
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.navigation.EventDetailsRoute
import com.ossalali.daysremaining.navigation.EventListRoute
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize
import com.ossalali.daysremaining.presentation.ui.theme.TextSize
import com.ossalali.daysremaining.presentation.ui.v2.model.EventUiModel
import com.ossalali.daysremaining.presentation.ui.v2.model.toNumberOfDays
import com.ossalali.daysremaining.presentation.ui.v2.viewmodel.EventListViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import java.time.LocalDate

fun EntryProviderScope<NavKey>.eventListScreen(backStack: NavBackStack<NavKey>) {
    entry<EventListRoute> { _ ->
        val eventListViewModel = hiltViewModel<EventListViewModel>()

        val state = eventListViewModel.listState.collectAsStateWithLifecycle()
        val activeFilter by eventListViewModel.activeFilterEnabled.collectAsStateWithLifecycle()
        val archivedFilter by eventListViewModel.archivedFilterEnabled.collectAsStateWithLifecycle()

        Column {
            FilterChips(
                modifier = Modifier.padding(horizontal = PaddingSize.half),
                activeFilterEnabled = activeFilter,
                archivedFilterEnabled = archivedFilter,
                onToggleActiveFilter = eventListViewModel::toggleActiveFilter,
                onToggleArchivedFilter = eventListViewModel::toggleArchivedFilter,
            )
            when (state.value) {
                EventListViewModel.ListState.Empty -> {
                    EventListEmpty(
                        modifier = Modifier.weight(1f),
                        onAddEvent = {
                            val eventUiModel = EventUiModel()
                            val hasNone = backStack.none { route -> route is EventDetailsRoute }
                            if (hasNone) {
                                backStack.add(EventDetailsRoute(eventUiModel = eventUiModel))
                            }
                        },
                    )
                }

                EventListViewModel.ListState.Error -> {
                    EventListError(modifier = Modifier.weight(1f))
                }

                is EventListViewModel.ListState.Loaded -> {
                    EventListLoaded(
                        modifier = Modifier.weight(1f),
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
}

@Composable
fun EventListLoaded(
    modifier: Modifier = Modifier,
    eventUiModels: ImmutableList<EventUiModel> = persistentListOf(),
    onItemClicked: (Int) -> Unit = {},
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier.fillMaxSize(),
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
                    // TODO: banner if archived
                    // TODO: reminder indication
                    Text(text = item.title)
                    Text(text = item.date.toNumberOfDays(), fontSize = TextSize.double)

                    if (item.imageUri != null && item.imageUri.isNotBlank()) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .padding(PaddingSize.half)
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(MaterialTheme.shapes.small),
                                model = item.imageUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                error = painterResource(R.drawable.broken_image_24px),
                            )
                        }
                    }

                    if (item.description.isNotBlank()) {
                        Text(
                            modifier = Modifier.padding(horizontal = PaddingSize.half),
                            text = item.description,
                            fontSize = TextSize.subtext,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
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
                    description =
                        """
                            This is a test of a multiline description
                            123
                            asd
                            """
                            .trimIndent(),
                    date = LocalDate.now().plusDays(20).toString(),
                    imageUri = "test",
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
    MyAppTheme { Surface { EventListLoaded(eventUiModels = eventUiModels) } }
}
