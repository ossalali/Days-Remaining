package com.ossalali.daysremaining.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EventWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EventWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d(
            "EventWidgetReceiver",
            "onUpdate received for appWidgetIds: ${appWidgetIds.joinToString()}"
        )

        super.onUpdate(context, appWidgetManager, appWidgetIds)

        ProcessLifecycleOwner.get().lifecycleScope.launch {
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

                    delay(500)

                    glanceAppWidget.update(context, glanceId)
                    Log.d(
                        "EventWidgetReceiver",
                        "Successfully triggered update for appWidgetId $appWidgetId"
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