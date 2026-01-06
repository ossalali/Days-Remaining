package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.infrastructure.ImageStorage
import com.ossalali.daysremaining.model.Reminder
import com.ossalali.daysremaining.navigation.EventDetailsRoute
import com.ossalali.daysremaining.navigation.ReminderRoute
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize
import com.ossalali.daysremaining.presentation.ui.v2.model.EventUiModel
import com.ossalali.daysremaining.presentation.ui.v2.model.toNumberOfDays
import com.ossalali.daysremaining.presentation.ui.v2.viewmodel.EventDetailsViewModel
import com.ossalali.daysremaining.presentation.ui.v2.viewmodel.ReminderViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalDate
import java.time.LocalDateTime

fun EntryProviderScope<NavKey>.eventDetailsScreen(backStack: NavBackStack<NavKey>) {
    entry<EventDetailsRoute> { route ->
        val eventDetailsViewModel = hiltViewModel<EventDetailsViewModel>()
        val reminderViewModel = hiltViewModel<ReminderViewModel>()

        LaunchedEffect(route.eventId, route.eventUiModel) {
            if (route.eventId != null) {
                eventDetailsViewModel.init(route.eventId)
                reminderViewModel.load(route.eventId)
            } else if (route.eventUiModel != null) {
                val insertedId = eventDetailsViewModel.init(eventUiModel = route.eventUiModel)
                reminderViewModel.load(insertedId)
            }
        }

        val eventState by eventDetailsViewModel.state.collectAsStateWithLifecycle()

        when (eventState) {
            is EventDetailsViewModel.DetailsState.Loaded -> {
                val reminderState by reminderViewModel.state.collectAsStateWithLifecycle()
                EventDetailsLoaded(
                    eventUiModel = (eventState as EventDetailsViewModel.DetailsState.Loaded).event,
                    onSaveClick = { uiModel, reminders ->
                        eventDetailsViewModel.updateEvent(uiModel)
                        reminderViewModel.replaceReminders(
                            reminders = reminders,
                            eventId = uiModel.id,
                        )
                        backStack.removeLastOrNull()
                    },
                    onDeleteClick = { eventId ->
                        eventDetailsViewModel.deleteEvent(eventId)
                        backStack.removeLastOrNull()
                    },
                    eventReminders = reminderState.reminders,
                    onArchiveClick = { eventId ->
                        eventDetailsViewModel.archiveEvent(eventId)
                        backStack.removeLastOrNull()
                    },
                    onUnarchiveClick = { eventId ->
                        eventDetailsViewModel.unarchiveEvent(eventId)
                        backStack.removeLastOrNull()
                    },
                    onReminderChipClick = { eventId ->
                        val hasNone = backStack.none { route -> route is ReminderRoute }
                        if (hasNone) {
                            backStack.add(ReminderRoute(eventId))
                        }
                    },
                )
            }

            is EventDetailsViewModel.DetailsState.Loading -> {
                EventDetailsLoading()
            }

            is EventDetailsViewModel.DetailsState.Error -> {
                EventDetailsError()
            }
        }
    }
}

