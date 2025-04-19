package com.ossalali.daysremaining.v2.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.v2.presentation.ui.theme.DefaultPreviews
import com.ossalali.daysremaining.v2.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.Event
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.Interaction
import com.ossalali.daysremaining.v2.presentation.viewmodel.EventListViewModel.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
internal fun EventList(
    modifier: Modifier = Modifier,
    viewModel: EventListViewModel = hiltViewModel(),
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.onInteraction(Interaction.Init)
    }

    LaunchedEffect(navController, viewModel) {
        viewModel.navigationEvent.collect { route ->
            navController.navigate(route)
        }
    }

    EventListImpl(
        onInteraction = viewModel::onInteraction,
        stateflow = viewModel.state,
        eventsFlow = viewModel.events,
        selectedEventIds = viewModel.selectedEventItemIds,
        currentEventItems = viewModel.currentEventItems,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventListImpl(
    onInteraction: (Interaction) -> Unit,
    stateflow: StateFlow<State>,
    eventsFlow: Flow<Event>,
    selectedEventIds: List<Int>,
    currentEventItems: List<EventItem>,
    modifier: Modifier = Modifier
) {
    CollectEvents(eventsFlow) { event ->
        when (event) {
            is Event.EventItemArchived -> TODO()
            is Event.EventItemDeleted -> TODO()
        }
    }
    val state by stateflow.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { true }
    )
    var showBottomSheet by remember { mutableStateOf(false) }

    // Create local search state since we don't have direct access to viewModel
    var isSearching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var filteredEvents by remember { mutableStateOf(currentEventItems) }

    // Animation states
    var isFirstRender by remember { mutableStateOf(true) }
    var bottomBarAnimStarted by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val bottomBarHeight = with(density) { Dimensions.quintuple.toPx() }
    val bottomBarOffsetY by animateFloatAsState(
        targetValue = if (isSearching && !isFirstRender) bottomBarHeight else 0f,
        animationSpec = tween(
            durationMillis = 300,
            // Delay the bottom bar animation to allow the search button to move up first
            delayMillis = if (isSearching) 150 else 0
        ),
        label = "BottomBarAnimation"
    )

    // Animation for search button to search bar
    var searchButtonPosition by remember { mutableStateOf(Offset.Zero) }
    var searchBarPosition by remember { mutableStateOf(Offset.Zero) }
    var searchButtonSize by remember { mutableStateOf(IntSize.Zero) }
    var searchBarSize by remember { mutableStateOf(IntSize.Zero) }

    // Animation progress for search button to search bar
    var animatingToSearchBar by remember { mutableStateOf(false) }
    val searchButtonAlpha by animateFloatAsState(
        targetValue = if (animatingToSearchBar || isSearching) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "SearchButtonAlpha"
    )

    // Animation for the animated search button that transforms
    val searchButtonTranslationY = remember { Animatable(0f) }
    val searchButtonWidth = remember { Animatable(60f) } // Starting with FAB size
    val searchButtonHeight = remember { Animatable(60f) } // Starting with FAB size
    val searchButtonCornerRadius =
        remember { Animatable(30f) } // Starting with circle shape (half of 60)

    // Show bottom sheet when in AddEventScreen state
    LaunchedEffect(state) {
        if (state is State.ShowAddEventScreen) {
            showBottomSheet = true
            sheetState.expand()
        } else {
            if (showBottomSheet) {
                sheetState.hide()
                showBottomSheet = false
            }
        }
    }

    // Mark first render complete after initialization
    LaunchedEffect(Unit) {
        isFirstRender = false
    }

    // Run the animation when search is toggled
    LaunchedEffect(isSearching) {
        if (isSearching && searchButtonPosition != Offset.Zero && searchBarPosition != Offset.Zero) {
            // Calculate the distance for the animation
            val distanceY = searchBarPosition.y - searchButtonPosition.y
            
            // Mark we're animating
            animatingToSearchBar = true
            
            // Ensure bottomBar animation doesn't start until button animation is complete
            launch {
                // Animate the vertical movement
                searchButtonTranslationY.animateTo(
                    targetValue = distanceY,
                    animationSpec = tween(durationMillis = 300)
                )
                
                // Now that the button has reached the top, trigger the bottom bar animation
                // by setting this flag which will be collected by the bottomBarOffsetY animation
                bottomBarAnimStarted = true
            }
            
            // These animations can run in parallel
            launch {
                // Animate width expansion
                searchButtonWidth.animateTo(
                    targetValue = searchBarSize.width.toFloat(),
                    animationSpec = tween(durationMillis = 300)
                )
            }
            
            launch {
                // Animate height adjustment
                searchButtonHeight.animateTo(
                    targetValue = searchBarSize.height.toFloat(),
                    animationSpec = tween(durationMillis = 300)
                )
            }
            
            launch {
                // Animate corner radius change (from circle to rounded rectangle)
                searchButtonCornerRadius.animateTo(
                    targetValue = 28f, // Match the search bar corner radius
                    animationSpec = tween(durationMillis = 300)
                )
            }
            
            // When all animations complete, finish the transition
            // Small delay to ensure animations finish
            launch {
                kotlinx.coroutines.delay(320)
                animatingToSearchBar = false
            }
        } else if (!isSearching) {
            // Reset animation values for next time
            searchButtonTranslationY.snapTo(0f)
            searchButtonWidth.snapTo(60f)
            searchButtonHeight.snapTo(60f)
            searchButtonCornerRadius.snapTo(30f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                // Only show SearchBar when search is active
                if (isSearching) {
                    val focusRequester = remember { FocusRequester() }
                    val keyboardController = LocalSoftwareKeyboardController.current

                    // Request focus when search is activated
                    LaunchedEffect(isSearching) {
                        if (isSearching) {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        }
                    }

                    // Track the position of the search bar for animation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                searchBarPosition = coordinates.positionInRoot()
                                searchBarSize = coordinates.size
                            }
                    ) {
                        // Only show the actual search bar once animation is complete
                        AnimatedVisibility(
                            visible = isSearching && !animatingToSearchBar,
                            enter = fadeIn(animationSpec = tween(durationMillis = 100)),
                            exit = fadeOut()
                        ) {
                            SearchBar(
                                inputField = {
                                    SearchBarDefaults.InputField(
                                        query = searchText,
                                        onQueryChange = { 
                                            searchText = it
                                            onInteraction(Interaction.SearchTextChanged(it))
                                            // Filter locally for immediate response
                                            filteredEvents = if (it.isEmpty()) {
                                                currentEventItems
                                            } else {
                                                currentEventItems.filter { event ->
                                                    event.title.contains(it, ignoreCase = true)
                                                }
                                            }
                                        },
                                        onSearch = { /* Submit not needed, we filter as user types */ },
                                        expanded = isSearching,
                                        onExpandedChange = { 
                                            if (!it) {
                                                isSearching = false
                                                onInteraction(Interaction.ToggleSearch)
                                            } else {
                                                isSearching = true
                                                onInteraction(Interaction.ToggleSearch)
                                            }
                                        },
                                        placeholder = { Text("Search events") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back",
                                                modifier = Modifier.clickable { 
                                                    isSearching = false
                                                    keyboardController?.hide()
                                                    onInteraction(Interaction.ToggleSearch)
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
                                onExpandedChange = { 
                                    if (!it) {
                                        isSearching = false
                                        keyboardController?.hide()
                                        onInteraction(Interaction.ToggleSearch)
                                    } else {
                                        isSearching = true
                                        onInteraction(Interaction.ToggleSearch)
                                    }
                                },
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
                                                isSearching = false
                                                keyboardController?.hide()
                                                // First hide search, then navigate
                                                onInteraction(Interaction.ToggleSearch)
                                                onInteraction(Interaction.OpenEventItemDetails(event.id)) 
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // Always show the bottom bar placeholder to maintain layout consistency during animation
                Box(modifier = Modifier.height(Dimensions.quadruple)) {}
            },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {}
        ) { paddingValues ->
            Surface(modifier = modifier) {
                when (val currentState = state) {
                    State.Init -> onInteraction(Interaction.Init)
                    is State.ShowEventsGrid -> {
                        // Only show the main grid when not searching
                        if (!isSearching) {
                            EventListGrid(
                                onEventItemClick = { eventItemId ->
                                    onInteraction(
                                        Interaction.OpenEventItemDetails(
                                            eventItemId
                                        )
                                    )
                                },
                                onEventItemSelection = { onInteraction(Interaction.Select(it)) },
                                events = currentState.eventItems,
                                selectedEventIds = selectedEventIds,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                            )
                        } else {
                            // Empty placeholder when searching - content is in the SearchBar
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                            )
                        }
                    }

                    is State.ShowAddEventScreen -> {
                        EventListGrid(
                            onEventItemClick = { eventItemId ->
                                onInteraction(
                                    Interaction.OpenEventItemDetails(
                                        eventItemId
                                    )
                                )
                            },
                            onEventItemSelection = { onInteraction(Interaction.Select(it)) },
                            events = currentEventItems,
                            selectedEventIds = selectedEventIds,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        )
                    }
                }
            }
        }

        // Add draggable bottom bar that shows sheet when dragged
        // Apply animation offset to the bottom bar
        DraggableBottomBarWithFAB(
            onClick = {
                isSearching = !isSearching
                onInteraction(Interaction.ToggleSearch)
            },
            onDragUp = { onInteraction(Interaction.AddEventItem) },
            onShowDeleted = { /* TODO: Implement show deleted events */ },
            onShowArchived = { /* TODO: Implement show archived events */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset { IntOffset(0, bottomBarOffsetY.roundToInt()) }
                .alpha(searchButtonAlpha), // Fade out when animating
            fabPositionCallback = { position, size ->
                // Specifically track the FAB position
                searchButtonPosition = position
                searchButtonSize = size
            }
        )

        // Animated Search Button that morphs into Search Bar
        if (animatingToSearchBar) {
            Surface(
                modifier = Modifier
                    .size(
                        width = with(LocalDensity.current) { searchButtonWidth.value.toDp() },
                        height = with(LocalDensity.current) { searchButtonHeight.value.toDp() }
                    )
                    .offset {
                        IntOffset(
                            x = searchButtonPosition.x.roundToInt(),
                            y = (searchButtonPosition.y + searchButtonTranslationY.value).roundToInt()
                        )
                    },
                shape = RoundedCornerShape(with(LocalDensity.current) { searchButtonCornerRadius.value.toDp() }),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Show search icon fading out as it expands
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Animation",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Show bottom sheet for add event screen
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    onInteraction(Interaction.Init)
                },
                sheetState = sheetState,
                dragHandle = { DragHandle() }
            ) {
                AddEventScreen(
                    onEventCreated = { event ->
                        onInteraction(Interaction.EventItemAdded(event))
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onInteraction(Interaction.Init)
                            }
                        }
                    },
                    onCancel = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onInteraction(Interaction.Init)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun DraggableBottomBarWithFAB(
    onClick: () -> Unit,
    onDragUp: () -> Unit,
    onShowDeleted: () -> Unit,
    onShowArchived: () -> Unit,
    modifier: Modifier = Modifier,
    fabPositionCallback: ((Offset, IntSize) -> Unit)? = null
) {
    // Track drag progress with a state
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val dragThreshold = -100f // Threshold to trigger action

    // Reset and trigger action if needed
    LaunchedEffect(dragOffset) {
        if (dragOffset < dragThreshold) {
            onDragUp()
            dragOffset = 0f
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        // Reset on drag end if threshold not met
                        if (dragOffset > dragThreshold) {
                            dragOffset = 0f
                        }
                    },
                    onDragCancel = {
                        dragOffset = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Only track vertical drags (y-axis)
                        dragOffset += dragAmount.y
                        // Limit dragging down
                        if (dragOffset > 0f) dragOffset = 0f
                    }
                )
            }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.quadruple)
                .align(Alignment.BottomCenter),
            color = MaterialTheme.colorScheme.background,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationButton(
                    icon = Icons.Default.Delete,
                    label = "Deleted",
                    onClick = onShowDeleted,
                    modifier = Modifier.weight(1f)
                )

                Box(modifier = Modifier.weight(1f))

                NavigationButton(
                    icon = Icons.Default.Archive,
                    label = "Archived",
                    onClick = onShowArchived,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-30).dp)
                .onGloballyPositioned { coordinates ->
                    // Capture the FAB position for animation
                    fabPositionCallback?.invoke(coordinates.positionInRoot(), coordinates.size)
                },
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp
            )
        ) {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Search Events",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun NavigationButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DragHandle() {
    Surface(
        modifier = Modifier
            .padding(vertical = Dimensions.default)
            .width(Dimensions.double)
            .height(Dimensions.quarter),
        shape = RoundedCornerShape(percent = 50),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    ) {}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchEventItem(
    event: EventItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                Text(
                    text = event.getNumberOfDays().toString(),
                    fontSize = TextUnit(16f, TextUnitType.Em),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                Text(
                    text = "Days",
                    style = MaterialTheme.typography.bodyMediumEmphasized,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun CollectEvents(
    eventsFlow: Flow<Event>,
    onEvent: (Event) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        eventsFlow.collect { event ->
            onEvent(event)
        }
    }
}

@DefaultPreviews
@Composable
internal fun EventListPreview(
    @PreviewParameter(EventListPreviewParameterProvider::class)
    state: State
) {
    EventListImpl(
        onInteraction = {},
        stateflow = MutableStateFlow(state),
        eventsFlow = emptyFlow(),
        selectedEventIds = emptyList(),
        currentEventItems = emptyList()
    )
}

internal class EventListPreviewParameterProvider : CollectionPreviewParameterProvider<State>(
    listOf(
        State.Init,
        State.ShowEventsGrid(
            listOf(
                EventItem(
                    id = 0,
                    title = "Event 1",
                    description = "Event 1 Description",
                    date = LocalDate.now().plusDays(5)
                )
            )
        )
    )
)