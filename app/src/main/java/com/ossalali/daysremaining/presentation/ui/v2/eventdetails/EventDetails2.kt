package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.infrastructure.ImageStorage
import com.ossalali.daysremaining.navigation.EventDetailsRoute
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize
import com.ossalali.daysremaining.presentation.ui.v2.model.EventUiModel
import com.ossalali.daysremaining.presentation.ui.v2.model.toNumberOfDays
import com.ossalali.daysremaining.presentation.ui.v2.viewmodel.EventDetailsViewModel
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalDate
import java.time.LocalDateTime

fun EntryProviderScope<NavKey>.eventDetailsScreen(backStack: NavBackStack<NavKey>) {
    entry<EventDetailsRoute> { route ->
        val eventDetailsViewModel =
            hiltViewModel<EventDetailsViewModel>(LocalViewModelStoreOwner.current!!)

        LaunchedEffect(route.eventId) { eventDetailsViewModel.init(route.eventId, route.isAddMode) }

        val state = eventDetailsViewModel.state.collectAsStateWithLifecycle()

        when (state.value) {
            EventDetailsViewModel.DetailsState.AddMode -> {}

            is EventDetailsViewModel.DetailsState.Loaded -> {
                EventDetailsLoaded(
                    eventUiModel = (state.value as EventDetailsViewModel.DetailsState.Loaded).event,
                    onSaveClick = { uiModel ->
                        eventDetailsViewModel.updateEvent(uiModel)
                        backStack.removeLastOrNull()
                    },
                    onDeleteClick = { eventId ->
                        eventDetailsViewModel.deleteEvent(eventId)
                        backStack.removeLastOrNull()
                    },
                )
            }

            EventDetailsViewModel.DetailsState.Loading -> {
                EventDetailsLoading()
            }

            EventDetailsViewModel.DetailsState.Saving -> {}

            EventDetailsViewModel.DetailsState.Error -> {}
        }
    }
}

@Composable
fun EventDetailsLoading() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun EventDetailsLoaded(
    eventUiModel: EventUiModel = EventUiModel(),
    onSaveClick: (EventUiModel) -> Unit = {},
    onDeleteClick: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var title by remember { mutableStateOf(eventUiModel.title) }
    var description by remember { mutableStateOf(eventUiModel.description) }
    var selectedDate by remember {
        mutableStateOf(eventUiModel.date.ifBlank { LocalDate.now().toString() })
    }
    val reminders = remember { mutableListOf<LocalDateTime>() }

    var imageUri by remember { mutableStateOf(eventUiModel.imageUri) }
    var showFullScreenImage by remember { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var showConfirmImageDeleteDialog by remember { mutableStateOf(false) }
    var showEventDeletionDialog by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }

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

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(PaddingSize.default)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        EventDetailsNumberOfDays(selectedDate.toNumberOfDays())
        // Event Title
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = { title = it },
            label = { Text(text = stringResource(R.string.event_title)) },
            placeholder = { Text(text = stringResource(R.string.enter_event_title)) },
        )
        // Event Date
        DatePickerChip(
            modifier = Modifier.align(Alignment.Start),
            clearFocus = focusManager::clearFocus,
            chipText = selectedDate,
            onDateChanged = { selectedDate = it.toString() },
        )
        // Event Description
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = { description = it },
            label = { Text(text = stringResource(R.string.description)) },
            placeholder = { Text(text = stringResource(R.string.add_event_details_optional)) },
        )

        ImagePicker(
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
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
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
            FullScreenImage(showFullScreenImage = { showFullScreenImage = it }, imageUri = imageUri)
        }

        if (showConfirmImageDeleteDialog) {
            ImageDeletionDialog(
                showConfirmImageDeleteDialog = { showConfirmImageDeleteDialog = it },
                removeImage = { imageUri = null },
            )
        }

        // reminders
        if (reminders.isNotEmpty()) {
            ReminderList(reminders = reminders.toImmutableList())
        }
        TextButton(onClick = { showReminderDialog = true }) {
            Icon(
                modifier = Modifier.padding(end = PaddingSize.quarter),
                painter = painterResource(R.drawable.notification_add_24px),
                contentDescription = null,
            )
            Text(text = "Add reminders")
        }
        if (showReminderDialog) {
            ReminderDateTimePicker(
                onSave = { dateTime ->
                    reminders.add(dateTime)
                    showReminderDialog = false
                },
                onDismiss = { showReminderDialog = false },
            )
            // ReminderDialog(
            //    onSave = {
            //        showReminderDialog = false
            //    },
            //    onDismiss = {
            //        showReminderDialog = false
            //    }
            // )
        }

        Spacer(modifier = Modifier.weight(1f))

        EventDetailsBottomBar(
            isSaving = false,
            leftButtonDrawable = R.drawable.delete_24px,
            leftButtonContentDescription = "delete",
            rightButtonDrawable = R.drawable.check_24px,
            rightButtonContentDescription = "save",
            onSaveClick = {
                onSaveClick(
                    eventUiModel.copy(
                        title = title,
                        description = description,
                        date = selectedDate,
                        imageUri = imageUri,
                    )
                )
            },
            onDeleteClick = { showEventDeletionDialog = true },
        )
        if (showEventDeletionDialog) {
            EventDeletionDialog(
                onDeleteConfirm = {
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