@Composable
fun EventDetailsLoaded(
    eventUiModel: EventUiModel = EventUiModel(),
    onSaveClick: (EventUiModel, ImmutableList<Reminder>) -> Unit = { _, _ -> },
    onDeleteClick: (Int) -> Unit = {},
    onArchiveClick: (Int) -> Unit = {},
    onUnarchiveClick: (Int) -> Unit = {},
    onReminderChipClick: (Int) -> Unit = {},
    eventReminders: ImmutableList<Reminder> = persistentListOf(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var title by remember { mutableStateOf(eventUiModel.title) }
    var description by remember { mutableStateOf(eventUiModel.description) }
    var selectedDate by remember {
        mutableStateOf(eventUiModel.date.ifBlank { LocalDate.now().toString() })
    }
    val reminders by
    remember(eventReminders) { mutableStateOf(eventReminders.toMutableStateList()) }
    var imageUri by remember { mutableStateOf(eventUiModel.imageUri) }
    val isArchived by remember { mutableStateOf(eventUiModel.isArchived) }

    val hasChanges by remember {
        derivedStateOf {
            title != eventUiModel.title ||
                    description != eventUiModel.description ||
                    selectedDate != eventUiModel.date.ifBlank { LocalDate.now().toString() } ||
                    imageUri != eventUiModel.imageUri ||
                    reminders.toList() != eventReminders.toList()
        }
    }

    var showFullScreenImage by remember { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var showConfirmImageDeleteDialog by remember { mutableStateOf(false) }
    var showEventDeletionDialog by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    val photoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION,
                    )
                } catch (_: Exception) {
                }

                val stored = ImageStorage.persistImageFromUri(context, uri)
                imageUri = (stored ?: uri).toString()
            }
        }

    var cameraTempUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean
            ->
            if (success) {
                imageUri = cameraTempUri?.toString()
            }
        }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(PaddingSize.default)) {
        val scrollState = rememberScrollState()
        Column(
            modifier =
                Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    }
                    .verticalScroll(scrollState)
                    .padding(bottom = PaddingSize.fabButton),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            EventDetailsNumberOfDays(
                modifier = Modifier.padding(bottom = PaddingSize.half),
                numberOfDays = selectedDate.toNumberOfDays(),
            )
            if (isArchived) {
                Text(
                    modifier =
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = PaddingSize.half)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape,
                            )
                            .padding(horizontal = PaddingSize.half, vertical = PaddingSize.quarter),
                    text = "ARCHIVED",
                    textAlign = TextAlign.Center,
                )
            }

            // Event Title
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                readOnly = isArchived,
                value = title,
                onValueChange = { title = it },
                label = { Text(text = stringResource(R.string.event_title)) },
                placeholder = { Text(text = stringResource(R.string.enter_event_title)) },
            )
            // Event Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DatePickerChip(
                    readOnly = isArchived,
                    clearFocus = focusManager::clearFocus,
                    chipText = selectedDate,
                    onDateChanged = { selectedDate = it.toString() },
                )
                ReminderChip(
                    readOnly = isArchived,
                    onSave = { updatedReminders ->
                        reminders.clear()
                        reminders.addAll(updatedReminders)
                    },
                    reminders = reminders.toImmutableList(),
                    currentEventItemId = eventUiModel.id,
                    onReminderChipClick = onReminderChipClick,
                )
            }
            // Event Description
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                readOnly = isArchived,
                value = description,
                onValueChange = { description = it },
                label = { Text(text = stringResource(R.string.description)) },
                placeholder = { Text(text = stringResource(R.string.add_event_details_optional)) },
            )

            ImagePicker(
                readOnly = isArchived,
                imageUri = imageUri,
                showFullScreenImage = { showFullScreenImage = it },
                showImagePickerDialog = { showImagePickerDialog = it },
                showConfirmImageDeleteDialog = { showConfirmImageDeleteDialog = it },
            )

            if (showImagePickerDialog) {
                ImagePickerDialog(
                    showImagePickerDialog = { showImagePickerDialog = it },
                    imageChosen = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    openCamera = {
                        val imageFile = ImageStorage.createImageFileInAppStorage(context)
                        val uri =
                            FileProvider.getUriForFile(
                                context,
                                context.packageName + ".fileprovider",
                                imageFile,
                            )
                        cameraTempUri = uri
                        cameraLauncher.launch(uri)
                    },
                )
            }

            if (showFullScreenImage && !imageUri.isNullOrBlank()) {
                FullScreenImage(
                    showFullScreenImage = { showFullScreenImage = it },
                    imageUri = imageUri,
                )
            }

            if (showConfirmImageDeleteDialog) {
                ImageDeletionDialog(
                    showConfirmImageDeleteDialog = { showConfirmImageDeleteDialog = it },
                    removeImage = { imageUri = null },
                )
            }

            if (isArchived) {
                OutlinedButton(onClick = { onUnarchiveClick(eventUiModel.id) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.padding(end = PaddingSize.quarter),
                            painter = painterResource(R.drawable.unarchive_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(text = "Unarchive event", color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else {
                OutlinedButton(onClick = { onArchiveClick(eventUiModel.id) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.padding(end = PaddingSize.quarter),
                            painter = painterResource(R.drawable.archive_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(text = "Archive event", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            if (showReminderDialog) {
                ReminderDateTimePicker(
                    onSave = { dateTime ->
                        reminders.add(Reminder(eventItemId = eventUiModel.id, dateTime = dateTime))
                        showReminderDialog = false
                    },
                    onDismiss = { showReminderDialog = false },
                )
            }
        }
        EventDetailsBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            hasChanges = hasChanges,
            isSaving = isSaving,
            isDeleting = isDeleting,
            isArchived = eventUiModel.isArchived,
            leftButtonDrawable = R.drawable.delete_24px,
            leftButtonContentDescription = "delete",
            rightButtonDrawable = R.drawable.check_24px,
            rightButtonContentDescription = "save",
            onSaveClick = {
                isSaving = true
                onSaveClick(
                    eventUiModel.copy(
                        title = title,
                        description = description,
                        date = selectedDate,
                        imageUri = imageUri,
                    ),
                    reminders.toImmutableList(),
                )
            },
            onDeleteClick = { showEventDeletionDialog = true },
        )
        if (showEventDeletionDialog) {
            EventDeletionDialog(
                onDeleteConfirm = {
                    isDeleting = true
                    onDeleteClick(eventUiModel.id)
                    showEventDeletionDialog = false
                },
                showEventDeletionDialog = { showEventDeletionDialog = it },
            )
        }
    }
}

@PreviewLightDark
@Composable
fun EventDetailsLoadedPreview() {
    MyAppTheme {
        Surface {
            var inputDate by remember { mutableStateOf(LocalDate.now().plusDays(5).toString()) }
            EventDetailsLoaded(
                eventUiModel =
                    EventUiModel(
                        title = "Birthday",
                        description =
                            """
                            This is the description of the birthday event 
                            with a new line
                            !!!
                            """
                                .trimIndent(),
                        date = inputDate,
                    ),
                eventReminders =
                    persistentListOf(
                        Reminder(id = 1, eventItemId = 1, dateTime = LocalDateTime.now())
                    ),
            )
        }
    }
}

@PreviewLightDark
@Composable
fun EventDetailsLoadedArchivedPreview() {
    MyAppTheme {
        Surface {
            var inputDate by remember { mutableStateOf(LocalDate.now().plusDays(5).toString()) }
            EventDetailsLoaded(
                eventUiModel =
                    EventUiModel(
                        title = "Birthday",
                        description =
                            """
                            This is the description of the birthday event 
                            with a new line
                            !!!
                            """
                                .trimIndent(),
                        date = inputDate,
                        isArchived = true,
                    )
            )
        }
    }
}

@PreviewLightDark
@Composable
fun EventDetailsLoadedEmptyPreview() {
    MyAppTheme {
        Surface {
            var inputDate by remember { mutableStateOf(LocalDate.now().toString()) }
            EventDetailsLoaded(eventUiModel = EventUiModel(date = inputDate))
        }
    }
}
