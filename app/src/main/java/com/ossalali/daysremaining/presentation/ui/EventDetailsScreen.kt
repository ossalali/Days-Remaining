package com.ossalali.daysremaining.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.infrastructure.ImageStorage
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
    onBackClick: () -> Unit,
    onDeleteEvent: (EventItem) -> Unit = {},
    viewModel: EventDetailsViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
) {
    val isAddMode = eventId == null

    LaunchedEffect(eventId, isAddMode) {
        if (isAddMode) {
            viewModel.initializeForAddMode()
        } else {
            viewModel.initializeForEditMode(eventId)
        }
    }

    val eventState by viewModel.event.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val hasChanges by viewModel.hasChanges.collectAsState()
    val viewModelIsAddMode by viewModel.isAddMode.collectAsState()

    val displayEvent = if (isAddMode) null else eventState

    EventDetailsContent(
        event = displayEvent,
        isLoading = isLoading,
        isSaving = isSaving,
        isAddMode = viewModelIsAddMode,
        hasChanges = hasChanges,
        onUpdateEvent = { updatedEvent ->
            viewModel.saveEvent(updatedEvent)
            onBackClick()
        },
        onDeleteEvent = { eventToDelete ->
            onDeleteEvent(eventToDelete)
            viewModel.eventDeletedHandled()
            onBackClick()
        },
        onTrackChanges = { changes -> viewModel.trackChanges(changes) },
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
    isAddMode: Boolean,
    hasChanges: Boolean,
    onUpdateEvent: (EventItem) -> Unit,
    onDeleteEvent: (EventItem) -> Unit,
    onTrackChanges: (Boolean) -> Unit,
    paddingValues: PaddingValues,
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val titleState = remember { TextFieldState() }
    var selectedDateMillis by rememberSaveable {
        mutableLongStateOf(LocalDate.now().toEpochDay() * 24 * 60 * 60 * 1000)
    }
    val descriptionState = remember { TextFieldState() }
    var imageUri by rememberSaveable { mutableStateOf<String?>(null) }
    var baselineEvent by remember { mutableStateOf<EventItem?>(null) }

    LaunchedEffect(event?.id, isAddMode) {
        if (isAddMode) {
            baselineEvent = null
            titleState.edit { replace(0, length, "") }
            descriptionState.edit { replace(0, length, "") }
            selectedDateMillis = LocalDate.now().toEpochDay() * 24 * 60 * 60 * 1000
            imageUri = null
        } else if (event != null && baselineEvent?.id != event.id) {
            baselineEvent = event
            titleState.edit { replace(0, length, event.title) }
            descriptionState.edit { replace(0, length, event.description) }
            selectedDateMillis = event.date.toEpochDay() * 24 * 60 * 60 * 1000
            imageUri = event.imageUri
        }
    }

    val originalTitle = baselineEvent?.title ?: ""
    val originalDateMillis =
        baselineEvent?.date?.toEpochDay()?.times(24 * 60 * 60 * 1000)
            ?: (LocalDate.now().toEpochDay() * 24 * 60 * 60 * 1000)
    val originalDescription = baselineEvent?.description ?: ""
    val originalImageUri = baselineEvent?.imageUri

    LaunchedEffect(
        titleState.text,
        selectedDateMillis,
        descriptionState.text,
        imageUri,
        isAddMode,
    ) {
        if (!isAddMode && baselineEvent != null) {
            val titleChanged = titleState.text.trim() != originalTitle
            val dateChanged = selectedDateMillis != originalDateMillis
            val descriptionChanged = descriptionState.text.trim() != originalDescription
            val imageChanged = imageUri != originalImageUri

            val hasChanges = titleChanged || dateChanged || descriptionChanged || imageChanged
            onTrackChanges(hasChanges)
        } else {
            onTrackChanges(false)
        }
    }

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
        } else if (event != null || isAddMode) {
            val displayEvent =
                event
                    ?: EventItem(
                        id = 0,
                        title = "",
                        date = LocalDate.now(),
                        description = "",
                        imageUri = null,
                        isArchived = false,
                    )

            ScrollableEventForm(
                event = displayEvent,
                titleState = titleState,
                selectedDateMillis = selectedDateMillis,
                onDateChanged = { selectedDateMillis = it },
                descriptionState = descriptionState,
                imageUri = imageUri,
                onImagePicked = { picked -> imageUri = picked },
                screenHorizontalPadding = screenHorizontalPadding,
                scrollPaddingConfig = scrollPaddingConfig,
                modifier = Modifier.weight(1f),
            )

            BottomActionBar(
                event = displayEvent,
                titleState = titleState,
                selectedDateMillis = selectedDateMillis,
                descriptionState = descriptionState,
                imageUri = imageUri,
                isSaving = isSaving,
                isAddMode = isAddMode,
                hasChanges = hasChanges,
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
    imageUri: String?,
    isSaving: Boolean,
    isAddMode: Boolean,
    hasChanges: Boolean,
    onSave: (EventItem) -> Unit,
) {
    val isTitleValid by remember { derivedStateOf { titleState.text.isNotBlank() } }

    val canSave by
        remember(isTitleValid, hasChanges, isSaving, isAddMode) {
            derivedStateOf {
                if (isAddMode) {

                    isTitleValid && !isSaving
                } else {

                    isTitleValid && hasChanges && !isSaving
                }
            }
        }

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
                        imageUri = imageUri,
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
    imageUri: String?,
    isSaving: Boolean,
    isAddMode: Boolean,
    hasChanges: Boolean,
    onSave: (EventItem) -> Unit,
    onDelete: () -> Unit,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = horizontalPadding).imePadding(),
        horizontalArrangement = if (isAddMode) Arrangement.End else Arrangement.SpaceBetween,
    ) {
        if (!isAddMode) {
            DeleteEventFab(onDelete = onDelete)
        }

        SaveEventFab(
            event = event,
            titleState = titleState,
            selectedDateMillis = selectedDateMillis,
            descriptionState = descriptionState,
            imageUri = imageUri,
            isSaving = isSaving,
            isAddMode = isAddMode,
            hasChanges = hasChanges,
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
    imageUri: String?,
    onImagePicked: (String?) -> Unit,
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
            imageUri = imageUri,
            onImagePicked = onImagePicked,
            screenHorizontalPadding = screenHorizontalPadding,
            scrollPaddingConfig = scrollPaddingConfig,
            scrollState = scrollState,
            focusManager = focusManager,
            keyboardController = keyboardController,
        )
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventContent(
    isArchived: Boolean,
    titleState: TextFieldState,
    selectedDateMillis: Long,
    onDateChanged: (Long) -> Unit,
    descriptionState: TextFieldState,
    imageUri: String?,
    onImagePicked: (String?) -> Unit,
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
    var showImagePickerDialog by rememberSaveable { mutableStateOf(false) }
    var showConfirmImageDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showFullScreenImage by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    val photoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION,
                    )
                } catch (_: Exception) {}

                val stored = ImageStorage.persistImageFromUri(context, uri)
                onImagePicked((stored ?: uri).toString())
            }
        }

    var cameraTempUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean
            ->
            if (success) {
                onImagePicked(cameraTempUri?.toString())
            }
        }

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

        Spacer(modifier = Modifier.height(verticalSpacing))

        val imagePreviewMaxWidth by
            remember(imageUri) {
                derivedStateOf {
                    if (imageUri.isNullOrBlank()) {
                        1f
                    } else {
                        0.9f
                    }
                }
            }

        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier =
                    Modifier.weight(imagePreviewMaxWidth)
                        .height(180.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(Dimensions.default),
                        )
                        .clickable {
                            if (!imageUri.isNullOrBlank()) {
                                showFullScreenImage = true
                            } else {
                                showImagePickerDialog = true
                            }
                        }
                        .padding(Dimensions.half),
                contentAlignment = Alignment.Center,
            ) {
                if (!imageUri.isNullOrBlank()) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.small),
                        model = imageUri,
                        contentDescription = "Event image",
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Text(
                        text = "Tap to add an image",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (!imageUri.isNullOrBlank()) {
                Spacer(modifier = Modifier.width(Dimensions.default))
                Column(
                    modifier = Modifier.weight(1f - imagePreviewMaxWidth).height(180.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    IconButton(
                        modifier =
                            Modifier.background(
                                shape = ShapeDefaults.Small,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                            ),
                        onClick = { showFullScreenImage = true },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Fullscreen,
                            contentDescription = "View fullscreen",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    IconButton(
                        modifier =
                            Modifier.background(
                                shape = ShapeDefaults.Small,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                            ),
                        onClick = { showImagePickerDialog = true },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhotoLibrary,
                            contentDescription = "Change image",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    IconButton(
                        modifier =
                            Modifier.background(
                                shape = ShapeDefaults.Small,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                            ),
                        onClick = { showConfirmImageDeleteDialog = true },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Remove image",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

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

        if (showImagePickerDialog) {
            AlertDialog(
                onDismissRequest = { showImagePickerDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showImagePickerDialog = false
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    ) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Choose photo",
                            )
                            Text("Choose photo")
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showImagePickerDialog = false
                            val imageFile = ImageStorage.createImageFileInAppStorage(context)
                            val uri =
                                FileProvider.getUriForFile(
                                    context,
                                    context.packageName + ".fileprovider",
                                    imageFile,
                                )
                            cameraTempUri = uri
                            cameraLauncher.launch(uri)
                        }
                    ) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Take photo",
                            )
                            Text("Take photo")
                        }
                    }
                },
                title = { Text("Add image") },
                text = { Text("Take or Choose an photo") },
            )
        }

        if (showFullScreenImage && !imageUri.isNullOrBlank()) {
            Dialog(
                onDismissRequest = { showFullScreenImage = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = imageUri,
                        contentDescription = "Event image fullscreen",
                        contentScale = ContentScale.Fit,
                    )
                    IconButton(
                        modifier = Modifier.align(Alignment.TopEnd).padding(Dimensions.default),
                        onClick = { showFullScreenImage = false },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                        )
                    }
                }
            }
        }

        if (showConfirmImageDeleteDialog) {
            AlertDialog(
                title = {
                    Text(text = "Delete image?", style = MaterialTheme.typography.titleLarge)
                },
                text = { Text("Do you want to delete the image?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onImagePicked(null)
                            showConfirmImageDeleteDialog = false
                        }
                    ) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Confirm Delete Image",
                            )
                            Text("OK")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmImageDeleteDialog = false }) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel Delete Image",
                            )
                            Text("Cancel")
                        }
                    }
                },
                onDismissRequest = { showConfirmImageDeleteDialog = false },
            )
        }
    }
}

