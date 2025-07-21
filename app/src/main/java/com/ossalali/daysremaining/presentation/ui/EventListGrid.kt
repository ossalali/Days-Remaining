package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
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
                Box(modifier = Modifier.clipToBounds()) {
                    if (event.isArchived) {
                        Box(
                          modifier =
                            Modifier.align(alignment = Alignment.BottomEnd)
                              .offset(x = (70).dp, y = (-20).dp)
                              .graphicsLayer { rotationZ = -45f }
                              .background(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 1f)
                              )
                        ) {
                            Text(
                              modifier = Modifier.fillMaxSize(),
                              text = "Archived",
                              style = MaterialTheme.typography.labelMedium,
                              textAlign = TextAlign.Center,
                            )
                        }
                    }

                    Row(
                      modifier = Modifier.fillMaxSize().padding(Dimensions.half),
                      verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = Dimensions.half)) {
                            Text(
                              text = event.title,
                              style = MaterialTheme.typography.titleLarge,
                              textAlign = TextAlign.Center,
                              modifier =
                                Modifier.fillMaxWidth().padding(bottom = Dimensions.quarter),
                            )
                            Text(
                              text = numberOfDays.toString(),
                              fontSize = TextUnit(16f, TextUnitType.Em),
                              textAlign = TextAlign.Center,
                              modifier =
                                Modifier.fillMaxWidth().padding(bottom = Dimensions.quarter),
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
}

@DefaultPreviews
@Composable
internal fun EventListGridPreview() {
    val eventItems =
      persistentListOf(
        EventItem(
          id = 0,
          title = "Event 1",
          description = "Event 1 Description",
          date = LocalDate.now().plusDays(5),
        ),
        EventItem(
          id = 1,
          title = "Event 2 Long Title To See How It Wraps",
          description =
            "Event 2 Description is a bit longer to test the text overflow if it ever happens.",
          date = LocalDate.now().plusDays(10),
        ),
        EventItem(
          id = 2,
          title = "Event 3 Archived",
          description = "This is another archived event.",
          date = LocalDate.now().plusDays(20),
        ),
        EventItem(
          id = 3,
          title = "Event 4",
          description = "A normal event.",
          date = LocalDate.now().plusDays(15),
          isArchived = true,
        ),
        EventItem(
          id = 4,
          title = "Event 5 Archived",
          description = "This is another archived event.",
          date = LocalDate.now().plusDays(30),
          isArchived = true,
        ),
        EventItem(
          id = 5,
          title = "Event 6",
          description = "A normal event as a test .",
          date = LocalDate.now().plusDays(25),
          isArchived = true,
        ),
      )
    EventListGrid(
      onEventItemClick = {},
      onEventItemSelection = {},
      events = eventItems,
      selectedEventItems = persistentListOf(eventItems[2], eventItems[5]),
      modifier = Modifier,
    )
}
