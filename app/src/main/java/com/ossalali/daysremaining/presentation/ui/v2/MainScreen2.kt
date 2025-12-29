package com.ossalali.daysremaining.presentation.ui.v2

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ossalali.daysremaining.navigation.DebugRoute
import com.ossalali.daysremaining.navigation.EventDetailsRoute
import com.ossalali.daysremaining.navigation.EventListRoute
import com.ossalali.daysremaining.navigation.SettingsRoute
import com.ossalali.daysremaining.presentation.ui.debugScreen
import com.ossalali.daysremaining.presentation.ui.settingsScreen
import com.ossalali.daysremaining.presentation.ui.v2.eventdetails.EventDetailsBottomBar
import com.ossalali.daysremaining.presentation.ui.v2.eventdetails.eventDetailsScreen
import com.ossalali.daysremaining.presentation.ui.v2.eventlist.eventListScreen

@Composable
fun MainScreen2() {
    val backStack = rememberNavBackStack(EventListRoute)
    Scaffold(
        topBar = {
            when (backStack.lastOrNull()) {
                is EventDetailsRoute -> {
                    TopAppBar(onBackClick = { backStack.removeLastOrNull() }, showBackButton = true)
                }

                is SettingsRoute -> {
                    TopAppBar(onBackClick = { backStack.removeLastOrNull() }, showBackButton = true)
                }

                is DebugRoute -> {
                    TopAppBar(onBackClick = { backStack.removeLastOrNull() }, showBackButton = true)
                }

                else -> {
                    /* EventListRoute */
                    TopAppBar(
                        onBackClick = { backStack.removeLastOrNull() },
                        showTopAppBarButtons = true,
                        navigateToDebugScreen = {
                            if (backStack.contains(DebugRoute)) return@TopAppBar
                            backStack.add(DebugRoute)
                        },
                        navigateToSettingsScreen = {
                            if (backStack.contains(SettingsRoute)) return@TopAppBar
                            backStack.add(SettingsRoute)
                        },
                    )
                }
            }
        },
        bottomBar = {
            when (backStack.lastOrNull()) {
                is EventDetailsRoute -> {
                    EventDetailsBottomBar()
                }

                is SettingsRoute -> {
                    // no bottom bar
                }

                is DebugRoute -> {
                    // no bottom bar
                }
                else -> {
                    // search bar and add button
                    Text(text = "Event List Bottom Bar")
                }
            }
        },
    ) { paddingValues ->
        NavDisplay(
            modifier = Modifier.padding(paddingValues),
            backStack = backStack,
            entryDecorators =
                listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
            transitionSpec = { slideInFromRight() togetherWith slideOutToLeft() },
            popTransitionSpec = { slideInFromLeft() togetherWith slideOutToRight() },
            predictivePopTransitionSpec = { slideInFromLeft() togetherWith slideOutToRight() },
            entryProvider =
                entryProvider {
                    eventListScreen(backStack = backStack)
                    eventDetailsScreen(backStack = backStack)
                    settingsScreen(backStack = backStack)
                    debugScreen(backStack = backStack)
                },
        )
    }
}

private fun slideInFromRight(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(300),
    ) + fadeIn(animationSpec = tween(300))
}

private fun slideOutToLeft(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(300),
    ) + fadeOut(animationSpec = tween(300))
}

private fun slideInFromLeft(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(300),
    ) + fadeIn(animationSpec = tween(300))
}

private fun slideOutToRight(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(300),
    ) + fadeOut(animationSpec = tween(300))
}
