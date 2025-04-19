package com.ossalali.daysremaining.v2.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.ossalali.daysremaining.model.EventItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Manages search animation state and handles the transition from search button to search bar
 */
@Composable
fun rememberSearchAnimationState(): SearchAnimationState {
    // Animation for search button to search bar
    var searchButtonPosition by remember { mutableStateOf(Offset.Zero) }
    var searchBarPosition by remember { mutableStateOf(Offset.Zero) }
    var searchButtonSize by remember { mutableStateOf(IntSize.Zero) }
    var searchBarSize by remember { mutableStateOf(IntSize.Zero) }

    // Animation progress for search button to search bar
    var animatingToSearchBar by remember { mutableStateOf(false) }
    val searchButtonAlpha by animateFloatAsState(
        targetValue = if (animatingToSearchBar) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "SearchButtonAlpha"
    )

    // Animation for the animated search button that transforms
    val searchButtonTranslationY = remember { Animatable(0f) }
    val searchButtonWidth = remember { Animatable(60f) } // Starting with FAB size
    val searchButtonHeight = remember { Animatable(60f) } // Starting with FAB size
    val searchButtonCornerRadius =
        remember { Animatable(30f) } // Starting with circle shape (half of 60)

    // Create bottom bar animation state
    var bottomBarAnimStarted by remember { mutableStateOf(false) }

    return remember(
        searchButtonPosition, searchBarPosition, searchButtonSize, searchBarSize,
        animatingToSearchBar, searchButtonAlpha, searchButtonTranslationY,
        searchButtonWidth, searchButtonHeight, searchButtonCornerRadius,
        bottomBarAnimStarted
    ) {
        SearchAnimationState(
            searchButtonPosition = searchButtonPosition,
            searchBarPosition = searchBarPosition,
            searchButtonSize = searchButtonSize,
            searchBarSize = searchBarSize,
            animatingToSearchBar = animatingToSearchBar,
            searchButtonAlpha = searchButtonAlpha,
            searchButtonTranslationY = searchButtonTranslationY,
            searchButtonWidth = searchButtonWidth,
            searchButtonHeight = searchButtonHeight,
            searchButtonCornerRadius = searchButtonCornerRadius,
            bottomBarAnimStarted = bottomBarAnimStarted,
            updateSearchButtonPosition = { position, size ->
                searchButtonPosition = position
                searchButtonSize = size
            },
            updateSearchBarPosition = { position, size ->
                searchBarPosition = position
                searchBarSize = size
            },
            setAnimatingToSearchBar = { animatingToSearchBar = it },
            setBottomBarAnimStarted = { bottomBarAnimStarted = it }
        )
    }
}

/**
 * State holder for search animation
 */
class SearchAnimationState(
    var searchButtonPosition: Offset,
    var searchBarPosition: Offset,
    var searchButtonSize: IntSize,
    var searchBarSize: IntSize,
    var animatingToSearchBar: Boolean,
    val searchButtonAlpha: Float,
    val searchButtonTranslationY: Animatable<Float, *>,
    val searchButtonWidth: Animatable<Float, *>,
    val searchButtonHeight: Animatable<Float, *>,
    val searchButtonCornerRadius: Animatable<Float, *>,
    var bottomBarAnimStarted: Boolean,
    val updateSearchButtonPosition: (Offset, IntSize) -> Unit,
    val updateSearchBarPosition: (Offset, IntSize) -> Unit,
    val setAnimatingToSearchBar: (Boolean) -> Unit,
    val setBottomBarAnimStarted: (Boolean) -> Unit
) {
    /**
     * Triggers the animation for search button to search bar transition
     */
    suspend fun animateToSearchBar() {
        if (searchButtonPosition != Offset.Zero) {
            // Calculate the distance for the animation
            val distanceY = searchBarPosition.y - searchButtonPosition.y

            // Mark we're animating
            setAnimatingToSearchBar(true)

            // Launch animations in sequence for better visual coordination
            // First, move the button up to near the search bar position
            searchButtonTranslationY.animateTo(
                targetValue = distanceY * 0.7f,  // Move most of the way up first
                animationSpec = tween(durationMillis = 180)
            )

            // Now trigger the bottom bar to start sliding down
            setBottomBarAnimStarted(true)

            // Complete the button movement to final position
            searchButtonTranslationY.animateTo(
                targetValue = distanceY,
                animationSpec = tween(durationMillis = 120)
            )

            // Animate width expansion
            searchButtonWidth.animateTo(
                targetValue = searchBarSize.width.toFloat(),
                animationSpec = tween(durationMillis = 200)
            )

            // Animate height adjustment - limit to a reasonable height (56dp in pixels)
            // This prevents the button from expanding to the full height of search results
            val targetHeight = minOf(
                searchBarSize.height.toFloat(),
                168f
            ) // Limiting to reasonable search bar height
            searchButtonHeight.animateTo(
                targetValue = targetHeight,
                animationSpec = tween(durationMillis = 200)
            )

            // Animate corner radius change
            searchButtonCornerRadius.animateTo(
                targetValue = 28f, // Match the search bar corner radius
                animationSpec = tween(durationMillis = 200)
            )

            // Allow all animations to complete
            delay(220)
            setAnimatingToSearchBar(false)
        }
    }

    /**
     * Resets animation state when search is closed
     */
    suspend fun reset() {
        setBottomBarAnimStarted(false)
        searchButtonTranslationY.snapTo(0f)
        searchButtonWidth.snapTo(60f)
        searchButtonHeight.snapTo(60f)
        searchButtonCornerRadius.snapTo(30f)
    }
}

/**
 * Displays the search bar with animation components
 */
@OptIn(ExperimentalMaterial3Api::class)
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
    val scope = rememberCoroutineScope()

    // Request focus when search is activated
    LaunchedEffect(isSearching) {
        if (isSearching) {
            focusRequester.requestFocus()
            keyboardController?.show()
            // Trigger animation when search is activated
            scope.launch {
                searchAnimState.animateToSearchBar()
            }
        } else {
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
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchText,
                        onQueryChange = onSearchTextChanged,
                        onSearch = { /* Submit not needed, we filter as user types */ },
                        expanded = isSearching,
                        onExpandedChange = onSearchActiveChanged,
                        placeholder = { Text("Search events") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.clickable {
                                    onSearchActiveChanged(false)
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
                expanded = isSearching,
                onExpandedChange = onSearchActiveChanged,
                modifier = Modifier.fillMaxWidth()
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
                                onSearchActiveChanged(false)
                                keyboardController?.hide()
                                onEventClicked(event.id)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    // Animated Search Button that morphs into Search Bar
    if (searchAnimState.animatingToSearchBar) {
        AnimatedSearchButton(searchAnimState = searchAnimState)
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
                    x = (searchAnimState.searchBarPosition.x + (searchAnimState.searchBarSize.width - searchAnimState.searchButtonWidth.value) / 2).roundToInt(),
                    y = (searchAnimState.searchButtonPosition.y + searchAnimState.searchButtonTranslationY.value).roundToInt()
                )
            },
        shape = RoundedCornerShape(with(LocalDensity.current) { searchAnimState.searchButtonCornerRadius.value.toDp() }),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Show search icon fading out as it expands
            val iconAlpha by animateFloatAsState(
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