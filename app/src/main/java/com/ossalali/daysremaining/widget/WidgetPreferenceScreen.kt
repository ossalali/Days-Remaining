package com.ossalali.daysremaining.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.Icons // Added
import androidx.compose.material.icons.filled.Done // Added
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api // Added
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon // Added
import androidx.compose.material3.IconButton // Added
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar // Added
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope // Added
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
// import androidx.compose.ui.tooling.preview.Preview // Already commented out
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
// import androidx.hilt.navigation.compose.hiltViewModel // Already commented out
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch // Added

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class) // Added ExperimentalMaterial3Api
@Composable
fun WidgetPreferenceScreen(
    viewModel: WidgetPreferenceScreenViewModel
) {
    val systemUiController = rememberSystemUiController()
    val scope = rememberCoroutineScope() // For launching suspend functions from compose
    val isDarkMode = isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme

    val statusBarColor = if (isDarkMode) {
        colorScheme.inverseOnSurface  // Light color for dark mode
    } else {
        colorScheme.onSurface  // Dark color for light mode
    }

    val inputEvents by viewModel.getEvents().collectAsState()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = !isDarkMode
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Widget Settings") },
                actions = {
                    IconButton(onClick = {
                        scope.launch { // Launch the suspend function
                            viewModel.saveSelectedEvents()
                            // Optionally, finish activity or show a toast:
                            // val activity = (context as? Activity)
                            // activity?.finish()
                        }
                    }) {
                        Icon(Icons.Filled.Done, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (inputEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No events found",
                    style = MaterialTheme.typography.titleLargeEmphasized,
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    inputEvents,
                    key = { event -> event.id }
                ) { event ->
                    val isSelected = viewModel.selectedEventIds.contains(event.id)
                    Card(
                        border = if (isSelected) {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            null
                        },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .combinedClickable(
                                onClick = {
                                    viewModel.toggleSelection(event.id)
                                },
                                onClickLabel = "Event Selected",

                                ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp)
                                )
                                Text(
                                    text = event.getNumberOfDays().toString(),
                                    fontSize = TextUnit(16f, TextUnitType.Em),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp)
                                )
                                Text(
                                    text = "Days",
                                    style = MaterialTheme.typography.bodyMediumEmphasized,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// @Preview
// @Composable
// fun WidgetPreferenceScreenPreview() {
    // WidgetPreferenceScreen() // Preview will need a ViewModel instance
// }