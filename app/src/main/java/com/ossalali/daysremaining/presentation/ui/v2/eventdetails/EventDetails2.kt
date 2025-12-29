package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.navigation.EventDetailsRoute
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.ui.v2.model.EventUiModel
import com.ossalali.daysremaining.presentation.ui.v2.viewmodel.EventDetailsViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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
                    onDateChanged = { date: LocalDate -> eventDetailsViewModel.updateDate(date) },
                    onSave = {},
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
    onSave: (EventUiModel) -> Unit = {},
    onDateChanged: (LocalDate) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val numberOfDays =
        remember(eventUiModel.date) {
            if (eventUiModel.date.isNotBlank()) {
                LocalDate.now().until(LocalDate.parse(eventUiModel.date), ChronoUnit.DAYS)
            } else {
                0
            }
        }
    var title by remember { mutableStateOf(eventUiModel.title) }
    var description by remember { mutableStateOf(eventUiModel.description) }
    val chipText =
        remember(eventUiModel.date) { eventUiModel.date.ifBlank { LocalDate.now().toString() } }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(Dimensions.default)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        EventDetailsNumberOfDays(numberOfDays.toString())
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
            chipText = chipText,
            onDateChanged = onDateChanged,
        )
        // Event Description
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = { description = it },
            label = { Text(text = stringResource(R.string.description)) },
            placeholder = { Text(text = stringResource(R.string.add_event_details_optional)) },
        )

        // Image
        // reminders
        // bottom bar

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
                        imageUri =
                            "C:\\Users\\ossma\\Projects\\Days-Remaining\\app\\src\\main\\icon_1-playstore.png",
                    ),
                onDateChanged = { date -> inputDate = date.toString() },
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
            EventDetailsLoaded(
                eventUiModel = EventUiModel(date = inputDate),
                onDateChanged = { date -> inputDate = date.toString() },
            )
        }
    }
}
