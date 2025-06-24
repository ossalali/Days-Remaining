package com.ossalali.daysremaining.presentation.ui.nav3example

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ossalali.daysremaining.model.EventItem

data object EventsScreen

data class EventDetailsScreen(val eventItem: EventItem, val paddingValues: PaddingValues)

@Composable
fun NavExample() {
    val backStack = remember { mutableStateListOf<Any>(EventsScreen) }
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<EventsScreen> {
                EventsScreen(backStack)
            }
            entry<EventDetailsScreen>(
                metadata = NavDisplay.transitionSpec {
                    // Slide new content up, keeping the old content in place underneath
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(1000)
                    ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                } + NavDisplay.popTransitionSpec {
                    // Slide old content down, revealing the new content in place underneath
                    EnterTransition.None togetherWith
                            slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(1000)
                            )
                } + NavDisplay.predictivePopTransitionSpec {
                    // Slide old content down, revealing the new content in place underneath
                    EnterTransition.None togetherWith
                            slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(1000)
                            )
                }

            ) {
                EventDetailsScreenV2(it.eventItem, it.paddingValues)
            }
        },
        transitionSpec = {
            // Slide in from right when navigating forward
            slideInVertically(initialOffsetY = { it }) togetherWith
                    slideOutVertically(targetOffsetY = { it })
            // slideInHorizontally(initialOffsetX = { it }) togetherWith
            //         slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            // Slide in from left when navigating back
            slideInVertically(initialOffsetY = { it }) togetherWith
                    slideOutVertically(targetOffsetY = { it })

            // slideInHorizontally(initialOffsetX = { -it }) togetherWith
            //         slideOutHorizontally(targetOffsetX = { it })
        })
}
