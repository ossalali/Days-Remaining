package com.ossalali.daysremaining.presentation.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.event.EventListScreen
import com.ossalali.daysremaining.presentation.event.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    onCloseSearch: () -> Unit,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    eventViewModel: EventViewModel,
    eventsList: MutableList<EventItem>
) {
    var isSearchBarActive by remember { mutableStateOf(true) }
    val textFieldState = rememberTextFieldState(searchText)
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.focusRequester(focusRequester),
                state = textFieldState,
                onSearch = { isSearchBarActive = false },
                expanded = isSearchBarActive,
                onExpandedChange = { isSearchBarActive = it },
                placeholder = { Text("Search Events") },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.clickable { onCloseSearch() },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            )
        },
        expanded = isSearchBarActive,
        onExpandedChange = {
            isSearchBarActive = it
            if (!isSearchBarActive) {
                onCloseSearch()
            }
        }
    ) {
        EventListScreen(eventViewModel, eventsList)
        onSearchTextChange(textFieldState.text.toString())
    }
}