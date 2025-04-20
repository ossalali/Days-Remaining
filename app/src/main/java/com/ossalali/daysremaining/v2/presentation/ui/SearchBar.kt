package com.ossalali.daysremaining.v2.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ossalali.daysremaining.model.EventItem
import java.time.LocalDate

/**
 * Simple search bar component without animation logic
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSearchBar(
    searchText: String,
    isExpanded: Boolean,
    onSearchTextChanged: (String) -> Unit,
    onExpandedChanged: (Boolean) -> Unit,
    filteredEvents: List<EventItem>,
    onEventClicked: (Int) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = searchText,
                onQueryChange = onSearchTextChanged,
                onSearch = { /* Submit not needed, we filter as user types */ },
                expanded = isExpanded,
                onExpandedChange = onExpandedChanged,
                placeholder = { Text("Search events") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.clickable {
                            onExpandedChanged(false)
                            keyboardController?.hide()
                        }
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        },
        expanded = isExpanded,
        onExpandedChange = onExpandedChanged,
        modifier = modifier.fillMaxWidth()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            items(filteredEvents) { event ->
                SearchEventItem(
                    event = event,
                    onClick = {
                        onExpandedChanged(false)
                        keyboardController?.hide()
                        onEventClicked(event.id)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Preview for testing the search bar independently
 */
@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    var searchText by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }

    // Sample event data for the preview
    val sampleEvents = remember {
        listOf(
            EventItem(1, "Birthday", LocalDate.now().plusDays(1), "d1", false),
            EventItem(2, "Anniversary", LocalDate.now().plusDays(2), "d2", false),
            EventItem(3, "Vacation", LocalDate.now().plusDays(3), "d3", false),
            EventItem(4, "Meeting", LocalDate.now().plusDays(4), "d4", false)
        )
    }

    MaterialTheme {
        EventSearchBar(
            searchText = searchText,
            isExpanded = isExpanded,
            onSearchTextChanged = { searchText = it },
            onExpandedChanged = { isExpanded = it },
            filteredEvents = sampleEvents.filter {
                searchText.isEmpty() || it.title.contains(searchText, ignoreCase = true)
            },
            onEventClicked = { /* Preview only */ },
            focusRequester = focusRequester,
            modifier = Modifier.fillMaxWidth()
        )
    }
} 