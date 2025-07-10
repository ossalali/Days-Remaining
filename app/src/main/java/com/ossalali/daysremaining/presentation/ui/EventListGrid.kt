package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.ui.theme.DefaultPreviews
import java.time.LocalDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@Composable
fun EventListGrid(
    onEventItemClick: (Int) -> Unit,
    onEventItemSelection: (Int) -> Unit,
    events: List<EventItem>,
    selectedEventItems: List<EventItem>,
    modifier: Modifier = Modifier,
) {
    if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No events found", style = MaterialTheme.typography.titleLargeEmphasized)
        }
    }
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = modifier) {
        items(events, key = { event -> event.id }) { event ->
            val isSelected = selectedEventItems.map { it.id }.contains(event.id)
            Card(
                border =
                    if (isSelected) {
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                } else {
                    null
                },
                shape = MaterialTheme.shapes.medium,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
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
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                        )
                        Text(
                            text = event.getNumberOfDays().toString(),
                            fontSize = TextUnit(16f, TextUnitType.Em),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                        )
                        Text(
                            text = "Days",
                            style = MaterialTheme.typography.bodyMediumEmphasized,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                        )
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
        events = listOf(eventItem),
        selectedEventItems = emptyList(),
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
