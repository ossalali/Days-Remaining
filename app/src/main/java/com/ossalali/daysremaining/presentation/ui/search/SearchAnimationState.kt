package com.ossalali.daysremaining.presentation.ui.search

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay

/**
 * Manages search animation state and handles the transition from search button to search bar
 */
@Composable
fun rememberSearchAnimationState(): SearchAnimationState {
    // Animation for search button to search bar
    var searchButtonPosition by remember { mutableStateOf(Offset.Zero) }
    var searchBarPosition by remember { mutableStateOf(Offset.Zero) }
    var searchBarSize by remember { mutableStateOf(IntSize.Zero) }

    // Animation progress for search button to search bar
    var animatingToSearchBar by remember { mutableStateOf(false) }

    // Animation for the animated search button that transforms
    val searchButtonTranslationY = remember { Animatable(0f) }
    val searchButtonWidth = remember { Animatable(60f) } // Starting with FAB size
    val searchButtonHeight = remember { Animatable(60f) } // Starting with FAB size
    val searchButtonCornerRadius =
        remember { Animatable(30f) } // Starting with circle shape (half of 60)

    return remember(
        searchButtonPosition, searchBarPosition, searchBarSize,
        animatingToSearchBar, searchButtonTranslationY,
        searchButtonWidth, searchButtonHeight, searchButtonCornerRadius
    ) {
        SearchAnimationState(
            searchButtonPosition = searchButtonPosition,
            searchBarPosition = searchBarPosition,
            searchBarSize = searchBarSize,
            animatingToSearchBar = animatingToSearchBar,
            searchButtonTranslationY = searchButtonTranslationY,
            searchButtonWidth = searchButtonWidth,
            searchButtonHeight = searchButtonHeight,
            searchButtonCornerRadius = searchButtonCornerRadius,
            updateSearchButtonPosition = { position, size ->
                searchButtonPosition = position
            },
            updateSearchBarPosition = { position, size ->
                searchBarPosition = position
                searchBarSize = size
            },
            setAnimatingToSearchBar = { animatingToSearchBar = it }
        )
    }
}

/**
 * State holder for search animation
 */
class SearchAnimationState(
    var searchButtonPosition: Offset,
    var searchBarPosition: Offset,
    var searchBarSize: IntSize,
    var animatingToSearchBar: Boolean,
    val searchButtonTranslationY: Animatable<Float, *>,
    val searchButtonWidth: Animatable<Float, *>,
    val searchButtonHeight: Animatable<Float, *>,
    val searchButtonCornerRadius: Animatable<Float, *>,
    val updateSearchButtonPosition: (Offset, IntSize) -> Unit,
    val updateSearchBarPosition: (Offset, IntSize) -> Unit,
    val setAnimatingToSearchBar: (Boolean) -> Unit
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
        searchButtonTranslationY.snapTo(0f)
        searchButtonWidth.snapTo(60f)
        searchButtonHeight.snapTo(60f)
        searchButtonCornerRadius.snapTo(30f)
    }
} 