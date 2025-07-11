package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.businesslogic.debug.AddDebugEventsUseCase
import com.ossalali.daysremaining.businesslogic.debug.DebugAddEvents
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.DebugScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun DebugScreen(
  debugScreenViewModel: DebugScreenViewModel = hiltViewModel(),
  addEvents: DebugAddEvents = DebugAddEvents(),
  paddingValues: PaddingValues = PaddingValues(),
) {
    val scope = rememberCoroutineScope()
    var showSnackbar by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        Column(modifier = Modifier.padding(Dimensions.default)) {
            Text("Add Events")
            Button(
              modifier = Modifier.padding(Dimensions.half),
              onClick = {
                  scope.launch {
                      addEvents(debugScreenViewModel, AddDebugEventsUseCase())
                      showSnackbar = true
                  }
              },
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Events")
            }

            if (showSnackbar) {
                Snackbar { Text("Events added") }
            }
        }
    }
}
