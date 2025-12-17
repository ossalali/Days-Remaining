package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.businesslogic.debug.AddDebugEventsUseCase
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.navigation.DebugRoute
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.DebugScreenViewModel

fun EntryProviderScope<NavKey>.debugScreen(
    backStack: NavBackStack<NavKey>
) {
    entry<DebugRoute> {
        val debugScreenViewModel =
            viewModel<DebugScreenViewModel>(LocalViewModelStoreOwner.current!!)

        DebugScreen(
            debugScreenViewModel::insertEvents,
            onClose = { backStack.removeLastOrNull() },
        )
    }
}

@Composable
fun DebugScreen(
    insertEvents: (List<EventItem>) -> Unit = {},
    onClose: () -> Unit = {},
) {
    var numberOfEvents by rememberSaveable { mutableIntStateOf(0) }
    var textFieldValue by rememberSaveable { mutableStateOf("") }
    val textFieldFocusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    numberOfEvents = it.toIntOrNull() ?: 0
                },
                label = { Text("Add Events") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimensions.default)
                        .focusRequester(textFieldFocusRequester),
            )
            Spacer(Modifier.height(Dimensions.default))
            FloatingActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.default),
                onClick = {
                    AddDebugEventsUseCase(insertEvents)(numberOfEvents)
                    onClose()
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_24px),
                    contentDescription = "Add Events"
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun DebugScreenPreview() {
    MyAppTheme {
        Surface {
            DebugScreen()
        }
    }
}
