package com.ossalali.daysremaining.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.ossalali.daysremaining.model.EventNotificationTrigger
import com.ossalali.daysremaining.model.NotificationTriggerType
import com.ossalali.daysremaining.model.RelativeUnit
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions

/** Card displaying a single reminder trigger. */
@Composable
fun ReminderCard(
    modifier: Modifier = Modifier,
    trigger: EventNotificationTrigger,
    onRemove: () -> Unit,
) {
  Card(
      modifier = modifier.fillMaxWidth(),
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant,
          ),
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(Dimensions.default),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Reminder",
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(Dimensions.default))
        Column {
          Text(
              text = formatTriggerDescription(trigger),
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Medium,
          )
        }
      }
      IconButton(onClick = onRemove) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remove reminder",
            tint = MaterialTheme.colorScheme.error,
        )
      }
    }
  }
}

/** Dialog for adding a new reminder. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onAddRelative: (RelativeUnit, Int) -> Unit,
    onAddCompletion: () -> Unit,
) {
  var selectedType by remember { mutableStateOf(NotificationTriggerType.RELATIVE) }
  var selectedUnit by remember { mutableStateOf(RelativeUnit.DAYS) }
  var step by remember { mutableStateOf("") }
  var showUnitDropdown by remember { mutableStateOf(false) }

  AlertDialog(
      onDismissRequest = onDismiss,
      title = { Text("Add Reminder") },
      text = {
        Column {
          Text(
              text = "When would you like to be reminded?",
              style = MaterialTheme.typography.bodyMedium,
          )
          Spacer(modifier = Modifier.height(Dimensions.default))

          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(Dimensions.half),
          ) {
            TextButton(
                onClick = { selectedType = NotificationTriggerType.RELATIVE },
                modifier = Modifier.weight(1f),
            ) {
              Text(
                  text = "Before Event",
                  color =
                      if (selectedType == NotificationTriggerType.RELATIVE) {
                        MaterialTheme.colorScheme.primary
                      } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                      },
                  fontWeight =
                      if (selectedType == NotificationTriggerType.RELATIVE) {
                        FontWeight.Bold
                      } else {
                        FontWeight.Normal
                      },
              )
            }
            TextButton(
                onClick = { selectedType = NotificationTriggerType.ON_COMPLETION },
                modifier = Modifier.weight(1f),
            ) {
              Text(
                  text = "On Event Day",
                  color =
                      if (selectedType == NotificationTriggerType.ON_COMPLETION) {
                        MaterialTheme.colorScheme.primary
                      } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                      },
                  fontWeight =
                      if (selectedType == NotificationTriggerType.ON_COMPLETION) {
                        FontWeight.Bold
                      } else {
                        FontWeight.Normal
                      },
              )
            }
          }

          Spacer(modifier = Modifier.height(Dimensions.default))

          if (selectedType == NotificationTriggerType.RELATIVE) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.default),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              OutlinedTextField(
                  value = step,
                  onValueChange = { newValue ->
                    step = newValue.filter { it.isDigit() && it.digitToInt() != 0 }
                  },
                  label = { Text("Number") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  modifier = Modifier.weight(1f),
                  singleLine = true,
              )

              ExposedDropdownMenuBox(
                  expanded = showUnitDropdown,
                  onExpandedChange = { showUnitDropdown = it },
                  modifier = Modifier.weight(1f),
              ) {
                OutlinedTextField(
                    value = formatUnit(selectedUnit, (step.toIntOrNull() ?: 0) > 1),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unit") },
                    trailingIcon = {
                      ExposedDropdownMenuDefaults.TrailingIcon(expanded = showUnitDropdown)
                    },
                    modifier =
                        Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = showUnitDropdown,
                    onDismissRequest = { showUnitDropdown = false },
                ) {
                  RelativeUnit.entries.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(formatUnit(unit, (step.toIntOrNull() ?: 0) > 1)) },
                        onClick = {
                          selectedUnit = unit
                          showUnitDropdown = false
                        },
                    )
                  }
                }
              }
            }
          } else {
            Text(
                text = "You'll be reminded on the event day",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
          }
        }
      },
      confirmButton = {
        TextButton(
            enabled =
                selectedType == NotificationTriggerType.ON_COMPLETION ||
                    (step.toIntOrNull() != null && step.isNotBlank() && step.toInt() > 0),
            onClick = {
              when (selectedType) {
                NotificationTriggerType.RELATIVE ->
                    onAddRelative(selectedUnit, step.toIntOrNull() ?: 1)

                NotificationTriggerType.ON_COMPLETION -> onAddCompletion()
              }
              onDismiss()
            }) {
              Text("Add")
            }
      },
      dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
  )
}

/** Format a trigger into a human-readable description. */
private fun formatTriggerDescription(trigger: EventNotificationTrigger): String {
  return when (trigger.type) {
    NotificationTriggerType.RELATIVE -> {
      val step = trigger.step ?: 1
      val unit = trigger.unit ?: RelativeUnit.DAYS
      "$step ${formatUnit(unit, step > 1)} before"
    }

    NotificationTriggerType.ON_COMPLETION -> "On event day"
  }
}

/** Format a RelativeUnit to a readable string. */
private fun formatUnit(unit: RelativeUnit, plural: Boolean): String {
  val base =
      when (unit) {
        RelativeUnit.DAYS -> "day"
        RelativeUnit.WEEKS -> "week"
        RelativeUnit.MONTHS -> "month"
      }
  return if (plural) "${base}s" else base
}
