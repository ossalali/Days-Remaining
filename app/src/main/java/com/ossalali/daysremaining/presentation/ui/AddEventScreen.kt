package com.ossalali.daysremaining.presentation.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.ui.theme.DefaultPreviews
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.AddEventViewmodel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
  viewModel: AddEventViewmodel = hiltViewModel(),
  onClose: () -> Unit,
  paddingValues: PaddingValues,
) {
    val isSaving by viewModel.isSaving.collectAsState()
    AddEventScreenContent(
      isSaving = isSaving,
      onAddEvent = { event -> viewModel.addEvent(event) },
      onClose = onClose,
      paddingValues = paddingValues,
    )
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreenContent(
  isSaving: Boolean,
  onAddEvent: (EventItem) -> Unit,
  onClose: () -> Unit,
  paddingValues: PaddingValues,
) {
    val titleFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var selectedDateMillis by rememberSaveable {
        mutableLongStateOf(LocalDate.now().toEpochDay() * 24 * 60 * 60 * 1000)
    }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showDate by rememberSaveable { mutableStateOf(false) }
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var titleError by rememberSaveable { mutableStateOf(false) }
    var dateError by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
    val selectedLocalDate =
      Instant.ofEpochMilli(selectedDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDate = selectedLocalDate.format(dateFormatter)

    LaunchedEffect(Unit) {
        titleFocusRequester.requestFocus()
        keyboardController?.show()
    }

    val configuration = LocalConfiguration.current
    val isLandScape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenHorizontalPadding =
      if (isLandScape) {
          configuration.screenWidthDp.dp / 4
      } else {
          Dimensions.default
      }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        Column(
          modifier =
            Modifier.fillMaxSize()
              .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
              )
              .padding(horizontal = screenHorizontalPadding)
        ) {
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
            OutlinedTextField(
              value = title,
              onValueChange = {
                  title = it
                  titleError = false
              },
              label = { Text("Title") },
              modifier =
                Modifier.fillMaxWidth().focusRequester(focusRequester = titleFocusRequester),
              isError = titleError,
              supportingText = { if (titleError) Text("Title cannot be empty") },
            )
            Spacer(modifier = Modifier.height(Dimensions.half))

            val fontScale = LocalDensity.current.fontScale
            InputChip(
              modifier =
                Modifier.height(Dimensions.triple * fontScale)
                  .width(Dimensions.nonuple * fontScale),
              selected = showDate,
              onClick = { showDatePicker = true },
              label = {
                  if (showDate) {
                      Text(formattedDate)
                  } else {
                      Text("Add Date")
                  }
              },
              leadingIcon = {
                  Icon(
                    modifier = Modifier.offset(y = (-2).dp),
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = "Add Date to event",
                  )
              },
            )
            Spacer(modifier = Modifier.height(Dimensions.half))

            OutlinedTextField(
              value = description,
              onValueChange = { description = it },
              label = { Text("Description") },
              modifier = Modifier.fillMaxWidth(),
            )

            if (showDatePicker) {
                DatePickerDialog(
                  onDismissRequest = { showDatePicker = false },
                  confirmButton = {
                      TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                selectedDateMillis = millis
                            }
                            showDatePicker = false
                            showDate = true
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
        FloatingActionButton(
          modifier = Modifier.imePadding().padding(Dimensions.default),
          onClick = {
              titleError = title.isBlank()
              dateError = selectedDateMillis == 0L
              if (!titleError && !dateError) {
                  onAddEvent(
                    EventItem(title = title, date = selectedLocalDate, description = description)
                  )
                  onClose()
              }
          },
        ) {
            if (isSaving) {
                CircularProgressIndicator()
            } else {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Save Event")
            }
        }
    }
}

@DefaultPreviews
@Composable
fun AddEventScreenPreview() {
    AddEventScreenContent(
      isSaving = false,
      onAddEvent = {},
      onClose = {},
      paddingValues = PaddingValues(Dimensions.default),
    )
}
