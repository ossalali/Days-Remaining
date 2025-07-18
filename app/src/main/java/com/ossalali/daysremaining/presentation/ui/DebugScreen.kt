package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.businesslogic.debug.AddDebugEventsUseCase
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.DebugScreenViewModel

@Composable
fun DebugScreen(
  debugScreenViewModel: DebugScreenViewModel = hiltViewModel(),
  paddingValues: PaddingValues = PaddingValues(),
  onClose: () -> Unit,
) {
    var showSnackBar by remember { mutableStateOf(false) }
    var numberOfEvents by rememberSaveable { mutableIntStateOf(0) }
    var textFieldValue by rememberSaveable { mutableStateOf("") }
    val textFieldFocusRequester = remember { FocusRequester() }

    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        Column {
            Row(
              modifier = Modifier.padding(Dimensions.default),
              horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                OutlinedTextField(
                  value = textFieldValue,
                  onValueChange = {
                      textFieldValue = it
                      numberOfEvents = it.toIntOrNull() ?: 0
                  },
                  label = { Text("Add Events") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  modifier = Modifier.focusRequester(textFieldFocusRequester),
                )
                Button(
                  modifier = Modifier.padding(Dimensions.half),
                  onClick = {
                      AddDebugEventsUseCase(debugScreenViewModel::insertEvents)(numberOfEvents)
                      showSnackBar = true
                      onClose()
                  },
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Events")
                }
            }
            Spacer(Modifier.weight(1f))
            if (showSnackBar) {
                Snackbar(modifier = Modifier.imePadding()) { Text("$numberOfEvents events added") }
            }
        }
    }
}
