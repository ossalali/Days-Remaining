package com.ossalali.daysremaining.presentation.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.BuildConfig
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.infrastructure.appLogger
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.ui.previews.DefaultPreviews
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.EventDetailsViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    eventId: Int? = null,
    event: EventItem? = null,
    onBackClick: () -> Unit,
    onDeleteEvent: (EventItem) -> Unit,
    viewModel: EventDetailsViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
) {
    if (eventId != null) {
        LaunchedEffect(eventId) { viewModel.loadEventById(eventId) }
    }

    val eventState by viewModel.event.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val displayEvent = event ?: eventState

    EventDetailsContent(
        event = displayEvent,
        isLoading = isLoading,
        isSaving = isSaving,
        onUpdateEvent = { updatedEvent ->
            viewModel.saveEvent(updatedEvent)
            onBackClick()
        },
        onDeleteEvent = { eventToDelete ->
            onDeleteEvent(eventToDelete)
            viewModel.eventDeletedHandled()
            onBackClick()
        },
        paddingValues = paddingValues,
    )
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun calculateResponsiveHorizontalPadding(): Dp {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    val isTablet =
        with(density) {
            val minDimensionDp = minOf(screenWidthDp, screenHeightDp)

            minDimensionDp >= 600.dp
        }

    return when {
        isTablet && isLandscape -> screenWidthDp * 0.25f

        isTablet && !isLandscape -> screenWidthDp * 0.15f

        !isTablet && isLandscape -> screenWidthDp * 0.2f

        else -> Dimensions.default
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun calculateActionBarPadding(): Dp {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp.dp

    val isTablet =
        with(density) {
            val minDimensionDp = minOf(screenWidthDp, configuration.screenHeightDp.dp)
            minDimensionDp >= 600.dp
        }

    return when {
        isTablet -> Dimensions.triple

        isLandscape -> Dimensions.double

        else -> Dimensions.default * 2
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun calculateScrollPadding(): ScrollPaddingConfig {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenHeightDp = configuration.screenHeightDp.dp

    val isTablet =
        with(density) {
            val minDimensionDp = minOf(configuration.screenWidthDp.dp, screenHeightDp)
            minDimensionDp >= 600.dp
        }

    return when {
        isTablet ->
            ScrollPaddingConfig(
                titleFieldPadding = 250,
                descriptionFieldPadding = 200,
                focusLostPadding = 150,
            )

        isLandscape ->
            ScrollPaddingConfig(
                titleFieldPadding = 150,
                descriptionFieldPadding = 120,
                focusLostPadding = 80,
            )

        else ->
            ScrollPaddingConfig(
                titleFieldPadding = 200,
                descriptionFieldPadding = 150,
                focusLostPadding = 100,
            )
    }
}

private data class ScrollPaddingConfig(
    val titleFieldPadding: Int,
    val descriptionFieldPadding: Int,
    val focusLostPadding: Int,
)

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsContent(
    event: EventItem?,
    isLoading: Boolean,
    isSaving: Boolean,
    onUpdateEvent: (EventItem) -> Unit,
    onDeleteEvent: (EventItem) -> Unit,
    paddingValues: PaddingValues,
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val titleState = remember(event) { TextFieldState(initialText = event?.title ?: "") }
    var selectedDateMillis by
        rememberSaveable(event) {
            mutableLongStateOf(
                (event?.date?.toEpochDay() ?: LocalDate.now().toEpochDay()) * 24 * 60 * 60 * 1000
            )
        }
    val descriptionState =
        remember(event) { TextFieldState(initialText = event?.description ?: "") }

    val originalTitle = event?.title ?: ""
    val originalDateMillis =
        remember(event) {
            (event?.date?.toEpochDay() ?: LocalDate.now().toEpochDay()) * 24 * 60 * 60 * 1000
        }
    val originalDescription = event?.description ?: ""

    if (showDeleteConfirmDialog && event != null) {
        DeleteAlertDialog(
            eventTitle = event.title,
            onConfirm = {
                onDeleteEvent(event)
                showDeleteConfirmDialog = false
            },
            onDismiss = { showDeleteConfirmDialog = false },
        )
    }

    val screenHorizontalPadding = calculateResponsiveHorizontalPadding()
    val actionBarPadding = calculateActionBarPadding()
    val scrollPaddingConfig = calculateScrollPadding()

    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).imePadding()) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (event != null) {
            ScrollableEventForm(
                event = event,
                titleState = titleState,
                selectedDateMillis = selectedDateMillis,
                onDateChanged = { selectedDateMillis = it },
                descriptionState = descriptionState,
                screenHorizontalPadding = screenHorizontalPadding,
                scrollPaddingConfig = scrollPaddingConfig,
                modifier = Modifier.weight(1f),
            )

            BottomActionBar(
                event = event,
                titleState = titleState,
                selectedDateMillis = selectedDateMillis,
                descriptionState = descriptionState,
                originalTitle = originalTitle,
                originalDateMillis = originalDateMillis,
                originalDescription = originalDescription,
                isSaving = isSaving,
                onSave = onUpdateEvent,
                onDelete = { showDeleteConfirmDialog = true },
                horizontalPadding = actionBarPadding,
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Event not found", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SaveEventFab(
    event: EventItem,
    titleState: TextFieldState,
    selectedDateMillis: Long,
    descriptionState: TextFieldState,
    originalTitle: String,
    originalDateMillis: Long,
    originalDescription: String,
    isSaving: Boolean,
    onSave: (EventItem) -> Unit,
) {
    val isTitleValid by remember { derivedStateOf { titleState.text.isNotBlank() } }

    val selectedDateMillisState = rememberUpdatedState(selectedDateMillis)

    val hasChanges by
        remember(titleState.text, descriptionState.text, selectedDateMillis) {
            derivedStateOf {
                if (titleState.text.isBlank()) {
                    return@derivedStateOf false
                }
                val titleChanged = titleState.text.toString().trim() != originalTitle
                val dateChanged = selectedDateMillisState.value != originalDateMillis
                val descriptionChanged =
                    descriptionState.text.toString().trim() != originalDescription

                if (BuildConfig.DEBUG) {
                    appLogger()
                        .d(
                            tag = "CHANGE",
                            message =
                                "Change detection - Title: $titleChanged, Date: $dateChanged, Description: $descriptionChanged",
                        )
                    appLogger()
                        .d(
                            tag = "CHANGE",
                            message =
                                "Selected date millis: ${selectedDateMillisState.value}, Original date millis: $originalDateMillis",
                        )
                }

                titleChanged || dateChanged || descriptionChanged
            }
        }

    val canSave by remember { derivedStateOf { isTitleValid && hasChanges && !isSaving } }

    FloatingActionButton(
        onClick = {
            if (canSave) {
                val updatedEvent =
                    event.copy(
                        title = titleState.text.toString().trim(),
                        date =
                            Instant.ofEpochMilli(selectedDateMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        description = descriptionState.text.toString().trim(),
                    )
                onSave(updatedEvent)
            }
        },
        containerColor =
            if (canSave) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor =
            if (canSave) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    ) {
        if (isSaving) {
            CircularProgressIndicator()
        } else {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Save Event")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomActionBar(
    event: EventItem,
    titleState: TextFieldState,
    selectedDateMillis: Long,
    descriptionState: TextFieldState,
    originalTitle: String,
    originalDateMillis: Long,
    originalDescription: String,
    isSaving: Boolean,
    onSave: (EventItem) -> Unit,
    onDelete: () -> Unit,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = horizontalPadding).imePadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        DeleteEventFab(onDelete = onDelete)

        SaveEventFab(
            event = event,
            titleState = titleState,
            selectedDateMillis = selectedDateMillis,
            descriptionState = descriptionState,
            originalTitle = originalTitle,
            originalDateMillis = originalDateMillis,
            originalDescription = originalDescription,
            isSaving = isSaving,
            onSave = onSave,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteEventFab(onDelete: () -> Unit) {
    FloatingActionButton(
        onClick = onDelete,
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError,
    ) {
        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Event")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScrollableEventForm(
    event: EventItem,
    titleState: TextFieldState,
    selectedDateMillis: Long,
    onDateChanged: (Long) -> Unit,
    descriptionState: TextFieldState,
    screenHorizontalPadding: Dp,
    scrollPaddingConfig: ScrollPaddingConfig,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier =
            modifier.verticalScroll(scrollState).padding(horizontal = Dimensions.default).clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
    ) {
        EventContent(
            isArchived = event.isArchived,
            titleState = titleState,
            selectedDateMillis = selectedDateMillis,
            onDateChanged = onDateChanged,
            descriptionState = descriptionState,
            screenHorizontalPadding = screenHorizontalPadding,
            scrollPaddingConfig = scrollPaddingConfig,
            scrollState = scrollState,
            focusManager = focusManager,
            keyboardController = keyboardController,
        )
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EventContent(
    isArchived: Boolean,
    titleState: TextFieldState,
    selectedDateMillis: Long,
    onDateChanged: (Long) -> Unit,
    descriptionState: TextFieldState,
    screenHorizontalPadding: Dp,
    scrollPaddingConfig: ScrollPaddingConfig,
    scrollState: ScrollState,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
    val coroutineScope = rememberCoroutineScope()
    val titleFocusRequester = remember { FocusRequester() }
    val descriptionFocusRequester = remember { FocusRequester() }

    var titleFieldPosition by remember { mutableIntStateOf(0) }
    var descriptionFieldPosition by remember { mutableIntStateOf(0) }
    var isDescriptionFocused by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isTablet = minOf(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp) >= 600.dp

    val descriptionLineLimits =
        remember(isDescriptionFocused, isLandscape, isTablet) {
            val (minLines, maxLines) =
                when {
                    isTablet && isDescriptionFocused -> Pair(5, 12)
                    isTablet && !isDescriptionFocused -> Pair(4, 10)

                    isLandscape && isDescriptionFocused -> Pair(3, 6)
                    isLandscape && !isDescriptionFocused -> Pair(2, 4)

                    isDescriptionFocused -> Pair(4, 10)
                    else -> Pair(3, 8)
                }

            TextFieldLineLimits.MultiLine(minHeightInLines = minLines, maxHeightInLines = maxLines)
        }

    LaunchedEffect(selectedDateMillis) { datePickerState.selectedDateMillis = selectedDateMillis }

    val selectedLocalDate =
        Instant.ofEpochMilli(selectedDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDate = selectedLocalDate.format(dateFormatter)

    val titleError by remember { derivedStateOf { titleState.text.isBlank() } }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = screenHorizontalPadding)) {
        if (isArchived) {
            Text(
                modifier =
                    Modifier.align(Alignment.CenterHorizontally)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = CircleShape,
                        )
                        .padding(
                            horizontal = Dimensions.default + Dimensions.half,
                            vertical = Dimensions.half + Dimensions.quarter,
                        ),
                text = "ARCHIVED",
                textAlign = TextAlign.Center,
            )
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = ChronoUnit.DAYS.between(LocalDate.now(), selectedLocalDate).toString(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayLarge,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.days_remaining),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )

        val verticalSpacing =
            when {
                isTablet -> Dimensions.double
                isLandscape -> Dimensions.half
                else -> Dimensions.default
            }

        Spacer(modifier = Modifier.height(verticalSpacing))

        OutlinedTextField(
            state = titleState,
            modifier =
                Modifier.fillMaxWidth()
                    .focusRequester(titleFocusRequester)
                    .onGloballyPositioned { coordinates: LayoutCoordinates ->
                        titleFieldPosition = coordinates.positionInParent().y.toInt()
                    }
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                val targetScrollPosition =
                                    maxOf(
                                        0,
                                        titleFieldPosition - scrollPaddingConfig.titleFieldPadding,
                                    )
                                scrollState.animateScrollTo(targetScrollPosition)
                            }
                        } else {

                            coroutineScope.launch {
                                if (scrollState.value > titleFieldPosition) {
                                    scrollState.animateScrollTo(
                                        maxOf(
                                            0,
                                            titleFieldPosition -
                                                scrollPaddingConfig.focusLostPadding,
                                        )
                                    )
                                }
                            }
                        }
                    },
            label = { Text(text = "Title") },
            placeholder = { Text(text = "Enter event title") },
            lineLimits = TextFieldLineLimits.SingleLine,
            isError = titleError,
            supportingText = {
                if (titleError) {
                    Text(text = "Title cannot be empty", color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions =
                KeyboardOptions(
                    imeAction = ImeAction.Next,
                    autoCorrectEnabled = true,
                    capitalization = KeyboardCapitalization.Sentences,
                ),
            onKeyboardAction = { descriptionFocusRequester.requestFocus() },
        )

        val density = LocalDensity.current
        val configuration = LocalConfiguration.current

        val chipHeight =
            with(density) {
                val baseHeight = Dimensions.triple
                val fontScale = density.fontScale
                val isTablet =
                    minOf(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp) >= 600.dp

                when {
                    isTablet -> baseHeight * fontScale * 1.2f
                    else -> baseHeight * fontScale
                }
            }

        val chipWidth =
            with(density) {
                val baseWidth = Dimensions.nonuple
                val fontScale = density.fontScale
                val isTablet =
                    minOf(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp) >= 600.dp

                when {
                    isTablet -> baseWidth * fontScale * 1.1f
                    else -> baseWidth * fontScale
                }
            }

        InputChip(
            modifier = Modifier.height(chipHeight).width(chipWidth),
            selected = true,
            onClick = { showDatePicker = true },
            label = { Text(formattedDate) },
            leadingIcon = {
                Icon(
                    modifier = Modifier.offset(y = (-2).dp),
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = "Add Date to event",
                )
            },
        )
        Spacer(modifier = Modifier.height(verticalSpacing))

        OutlinedTextField(
            state = descriptionState,
            modifier =
                Modifier.fillMaxWidth()
                    .focusRequester(descriptionFocusRequester)
                    .onGloballyPositioned { coordinates: LayoutCoordinates ->
                        descriptionFieldPosition = coordinates.positionInParent().y.toInt()
                    }
                    .onFocusChanged { focusState ->
                        isDescriptionFocused = focusState.isFocused
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                val targetScrollPosition =
                                    maxOf(
                                        0,
                                        descriptionFieldPosition -
                                            scrollPaddingConfig.descriptionFieldPadding,
                                    )
                                scrollState.animateScrollTo(targetScrollPosition)
                            }
                        } else {

                            coroutineScope.launch {
                                if (scrollState.value > descriptionFieldPosition) {
                                    scrollState.animateScrollTo(
                                        maxOf(
                                            0,
                                            descriptionFieldPosition -
                                                scrollPaddingConfig.focusLostPadding,
                                        )
                                    )
                                }
                            }
                        }
                    },
            lineLimits = descriptionLineLimits,
            label = { Text(text = "Description") },
            placeholder = { Text(text = "Add event details (optional)") },
            keyboardOptions =
                KeyboardOptions(
                    imeAction = ImeAction.Default,
                    autoCorrectEnabled = true,
                    capitalization = KeyboardCapitalization.Sentences,
                ),
            onKeyboardAction = {
                focusManager.clearFocus()
                keyboardController?.hide()
            },
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                onDateChanged(millis)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@DefaultPreviews()
@Composable
fun EventDetailsContentLoadingPreview() {
    MyAppTheme {
        EventDetailsContent(
            event = null,
            isLoading = true,
            isSaving = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            paddingValues = PaddingValues(),
        )
    }
}

@DefaultPreviews()
@Composable
fun EventDetailsContentSavingPreview() {
    val sampleEvent =
        EventItem(
            id = 1,
            title = "Event Being Saved",
            date = LocalDate.now().plusDays(5),
            description =
                "This event is currently being saved with the new keyboard-aware layout structure.",
        )

    MyAppTheme {
        EventDetailsContent(
            event = sampleEvent,
            isLoading = false,
            isSaving = true,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            paddingValues = PaddingValues(),
        )
    }
}

@DefaultPreviews()
@Composable
fun EventDetailsContentNotFoundPreview() {
    MyAppTheme {
        EventDetailsContent(
            event = null,
            isLoading = false,
            isSaving = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            paddingValues = PaddingValues(),
        )
    }
}

@DefaultPreviews()
@Composable
fun EventDetailsPreview() {
    val sampleEvent =
        EventItem(
            id = 1,
            title = "Sample Event Title",
            date = LocalDate.now().plusDays(10),
            description = "This is a sample event description with the new keyboard-aware layout.",
            isArchived = true,
        )

    MyAppTheme {
        EventDetailsContent(
            event = sampleEvent,
            isLoading = false,
            isSaving = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            paddingValues = PaddingValues(),
        )
    }
}

@DefaultPreviews()
@Composable
fun EventDetailsShortContentPreview() {
    val sampleEvent =
        EventItem(
            id = 1,
            title = "Short",
            date = LocalDate.now().plusDays(3),
            description = "Brief.",
        )

    MyAppTheme {
        EventDetailsContent(
            event = sampleEvent,
            isLoading = false,
            isSaving = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            paddingValues = PaddingValues(),
        )
    }
}

@DefaultPreviews()
@Composable
fun EventDetailsLongContentPreview() {
    val sampleEvent =
        EventItem(
            id = 1,
            title = "Very Long Event Title That Might Wrap to Multiple Lines in Certain Scenarios",
            date = LocalDate.now().plusDays(30),
            description =
                """
                This is a very long description that tests how the keyboard-aware layout handles extensive content. 
                
                It includes multiple paragraphs to simulate real-world usage where users might write detailed event descriptions.
                
                The new scrollable layout should handle this content gracefully, ensuring that when the keyboard appears, users can still access all parts of the description field.
                
                This tests the ScrollableEventForm component's ability to manage long content while maintaining proper keyboard interactions and focus management.
                
                Additional content to further test scrolling behavior and ensure the bottom action bar remains accessible even with extensive text content.
            """
                    .trimIndent(),
        )

    MyAppTheme {
        EventDetailsContent(
            event = sampleEvent,
            isLoading = false,
            isSaving = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            paddingValues = PaddingValues(),
        )
    }
}

@DefaultPreviews()
@Composable
fun EventDetailsEmptyFieldsPreview() {
    val sampleEvent = EventItem(id = 1, title = "", date = LocalDate.now(), description = "")

    MyAppTheme {
        EventDetailsContent(
            event = sampleEvent,
            isLoading = false,
            isSaving = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            paddingValues = PaddingValues(),
        )
    }
}

@DefaultPreviews()
@Composable
fun EventDetailsWithSystemBarsPreview() {
    val sampleEvent =
        EventItem(
            id = 1,
            title = "Event with System UI",
            date = LocalDate.now().plusDays(7),
            description =
                "This preview shows how the keyboard-aware layout works with system bars and navigation padding.",
        )

    MyAppTheme {
        EventDetailsContent(
            event = sampleEvent,
            isLoading = false,
            isSaving = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            paddingValues = PaddingValues(top = 24.dp, bottom = 80.dp, start = 16.dp, end = 16.dp),
        )
    }
}

@DefaultPreviews()
@Composable
fun EventDetailsArchivedPreview() {
    val sampleEvent =
        EventItem(
            id = 1,
            title = "Archived Event",
            date = LocalDate.now().minusDays(30),
            description =
                "This archived event tests the keyboard-aware layout with the archived status indicator.",
            isArchived = true,
        )

    MyAppTheme {
        EventDetailsContent(
            event = sampleEvent,
            isLoading = false,
            isSaving = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            paddingValues = PaddingValues(),
        )
    }
}

@DefaultPreviews()
@Composable
fun ScrollableEventFormPreview() {
    val sampleEvent =
        EventItem(
            id = 1,
            title = "Form Component Test",
            date = LocalDate.now().plusDays(15),
            description =
                "Testing the ScrollableEventForm component in isolation to verify proper keyboard-aware behavior.",
        )

    val titleState = remember { TextFieldState(initialText = sampleEvent.title) }
    val descriptionState = remember { TextFieldState(initialText = sampleEvent.description) }

    MyAppTheme {
        ScrollableEventForm(
            event = sampleEvent,
            titleState = titleState,
            selectedDateMillis = sampleEvent.date.toEpochDay() * 24 * 60 * 60 * 1000,
            onDateChanged = { /* Preview - no action */ },
            descriptionState = descriptionState,
            screenHorizontalPadding = 16.dp,
            scrollPaddingConfig =
                ScrollPaddingConfig(
                    titleFieldPadding = 200,
                    descriptionFieldPadding = 150,
                    focusLostPadding = 100,
                ),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@DefaultPreviews()
@Composable
fun BottomActionBarPreview() {
    val sampleEvent =
        EventItem(
            id = 1,
            title = "Action Bar Test",
            date = LocalDate.now().plusDays(5),
            description = "Testing the BottomActionBar component.",
        )

    val titleState = remember { TextFieldState(initialText = sampleEvent.title) }
    val descriptionState = remember { TextFieldState(initialText = sampleEvent.description) }

    MyAppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            BottomActionBar(
                event = sampleEvent,
                titleState = titleState,
                selectedDateMillis = sampleEvent.date.toEpochDay() * 24 * 60 * 60 * 1000,
                descriptionState = descriptionState,
                originalTitle = sampleEvent.title,
                originalDateMillis = sampleEvent.date.toEpochDay() * 24 * 60 * 60 * 1000,
                originalDescription = sampleEvent.description,
                isSaving = false,
                onSave = { /* Preview - no action */ },
                onDelete = { /* Preview - no action */ },
                horizontalPadding = 16.dp,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Preview(
    name = "Phone Portrait - Keyboard Simulation",
    showSystemUi = true,
    device = Devices.PIXEL_7_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun EventDetailsKeyboardSimulationPreview() {
    val sampleEvent =
        EventItem(
            id = 1,
            title = "Keyboard Test Event",
            date = LocalDate.now().plusDays(12),
            description =
                "This preview simulates keyboard interaction by adding bottom padding to represent the soft keyboard area.",
        )

    MyAppTheme {
        EventDetailsContent(
            event = sampleEvent,
            isLoading = false,
            isSaving = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            paddingValues = PaddingValues(bottom = 300.dp),
        )
    }
}

@Preview(
    name = "Tablet Landscape - Keyboard Aware",
    showSystemUi = true,
    device = Devices.PIXEL_TABLET,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun EventDetailsTabletLandscapePreview() {
    val sampleEvent =
        EventItem(
            id = 1,
            title = "Tablet Landscape Event",
            date = LocalDate.now().plusDays(8),
            description =
                "Testing the responsive layout behavior on tablet in landscape orientation with the new keyboard-aware structure.",
        )

    MyAppTheme {
        EventDetailsContent(
            event = sampleEvent,
            isLoading = false,
            isSaving = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            paddingValues = PaddingValues(),
        )
    }
}

@Preview(
    name = "Small Screen - Compact Layout",
    showSystemUi = true,
    device = "spec:width=360dp,height=640dp,dpi=480",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun EventDetailsSmallScreenPreview() {
    val sampleEvent =
        EventItem(
            id = 1,
            title = "Small Screen Test",
            date = LocalDate.now().plusDays(4),
            description =
                "Testing keyboard-aware layout on smaller screens to ensure proper space utilization.",
        )

    MyAppTheme {
        EventDetailsContent(
            event = sampleEvent,
            isLoading = false,
            isSaving = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            paddingValues = PaddingValues(),
        )
    }
}
