package com.ossalali.daysremaining.widget

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.ossalali.daysremaining.infrastructure.appLogger
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetPreferenceScreen(viewModel: WidgetPreferenceScreenViewModel, onSaveComplete: () -> Unit) {
    val scope = rememberCoroutineScope()
    val inputEvents by viewModel.getEvents().collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Widget Settings") },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                viewModel.saveSelectedEvents()
                                onSaveComplete()
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Done, contentDescription = "Save")
                    }
                },
            )
        }
    ) { paddingValues ->
        if (inputEvents.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "No events found", style = MaterialTheme.typography.titleLarge)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(paddingValues),
            ) {
                items(inputEvents, key = { event -> event.id }) { event ->
                    val isSelected = viewModel.selectedEventIds.contains(event.id)
                    appLogger()
                        .d(
                            tag = "WidgetPreferenceScreen",
                            message =
                                "Composing event ${event.id}, isSelected: $isSelected, selectedIds: ${viewModel.selectedEventIds}",
                        )
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
                                    onClick = { viewModel.toggleSelection(event.id) },
                                    onClickLabel = "Event Selected",
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
                                    modifier =
                                        Modifier.fillMaxWidth().padding(bottom = Dimensions.quarter),
                                )
                                Text(
                                    text = event.numberOfDays.toString(),
                                    fontSize = TextUnit(16f, TextUnitType.Em),
                                    textAlign = TextAlign.Center,
                                    modifier =
                                        Modifier.fillMaxWidth().padding(bottom = Dimensions.quarter),
                                )
                                Text(
                                    text = "Days",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
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