// region Edit mode

@DefaultPreviews()
@Composable
fun EventDetailsContentLoadingPreview() {
    MyAppTheme {
        EventDetailsContent(
            event = null,
            isLoading = true,
            isSaving = false,
            isAddMode = false,
            hasChanges = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            onTrackChanges = { /* Preview - no action */ },
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
            isAddMode = false,
            hasChanges = true,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            onTrackChanges = { /* Preview - no action */ },
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
            isAddMode = false,
            hasChanges = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            onTrackChanges = { /* Preview - no action */ },
            paddingValues = PaddingValues(),
        )
    }
}

@DefaultPreviews()
@Composable
fun EventDetailsEmptyFieldsEditModePreview() {
    val sampleEvent = EventItem(id = 1, title = "", date = LocalDate.now(), description = "")

    MyAppTheme {
        EventDetailsContent(
            event = sampleEvent,
            isLoading = false,
            isSaving = false,
            isAddMode = false,
            hasChanges = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            onTrackChanges = { /* Preview - no action */ },
            paddingValues = PaddingValues(),
        )
    }
}

@DefaultPreviews()
@Composable
fun EventDetailsWithSystemBarsEditModePreview() {
    val sampleEvent =
        EventItem(
            id = 1,
            title = "Event with System UI",
            date = LocalDate.now().plusDays(7),
            description =
                "This preview shows how the keyboard-aware layout works with system bars and navigation padding in edit mode.",
        )

    MyAppTheme {
        EventDetailsContent(
            event = sampleEvent,
            isLoading = false,
            isSaving = false,
            isAddMode = false,
            hasChanges = true,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            onTrackChanges = { /* Preview - no action */ },
            paddingValues = PaddingValues(top = 24.dp, bottom = 80.dp, start = 16.dp, end = 16.dp),
        )
    }
}

