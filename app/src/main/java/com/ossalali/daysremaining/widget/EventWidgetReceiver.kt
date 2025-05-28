package com.ossalali.daysremaining.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EventWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EventWidget()

    // Optional: Add a CoroutineScope for any background work if needed, though usually handled by Glance.
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d(
            "EventWidgetReceiver",
            "onUpdate received for appWidgetIds: ${appWidgetIds.joinToString()}"
        )

        // First call super to ensure the default update mechanism is triggered
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // Then explicitly trigger updates for each widget ID
        coroutineScope.launch {
            appWidgetIds.forEach { appWidgetId ->
                Log.d(
                    "EventWidgetReceiver",
                    "Explicitly triggering update for appWidgetId: $appWidgetId"
                )
                try {
                    val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
                    Log.d(
                        "EventWidgetReceiver",
                        "Got GlanceId $glanceId for appWidgetId $appWidgetId"
                    )

                    // Add a small delay to ensure DataStore operations are complete
                    delay(500)

                    // Force a fresh update
                    glanceAppWidget.update(context, glanceId)
                    Log.d(
                        "EventWidgetReceiver",
                        "Successfully triggered update for appWidgetId $appWidgetId"
                    )

                    // Double-check the update was applied
                    delay(100)
                    glanceAppWidget.update(context, glanceId)
                    Log.d(
                        "EventWidgetReceiver",
                        "Applied second update for appWidgetId $appWidgetId"
                    )
                } catch (e: Exception) {
                    Log.e(
                        "EventWidgetReceiver",
                        "Error updating widget $appWidgetId: ${e.message}",
                        e
                    )
                }
            }
        }
    }

    override fun onEnabled(context: Context) {
        Log.d("EventWidgetReceiver", "onEnabled called")
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        Log.d("EventWidgetReceiver", "onDisabled called")
        super.onDisabled(context)
    }
}