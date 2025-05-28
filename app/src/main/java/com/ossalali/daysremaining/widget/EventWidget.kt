package com.ossalali.daysremaining.widget

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.widget.di.WidgetRepositoryEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

class EventWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetRepositoryEntryPoint::class.java
        )
        val eventRepo = widgetEntryPoint.eventRepo()
        val widgetDataStore = widgetEntryPoint.widgetDataStore() // Get WidgetDataStore

        // Get the current appWidgetId from the GlanceId more safely
        val appWidgetIdFromGlance = (id as? AppWidgetId)?.appWidgetId
        if (appWidgetIdFromGlance == null) {
            android.util.Log.e("EventWidget", "GlanceId $id is not an AppWidgetId or could not be resolved. Cannot provide glance.")
            // Optionally provide an error UI if necessary, e.g.:
            // provideContent { ErrorUi("Widget ID Error") }
            return
        }
        val currentAppWidgetId = appWidgetIdFromGlance
        android.util.Log.d("EventWidget", "Providing glance for appWidgetId: $currentAppWidgetId (GlanceId: $id)")

        // Get selected event IDs from DataStore
        val selectedEventIds = widgetDataStore.getSelectedEventIds(currentAppWidgetId).first()
        android.util.Log.d("EventWidget", "Selected event IDs for widget $currentAppWidgetId: $selectedEventIds")
        
        val events = if (selectedEventIds.isNotEmpty()) {
            val retrievedEvents = eventRepo.getEventsByIds(selectedEventIds)
            android.util.Log.d("EventWidget", "Retrieved ${retrievedEvents.size} events for widget $currentAppWidgetId")
            retrievedEvents.forEach { event ->
                android.util.Log.d("EventWidget", "Event: ${event.title} (ID: ${event.id})")
            }
            retrievedEvents
        } else {
            android.util.Log.d("EventWidget", "No events selected for widget $currentAppWidgetId")
            emptyList()
        }

        provideContent {
            val colorProvider = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                GlanceTheme.colors
            } else {
                WidgetColorScheme.colors
            }
            GlanceTheme(colors = colorProvider) {
                WidgetContent(events) // Pass the list of events
            }
        }
    }
}

@Composable
fun WidgetContent(eventItems: List<EventItem>) { // Changed parameter
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(8.dp)
            .cornerRadius(16.dp)
            .background(GlanceTheme.colors.background)
    ) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(8.dp)
                .cornerRadius(8.dp)
                .background(GlanceTheme.colors.widgetBackground)
        ) {
            if (eventItems.isEmpty()) {
                Text(
                    text = "No events selected. Configure widget.", 
                    style = TextStyle(fontSize = 12.sp, color = GlanceTheme.colors.onSurface)
                )
            } else {
                Column { // Display items in a Column
                    eventItems.forEach { eventItem ->
                        // Display logic for each event item
                        Text(
                            text = eventItem.title,
                            style = TextStyle(fontSize = 12.sp, color = GlanceTheme.colors.onSurface),
                            maxLines = 1
                        )
                        Row {
                            Text(
                                text = eventItem.getNumberOfDays().toString(),
                                // Adjusted font size for multiple items
                                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GlanceTheme.colors.onSurface), 
                                maxLines = 1
                            )
                            Text(
                                text = "d",
                                style = TextStyle(fontSize = 10.sp, color = GlanceTheme.colors.onSurface),
                                modifier = GlanceModifier.padding(start = 2.dp), // Adjusted padding
                                maxLines = 1
                            )
                        }
                        // Simple visual separation if needed, can be a line or just padding
                        // For now, let's assume the natural spacing of Column is enough,
                        // or add a small padding at the bottom of the Row/Column for each item if necessary
                        // androidx.glance.layout.Spacer(modifier = GlanceModifier.height(4.dp)) // Uncomment if needed
                    }
                }
            }
        }
    }
}