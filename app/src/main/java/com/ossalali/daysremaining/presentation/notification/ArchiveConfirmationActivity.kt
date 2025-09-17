package com.ossalali.daysremaining.presentation.notification

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.infrastructure.EventRepository
import com.ossalali.daysremaining.settings.di.WorkerEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArchiveConfirmationActivity : ComponentActivity() {

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
        fun newIntent(context: Context, eventId: Int): Intent {
            return Intent(context, ArchiveConfirmationActivity::class.java).apply {
                putExtra(EXTRA_EVENT_ID, eventId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val eventId = intent?.getIntExtra(EXTRA_EVENT_ID, -1) ?: -1
        setContent { MyAppTheme { ConfirmArchiveDialog(eventId = eventId, activity = this) } }
    }
}

@Composable
private fun ConfirmArchiveDialog(eventId: Int, activity: Activity) {
    val open = remember { mutableStateOf(true) }
    if (!open.value) return

    AlertDialog(
        onDismissRequest = {
            open.value = false
            activity.finish()
        },
        title = { Text("Archive Event") },
        text = { Text("Archiving will cancel scheduled notifications. Proceed?") },
        confirmButton = {
            TextButton(onClick = {
                open.value = false
                CoroutineScope(Dispatchers.IO).launch {
                    val entry =
                        EntryPointAccessors.fromApplication(activity, WorkerEntryPoint::class.java)
                    val repo: EventRepository = entry.eventRepo()
                    repo.archiveEvents(listOf(eventId))
                    // Cancel WorkManager unique works for this event's triggers by prefix pruning is not direct; callers schedule per trigger id
                }
                activity.finish()
            }) { Text("Archive", style = MaterialTheme.typography.labelLarge) }
        },
        dismissButton = {
            TextButton(onClick = {
                open.value = false
                activity.finish()
            }) { Text("Cancel", style = MaterialTheme.typography.labelLarge) }
        },
        properties = DialogProperties(dismissOnClickOutside = true, usePlatformDefaultWidth = true),
    )
}


