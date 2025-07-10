package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

/** Simple search bar component without animation logic */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSearchBar(
  searchText: String,
  onSearchTextChanged: (String) -> Unit,
  focusRequester: FocusRequester,
  modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    DockedSearchBar(
      inputField = {
          SearchBarDefaults.InputField(
            query = searchText,
            onQueryChange = onSearchTextChanged,
            onSearch = {},
            expanded = false,
            onExpandedChange = {},
            placeholder = { Text("Search events") },
            leadingIcon = {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Back",
                  modifier = Modifier.clickable { keyboardController?.hide() },
                )
            },
            trailingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
          )
      },
      expanded = false,
      onExpandedChange = {},
      modifier = modifier,
    ) {}
}

/** Preview for testing the search bar independently */
@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    MaterialTheme {
        EventSearchBar(
          searchText = searchText,
          onSearchTextChanged = { searchText = it },
          focusRequester = focusRequester,
          modifier = Modifier,
        )
    }
}
