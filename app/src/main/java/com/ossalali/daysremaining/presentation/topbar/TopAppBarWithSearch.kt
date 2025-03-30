package com.ossalali.daysremaining.presentation.topbar

import androidx.compose.runtime.Composable
import com.ossalali.daysremaining.model.EventItem

@Composable
fun TopAppBarWithSearch(
    onStartSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    onDrawerClick: () -> Unit,
    isSearching: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    eventsList: MutableList<EventItem>,
    selectedEventIds: List<Int> = emptyList(),
    onArchive: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    if (isSearching) {
        SearchTopAppBar(
            onCloseSearch = onCloseSearch,
            searchText = searchText,
            onSearchTextChange = onSearchTextChange,
            eventsList = eventsList
        )
    } else {
        DefaultTopAppBar(
            onStartSearch = onStartSearch,
            onDrawerClick = onDrawerClick,
            selectedEventIds = selectedEventIds,
            onArchive = onArchive,
            onDelete = onDelete
        )
    }
}