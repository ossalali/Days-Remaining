package com.ossalali.daysremaining.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.ossalali.daysremaining.infrastructure.appLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EventWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EventWidget()

    override fun onUpdate(
      context: Context,
      appWidgetManager: AppWidgetManager,
      appWidgetIds: IntArray,
    ) {
        appLogger()
          .d(
            tag = "EventWidgetReceiver",
            message = "onUpdate received for appWidgetIds: ${appWidgetIds.joinToString()}",
          )

        super.onUpdate(context, appWidgetManager, appWidgetIds)

        ProcessLifecycleOwner.get().lifecycleScope.launch {
            appWidgetIds.forEach { appWidgetId ->
                appLogger()
                  .d(
                    tag = "EventWidgetReceiver",
                    message = "Explicitly triggering update for appWidgetId: $appWidgetId",
                  )
                try {
                    val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
                    appLogger()
                      .d(
                        tag = "EventWidgetReceiver",
                        message = "Got GlanceId $glanceId for appWidgetId $appWidgetId",
                      )

                    delay(500)

                    glanceAppWidget.update(context, glanceId)
                    appLogger()
                      .d(
                        tag = "EventWidgetReceiver",
                        message = "Successfully triggered update for appWidgetId $appWidgetId",
                      )
                } catch (e: Exception) {
                    appLogger()
                      .e(
                        tag = "EventWidgetReceiver",
                        message = "Error updating widget $appWidgetId: ${e.message}",
                        throwable = e,
                      )
                }
            }
        }
    }

    override fun onEnabled(context: Context) {
        appLogger().d(tag = "EventWidgetReceiver", message = "onEnabled called")
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        appLogger().d(tag = "EventWidgetReceiver", message = "onDisabled called")
        super.onDisabled(context)
    }
}
