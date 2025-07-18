package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.ui.theme.DefaultPreviews
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@Composable
fun EventListGrid(
  onEventItemClick: (Int) -> Unit,
  onEventItemSelection: (Int) -> Unit,
  events: ImmutableList<EventItem>,
  selectedEventItems: ImmutableList<EventItem>,
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(),
) {
    if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No events found", style = MaterialTheme.typography.titleLargeEmphasized)
        }
        return
    }

    val selectedEventIds = remember(selectedEventItems) { selectedEventItems.map { it.id }.toSet() }

    LazyVerticalStaggeredGrid(
      columns = StaggeredGridCells.Fixed(2),
      modifier = modifier,
      contentPadding = contentPadding,
    ) {
        items(items = events, key = { event -> event.id }) { event ->
            val isSelected =
              remember(selectedEventIds, event.id) { selectedEventIds.contains(event.id) }

            val numberOfDays = event.numberOfDays

            Card(
              border =
                if (isSelected) {
                    BorderStroke(Dimensions.eighth, MaterialTheme.colorScheme.primary)
                } else {
                    null
                },
              shape = MaterialTheme.shapes.medium,
              modifier =
                Modifier.fillMaxWidth()
                  .padding(Dimensions.half)
                  .combinedClickable(
                    onClick = {
                        if (selectedEventItems.isEmpty()) {
                            onEventItemClick(event.id)
                        } else {
                            onEventItemSelection(event.id)
                        }
                    },
                    onLongClickLabel = "Event Selected",
                    onLongClick = { onEventItemSelection(event.id) },
                  ),
              elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.half),
            ) {
                Row(
                  modifier = Modifier.fillMaxSize().padding(Dimensions.half),
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = Dimensions.half)) {
                        Text(
                          text = event.title,
                          style = MaterialTheme.typography.titleLarge,
                          textAlign = TextAlign.Center,
                          modifier = Modifier.fillMaxWidth().padding(bottom = Dimensions.quarter),
                        )
                        Text(
                          text = numberOfDays.toString(),
                          fontSize = TextUnit(16f, TextUnitType.Em),
                          textAlign = TextAlign.Center,
                          modifier = Modifier.fillMaxWidth().padding(bottom = Dimensions.quarter),
                        )
                        if (event.description.isNotEmpty()) {
                            Text(
                              text = event.description,
                              style = MaterialTheme.typography.bodyMediumEmphasized,
                              textAlign = TextAlign.Center,
                              maxLines = 4,
                              overflow = TextOverflow.Ellipsis,
                              modifier =
                                Modifier.fillMaxWidth().padding(bottom = Dimensions.quarter),
                            )
                        }
                    }
                }
            }
        }
    }
}

@DefaultPreviews
@Composable
internal fun EventListGridPreview(
  @PreviewParameter(EventListGridPreviewParameterProvider::class) eventItem: EventItem
) {
    EventListGrid(
      onEventItemClick = {},
      onEventItemSelection = {},
      events = persistentListOf(eventItem),
      selectedEventItems = persistentListOf(),
      modifier = Modifier,
    )
}

internal class EventListGridPreviewParameterProvider :
  CollectionPreviewParameterProvider<EventItem>(
    listOf(
      EventItem(
        id = 0,
        title = "Event 1",
        description = "Event 1 Description",
        date = LocalDate.now().plusDays(5),
      )
    )
  )
