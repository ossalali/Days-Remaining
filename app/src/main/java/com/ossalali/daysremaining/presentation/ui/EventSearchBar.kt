package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    SearchBar(
      inputField = {
          SearchBarDefaults.InputField(
            query = searchText,
            onQueryChange = onSearchTextChanged,
            onSearch = { keyboardController?.hide() },
            expanded = false,
            onExpandedChange = {},
            placeholder = { Text("Search events") },
            trailingIcon = {
                if (isFocused) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close search bar",
                        modifier =
                        Modifier.clickable {
                            keyboardController?.hide()
                            onSearchTextChanged("")
                            focusManager.clearFocus()
                        },
                    )
                }
            },
            modifier =
                Modifier
                    .shadow(Dimensions.quarter, shape = CircleShape)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState -> isFocused = focusState.isFocused },
          )
      },
      expanded = false,
      onExpandedChange = {},
      modifier = modifier,
    ) {}
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    var searchText by remember { mutableStateOf("") }

    MaterialTheme {
        EventSearchBar(
            searchText = searchText,
            onSearchTextChanged = { searchText = it },
            modifier = Modifier,
        )
    }
}
