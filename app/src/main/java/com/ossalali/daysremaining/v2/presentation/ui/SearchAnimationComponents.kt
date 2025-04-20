package com.ossalali.daysremaining.v2.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.v2.presentation.ui.search.AnimatedSearchBar
import com.ossalali.daysremaining.v2.presentation.ui.search.SearchAnimationState
import com.ossalali.daysremaining.v2.presentation.ui.search.rememberSearchAnimationState
import com.ossalali.daysremaining.v2.presentation.ui.theme.Dimensions
import java.time.LocalDate

/**
 * Displays the search bar with animation components
 */
@Composable
fun AnimatedSearchBar(
    isSearching: Boolean,
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onSearchActiveChanged: (Boolean) -> Unit,
    searchAnimState: SearchAnimationState,
    filteredEvents: List<EventItem>,
    onEventClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedSearchBar(
        isSearching = isSearching,
        searchText = searchText,
        onSearchTextChanged = onSearchTextChanged,
        onSearchActiveChanged = onSearchActiveChanged,
        searchAnimState = searchAnimState,
        filteredEvents = filteredEvents,
        onEventClicked = onEventClicked,
        modifier = modifier
    )
}

/**
 * Preview for testing the search animation
 */
@Preview(showBackground = true, heightDp = 600)
@Composable
fun SearchAnimationPreview() {
    val searchAnimState = rememberSearchAnimationState()
    var isSearching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val sampleEvents = remember {
        listOf(
            EventItem(1, "Birthday", LocalDate.now().plusDays(1), "d1", false),
            EventItem(2, "Anniversary", LocalDate.now().plusDays(2), "d2", false),
            EventItem(3, "Vacation", LocalDate.now().plusDays(3), "d3", false),
            EventItem(4, "Meeting", LocalDate.now().plusDays(4), "d4", false)
        )
    }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { isSearching = !isSearching },
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(if (isSearching) "Close Search" else "Open Search")
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedSearchBar(
                    isSearching = isSearching,
                    searchText = searchText,
                    onSearchTextChanged = { searchText = it },
                    onSearchActiveChanged = { isSearching = it },
                    searchAnimState = searchAnimState,
                    filteredEvents = sampleEvents.filter {
                        searchText.isEmpty() || it.title.contains(searchText, ignoreCase = true)
                    },
                    onEventClicked = { },
                    modifier = Modifier.fillMaxWidth()
                )

                if (!isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimensions.double),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {},
                                modifier = Modifier.padding(
                                    start = Dimensions.half,
                                    end = Dimensions.half
                                )
                            ) {
                                Text("Deleted")
                            }
                            FloatingActionButton(
                                onClick = { isSearching = true },
                                modifier = Modifier
                                    .onGloballyPositioned { coordinates ->
                                        searchAnimState.updateSearchButtonPosition(
                                            coordinates.positionInRoot(),
                                            coordinates.size
                                        )
                                    }
                                    .padding(
                                        start = Dimensions.half,
                                        end = Dimensions.half
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            }
                            Button(
                                onClick = {},
                                modifier = Modifier.padding(
                                    start = Dimensions.half,
                                    end = Dimensions.half
                                )
                            ) {
                                Text("Archived")
                            }
                        }
                    }
                }
            }
        }
    }
} 