package com.ossalali.daysremaining.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class EventWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EventWidget()

    override fun onUpdate(
      context: Context,
      appWidgetManager: AppWidgetManager,
      appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        ProcessLifecycleOwner.get().lifecycleScope.launch {
            appWidgetIds.forEach { appWidgetId ->
                val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
                glanceAppWidget.update(context, glanceId)
            }
        }
    }
}
