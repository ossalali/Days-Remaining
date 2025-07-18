package com.ossalali.daysremaining.presentation.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.BuildConfig
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.EventDetailsViewModel
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
  viewModel: EventDetailsViewModel = hiltViewModel(),
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
      onBackClick = onBackClick,
      onUpdateEvent = { updatedEvent ->
          viewModel.saveEvent(updatedEvent)
          onBackClick()
      },
      onDeleteEvent = { eventToDelete ->
          viewModel.deleteEvent(eventToDelete.id)
          onBackClick()
      },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsContent(
  event: EventItem?,
  isLoading: Boolean,
  isSaving: Boolean,
  onBackClick: () -> Unit,
  onUpdateEvent: (EventItem) -> Unit,
  onDeleteEvent: (EventItem) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

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

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Scaffold(
          topBar = {
              TopAppBar(
                title = { Text(text = event?.title ?: "Event Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                          contentDescription = "Back",
                        )
                    }
                },
              )
          },
          snackbarHost = { SnackbarHost(snackbarHostState) },
          floatingActionButtonPosition = FabPosition.Center,
          floatingActionButton = {
              if (event != null) {
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = Dimensions.default),
                    horizontalArrangement = Arrangement.SpaceBetween,
                  ) {
                      DeleteEventFab(onDelete = { onDeleteEvent(event) })

                      SaveEventFab(
                        event = event,
                        titleState = titleState,
                        selectedDateMillis = selectedDateMillis,
                        descriptionState = descriptionState,
                        originalTitle = originalTitle,
                        originalDateMillis = originalDateMillis,
                        originalDescription = originalDescription,
                        isSaving = isSaving,
                        onSave = onUpdateEvent,
                      )
                  }
              }
          },
        ) { innerPadding ->
            Box(
              modifier =
                Modifier.fillMaxSize()
                  .padding(innerPadding)
                  .padding(horizontal = Dimensions.default),
              contentAlignment = Alignment.TopCenter,
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (event != null) {
                    EventContent(
                      event = event,
                      titleState = titleState,
                      selectedDateMillis = selectedDateMillis,
                      onDateChanged = { selectedDateMillis = it },
                      descriptionState = descriptionState,
                    )
                } else {
                    Text(text = "Event not found", style = MaterialTheme.typography.bodyLarge)
                }
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
      remember(titleState, descriptionState) {
          derivedStateOf {
              val titleChanged = titleState.text.toString().trim() != originalTitle
              val dateChanged = selectedDateMillisState.value != originalDateMillis
              val descriptionChanged =
                descriptionState.text.toString().trim() != originalDescription

              if (BuildConfig.DEBUG) {
                  Log.d(
                    "CHANGE",
                    "Change detection - Title: $titleChanged, Date: $dateChanged, Description: $descriptionChanged",
                  )
                  Log.d(
                    "CHANGE",
                    "Selected date millis: ${selectedDateMillisState.value}, Original date millis: $originalDateMillis",
                  )
              }

              titleChanged || dateChanged || descriptionChanged
          }
      }

    val canSave by remember { derivedStateOf { isTitleValid && hasChanges && !isSaving } }

    FloatingActionButton(
      modifier = Modifier.imePadding(),
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
            Icon(imageVector = Icons.Default.Save, contentDescription = "Save Event")
        }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EventContent(
  event: EventItem,
  titleState: TextFieldState,
  selectedDateMillis: Long,
  onDateChanged: (Long) -> Unit,
  descriptionState: TextFieldState,
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)

    LaunchedEffect(selectedDateMillis) { datePickerState.selectedDateMillis = selectedDateMillis }

    val selectedLocalDate =
      Instant.ofEpochMilli(selectedDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDate = selectedLocalDate.format(dateFormatter)

    val titleError by remember { derivedStateOf { titleState.text.isBlank() } }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    Column {
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

        Spacer(modifier = Modifier.height(Dimensions.default))
        if (BuildConfig.DEBUG) {
            Text(
              text = "Id: ${event.id}",
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(bottom = Dimensions.half),
            )
        }

        OutlinedTextField(
          state = titleState,
          modifier = Modifier.fillMaxWidth(),
          label = { Text(text = "Title") },
          lineLimits = TextFieldLineLimits.SingleLine,
          isError = titleError,
          supportingText = {
              if (titleError) {
                  Text(text = "Title cannot be empty", color = MaterialTheme.colorScheme.error)
              }
          },
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        InputChip(
          modifier = Modifier.height(Dimensions.triple).width(Dimensions.nonuple),
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
        Spacer(modifier = Modifier.height(Dimensions.default))

        OutlinedTextField(
          state = descriptionState,
          modifier = Modifier.fillMaxWidth(),
          lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 1, maxHeightInLines = 5),
          label = { Text(text = "Description") },
        )

        if (showDatePicker) {
            DatePickerDialog(
              onDismissRequest = { showDatePicker = false },
              confirmButton = {
                  TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis -> onDateChanged(millis) }
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

@Preview(showBackground = true)
@Composable
fun EventDetailsContentPreview() {
    val sampleEvent =
      EventItem(
        id = 1,
        title = "Sample Event Title",
        date = LocalDate.now().plusDays(10),
        description =
          "This is a sample event description that shows how the UI looks with longer text content.",
      )

    MyAppTheme {
        EventDetailsContent(
          event = sampleEvent,
          isLoading = false,
          isSaving = false,
          onBackClick = { /* Preview - no action */ },
          onUpdateEvent = { /* Preview - no action */ },
          onDeleteEvent = { /* Preview - no action */ },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EventDetailsContentLoadingPreview() {
    MyAppTheme {
        EventDetailsContent(
          event = null,
          isLoading = true,
          isSaving = false,
          onBackClick = { /* Preview - no action */ },
          onUpdateEvent = { /* Preview - no action */ },
          onDeleteEvent = { /* Preview - no action */ },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EventDetailsContentSavingPreview() {
    val sampleEvent =
      EventItem(
        id = 1,
        title = "Event Being Saved",
        date = LocalDate.now().plusDays(5),
        description = "This event is currently being saved.",
      )

    MyAppTheme {
        EventDetailsContent(
          event = sampleEvent,
          isLoading = false,
          isSaving = true,
          onBackClick = { /* Preview - no action */ },
          onUpdateEvent = { /* Preview - no action */ },
          onDeleteEvent = { /* Preview - no action */ },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EventDetailsContentNotFoundPreview() {
    MyAppTheme {
        EventDetailsContent(
          event = null,
          isLoading = false,
          isSaving = false,
          onBackClick = { /* Preview - no action */ },
          onUpdateEvent = { /* Preview - no action */ },
          onDeleteEvent = { /* Preview - no action */ },
        )
    }
}

@Preview()
@Composable
fun EventDetailsPreview() {
    val sampleEvent =
      EventItem(
        id = 1,
        title = "Sample Event Title",
        date = LocalDate.now().plusDays(10),
        description = "This is a sample event description.",
      )

    MyAppTheme {
        EventDetailsScreen(event = sampleEvent, onBackClick = { /* Preview - no action */ })
    }
}
