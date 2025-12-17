package com.ossalali.daysremaining.presentation.ui.v2

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.navigation.EventDetailsRoute
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.EventDetailsViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun EntryProviderScope<NavKey>.eventDetailsScreen(backStack: NavBackStack<NavKey>) {
    entry<EventDetailsRoute> { route ->
        val eventDetailsViewModel =
            viewModel<EventDetailsViewModel>(LocalViewModelStoreOwner.current!!)

        LaunchedEffect(route.eventId) { eventDetailsViewModel.init(route.eventId, route.isAddMode) }

        val state = eventDetailsViewModel.detailsState.collectAsStateWithLifecycle()

        when (state.value) {
            EventDetailsViewModel.DetailsState.AddMode -> TODO()
            is EventDetailsViewModel.DetailsState.Loaded -> {
                EventDetails(
                    formData =
                        (state.value as EventDetailsViewModel.DetailsState.Loaded).eventUiModel,
                    onDateChanged = { date: LocalDate -> eventDetailsViewModel.updateDate(date) },
                    onSave = {},
                )
            }

            EventDetailsViewModel.DetailsState.Loading -> TODO()
            EventDetailsViewModel.DetailsState.Saving -> TODO()
        }
    }
}

@Composable
fun EventDetails(
    formData: EventUiModel = EventUiModel(),
    onSave: (EventUiModel) -> Unit = {},
    onDateChanged: (LocalDate) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val numberOfDays =
        remember(formData.date) {
            if (formData.date.isNotBlank()) {
                LocalDate.now().until(LocalDate.parse(formData.date), ChronoUnit.DAYS)
            } else {
                0
            }
        }
    var title by remember { mutableStateOf(formData.title) }
    var description by remember { mutableStateOf(formData.description) }
    val chipText = remember(formData.date) { formData.date.ifBlank { LocalDate.now().toString() } }

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
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = { title = it },
            label = { Text(text = stringResource(R.string.event_title)) },
            placeholder = { Text(text = stringResource(R.string.enter_event_title)) },
        )
        DatePickerChip(
            modifier = Modifier.align(Alignment.Start),
            clearFocus = focusManager::clearFocus,
            chipText = chipText,
            onDateChanged = onDateChanged,
        )
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
fun EventDetailsPreview() {
    MyAppTheme {
        Surface {
            var inputDate by remember { mutableStateOf(LocalDate.now().plusDays(5).toString()) }
            EventDetails(
                formData =
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
fun EventDetailsEmptyPreview() {
    MyAppTheme {
        Surface {
            var inputDate by remember { mutableStateOf(LocalDate.now().toString()) }
            EventDetails(
                formData = EventUiModel(date = inputDate),
                onDateChanged = { date -> inputDate = date.toString() },
            )
        }
    }
}
