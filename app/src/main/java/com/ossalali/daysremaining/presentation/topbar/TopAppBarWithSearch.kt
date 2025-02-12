package com.ossalali.daysremaining.presentation.topbar

import androidx.compose.runtime.Composable
import com.ossalali.daysremaining.model.Event
import com.ossalali.daysremaining.presentation.event.EventViewModel

@Composable
fun TopAppBarWithSearch(
    onStartSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    onDrawerClick: () -> Unit,
    isSearching: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    eventViewModel: EventViewModel,
    eventsList: MutableList<Event>

) {
    if (isSearching) {
        SearchTopAppBar(
            onCloseSearch = onCloseSearch,
            searchText = searchText,
            onSearchTextChange = onSearchTextChange,
            eventViewModel = eventViewModel,
            eventsList = eventsList
        )
    } else {
        DefaultTopAppBar(
            onStartSearch = onStartSearch,
            onDrawerClick = onDrawerClick,
            eventViewModel = eventViewModel
        )
    }
}