@DefaultPreviews()
@Composable
fun EventDetailsArchivedEditModePreview() {
    val sampleEvent =
        EventItem(
            id = 1,
            title = "Archived Event",
            date = LocalDate.now().minusDays(30),
            description =
                "This archived event tests the keyboard-aware layout with the archived status indicator in edit mode.",
            isArchived = true,
        )

    MyAppTheme {
        EventDetailsContent(
            event = sampleEvent,
            isLoading = false,
            isSaving = false,
            isAddMode = false,
            hasChanges = true,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            onTrackChanges = { /* Preview - no action */ },
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
            imageUri = null,
            onImagePicked = { /* Preview - no action */ },
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
                imageUri = null,
                isSaving = false,
                isAddMode = false,
                hasChanges = true,
                onSave = { /* Preview - no action */ },
                onDelete = { /* Preview - no action */ },
                horizontalPadding = 16.dp,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@DefaultPreviews()
@Composable
fun EventDetailsKeyboardSimulationEditModePreview() {
    val sampleEvent =
        EventItem(
            id = 1,
            title = "Keyboard Test Event",
            date = LocalDate.now().plusDays(12),
            description =
                "This preview simulates keyboard interaction by adding bottom padding to represent the soft keyboard area in edit mode.",
        )

    MyAppTheme {
        EventDetailsContent(
            event = sampleEvent,
            isLoading = false,
            isSaving = false,
            isAddMode = false,
            hasChanges = true,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            onTrackChanges = { /* Preview - no action */ },
            paddingValues = PaddingValues(bottom = 300.dp),
        )
    }
}

// endregion

// region Add mode

@DefaultPreviews()
@Composable
fun EventDetailsAddModePreview() {
    MyAppTheme {
        EventDetailsContent(
            event = null,
            isLoading = false,
            isSaving = false,
            isAddMode = true,
            hasChanges = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            onTrackChanges = { /* Preview - no action */ },
            paddingValues = PaddingValues(),
        )
    }
}

@DefaultPreviews()
@Composable
fun EventDetailsAddModeSavingPreview() {
    MyAppTheme {
        EventDetailsContent(
            event = null,
            isLoading = false,
            isSaving = true,
            isAddMode = true,
            hasChanges = false,
            onUpdateEvent = { /* Preview - no action */ },
            onDeleteEvent = { /* Preview - no action */ },
            onTrackChanges = { /* Preview - no action */ },
            paddingValues = PaddingValues(),
        )
    }
}

// end region
