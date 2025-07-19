package com.ossalali.daysremaining.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
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
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.widget.EventWidget.Companion.BIG_SQUARE
import com.ossalali.daysremaining.widget.EventWidget.Companion.HORIZONTAL_RECTANGLE
import com.ossalali.daysremaining.widget.EventWidget.Companion.SMALL_SQUARE
import com.ossalali.daysremaining.widget.di.WidgetRepositoryEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs

class EventWidget : GlanceAppWidget() {
    companion object {
        val SMALL_SQUARE = DpSize(150.dp, 50.dp)
        val HORIZONTAL_RECTANGLE = DpSize(250.dp, 50.dp)
        val BIG_SQUARE = DpSize(250.dp, 250.dp)
    }

    override val sizeMode =
      SizeMode.Responsive(setOf(SMALL_SQUARE, HORIZONTAL_RECTANGLE, BIG_SQUARE))

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
    val size = LocalSize.current

    Box(
      modifier =
        GlanceModifier.fillMaxSize()
          .padding(Dimensions.half)
          .cornerRadius(Dimensions.default)
          .background(GlanceTheme.colors.background)
    ) {
        Box(
          modifier =
            GlanceModifier.fillMaxSize()
              .padding(Dimensions.half)
              .cornerRadius(Dimensions.half)
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
                  modifier = GlanceModifier.fillMaxSize().padding(Dimensions.quarter),
                  verticalAlignment = Alignment.Top,
                  horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (size.height <= SMALL_SQUARE.height && size.width <= SMALL_SQUARE.width) {
                        EventItemRow(eventItems.first(), size)
                    } else {
                        eventItems.forEach { eventItem -> EventItemRow(eventItem, size) }
                    }
                }
            }
        }
    }
}

@Composable
fun EventItemRow(eventItem: EventItem, size: DpSize) {
    fun autoSizeFont(): TextUnit =
      if (size.height >= BIG_SQUARE.height && size.width >= BIG_SQUARE.width) {
          40.sp
      } else if (
        size.height >= HORIZONTAL_RECTANGLE.height && size.width >= HORIZONTAL_RECTANGLE.width
      ) {
          35.sp
      } else if (size.height >= SMALL_SQUARE.height && size.width >= SMALL_SQUARE.width) {
          20.sp
      } else {
          10.sp
      }

    Column(
      modifier = GlanceModifier.wrapContentHeight().padding(vertical = Dimensions.eighth),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
          text = eventItem.title,
          style =
            TextStyle(
              fontSize = 12.sp,
              fontWeight = FontWeight.Normal,
              color = GlanceTheme.colors.onSurface,
            ),
        )

        val daysRemaining = eventItem.numberOfDays.toInt()
        val daysText =
          when {
              daysRemaining < 0 -> "${abs(daysRemaining)} days ago"
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

        Text(text = daysText, style = TextStyle(fontSize = autoSizeFont(), color = textColor))
    }
}
