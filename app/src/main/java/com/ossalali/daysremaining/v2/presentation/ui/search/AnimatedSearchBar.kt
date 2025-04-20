package com.ossalali.daysremaining.v2.presentation.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ossalali.daysremaining.infrastructure.logError
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.v2.presentation.ui.EventSearchBar
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

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
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Track whether the search bar is ready to receive focus
    var isSearchBarReady by remember { mutableStateOf(false) }

    // Request focus when search is activated and search bar is ready
    LaunchedEffect(isSearching, isSearchBarReady) {
        if (isSearching && isSearchBarReady && !searchAnimState.animatingToSearchBar) {
            try {
                delay(100) // Small delay to ensure the focus requester is attached
                focusRequester.requestFocus()
                keyboardController?.show()
            } catch (e: IllegalStateException) {
                logError("SearchBar focus request failed", e)
            }
        } else if (!isSearching) {
            // Reset animation when search is closed
            searchAnimState.reset()
        }
    }

    // Track the position of the search bar for animation
    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                // Only update once we have valid coordinates that aren't too large
                val size = coordinates.size
                if (size.height in 1..999) {  // Add sanity check for reasonable height
                    searchAnimState.updateSearchBarPosition(
                        coordinates.positionInRoot(),
                        size
                    )
                }
            }
    ) {
        // Only show the actual search bar once animation is complete
        AnimatedVisibility(
            visible = isSearching && !searchAnimState.animatingToSearchBar,
            enter = fadeIn(animationSpec = tween(durationMillis = 100)),
            exit = fadeOut()
        ) {
            // When the search bar becomes visible, mark it as ready for focus
            LaunchedEffect(Unit) {
                isSearchBarReady = true
            }

            EventSearchBar(
                searchText = searchText,
                isExpanded = isSearching,
                onSearchTextChanged = onSearchTextChanged,
                onExpandedChanged = onSearchActiveChanged,
                filteredEvents = filteredEvents,
                onEventClicked = onEventClicked,
                focusRequester = focusRequester,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Animated Search Button that morphs into Search Bar
    if (searchAnimState.animatingToSearchBar) {
        AnimatedSearchButton(searchAnimState = searchAnimState)
    }

    // When animation starts or search is closed, reset search bar ready state
    LaunchedEffect(searchAnimState.animatingToSearchBar, isSearching) {
        if (searchAnimState.animatingToSearchBar || !isSearching) {
            isSearchBarReady = false
        }
    }
}

/**
 * The animated button that morphs into a search bar
 */
@Composable
private fun AnimatedSearchButton(
    searchAnimState: SearchAnimationState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(
                width = with(LocalDensity.current) { searchAnimState.searchButtonWidth.value.toDp() },
                height = with(LocalDensity.current) { searchAnimState.searchButtonHeight.value.toDp() }
            )
            .offset {
                IntOffset(
                    // Center the expanding button within the search bar width
                    x = 0,
                    y = (searchAnimState.searchButtonPosition.y + searchAnimState.searchButtonTranslationY.value).roundToInt()
                )
            },
        shape = RoundedCornerShape(with(LocalDensity.current) { searchAnimState.searchButtonCornerRadius.value.toDp() }),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Show search icon fading out as it expands
            val iconAlpha by androidx.compose.animation.core.animateFloatAsState(
                targetValue = if (searchAnimState.searchButtonTranslationY.value >
                    searchAnimState.searchBarPosition.y - searchAnimState.searchButtonPosition.y - 50
                ) 0f else 1f,
                label = "SearchIconAlpha"
            )

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Animation",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .size(24.dp)
                    .alpha(iconAlpha)
            )
        }
    }
} 