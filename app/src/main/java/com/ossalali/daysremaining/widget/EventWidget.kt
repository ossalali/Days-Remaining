package com.ossalali.daysremaining.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.ossalali.daysremaining.MainActivity
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.widget.di.WidgetRepositoryEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

class EventWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val eventItems =
          try {
              val widgetEntryPoint =
                EntryPointAccessors.fromApplication(
                  context.applicationContext,
                  WidgetRepositoryEntryPoint::class.java,
                )
              val eventRepo = widgetEntryPoint.eventRepo()
              val widgetDataStore = widgetEntryPoint.widgetDataStore()

              // Get widget configuration with timeout
              val glanceManager = GlanceAppWidgetManager(context)
              val appWidgetId =
                withTimeoutOrNull(1000) {
                    try {
                        glanceManager.getAppWidgetId(id)
                    } catch (e: Exception) {
                        Log.e("EventWidget", "Error getting appWidgetId: ${e.message}")
                        null
                    }
                }

              if (appWidgetId == null) {
                  emptyList()
              } else {
                  val selectedEventIds =
                    withTimeoutOrNull(2000) {
                        widgetDataStore.getSelectedEventIds(appWidgetId).first()
                    } ?: emptyList()

                  if (selectedEventIds.isEmpty()) {
                      emptyList()
                  } else {
                      withTimeoutOrNull(2000) { eventRepo.getActiveEventsByIds(selectedEventIds) }
                        ?: emptyList()
                  }
              }
          } catch (e: Exception) {
              Log.e("EventWidget", "Error fetching widget data: ${e.message}", e)
              emptyList()
          }

        provideContent { GlanceTheme { WidgetContent(eventItems) } }
    }
}

@Composable
fun WidgetContent(eventItems: List<EventItem>) {

    Box(
      modifier =
        GlanceModifier.fillMaxSize()
          .padding(8.dp)
          .cornerRadius(16.dp)
          .background(GlanceTheme.colors.background)
    ) {
        Box(
          modifier =
            GlanceModifier.fillMaxSize()
              .padding(8.dp)
              .cornerRadius(8.dp)
              .background(GlanceTheme.colors.widgetBackground)
              .appWidgetBackground()
              .clickable(actionStartActivity<MainActivity>())
        ) {
            if (eventItems.isEmpty()) {
                Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                      text = "No events selected.\nConfigure widget.",
                      style =
                        TextStyle(
                          fontSize = 12.sp,
                          color = GlanceTheme.colors.onSurface,
                          fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            } else {
                Column(
                  modifier = GlanceModifier.fillMaxSize().padding(4.dp),
                  verticalAlignment = Alignment.Top,
                  horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    eventItems.forEach { eventItem -> EventItemRow(eventItem) }
                }
            }
        }
    }
}

@Composable
fun EventItemRow(eventItem: EventItem) {
    Column(
      modifier = GlanceModifier.wrapContentHeight().padding(vertical = 2.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
          text = eventItem.title,
          style =
            TextStyle(
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = GlanceTheme.colors.onSurface,
            ),
        )

        val daysRemaining = eventItem.numberOfDays.toInt()
        val daysText =
          when {
              daysRemaining < 0 -> "${Math.abs(daysRemaining)} days ago"
              daysRemaining == 0 -> "Today!"
              else -> "$daysRemaining days"
          }

        val textColor =
          when {
              daysRemaining < 0 -> GlanceTheme.colors.onSurface
              daysRemaining == 0 -> GlanceTheme.colors.error
              daysRemaining <= 7 -> GlanceTheme.colors.primary
              else -> GlanceTheme.colors.onSurface
          }

        Text(text = daysText, style = TextStyle(fontSize = 10.sp, color = textColor))
    }
}
