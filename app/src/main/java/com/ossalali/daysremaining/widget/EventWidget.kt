package com.ossalali.daysremaining.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.ossalali.daysremaining.MainActivity
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.widget.EventWidget.Companion.BIG_SQUARE
import com.ossalali.daysremaining.widget.EventWidget.Companion.HORIZONTAL_RECTANGLE
import com.ossalali.daysremaining.widget.EventWidget.Companion.SMALL_SQUARE
import com.ossalali.daysremaining.widget.di.WidgetRepositoryEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
        val widgetEntryPoint =
            EntryPointAccessors.fromApplication(
                context.applicationContext,
                WidgetRepositoryEntryPoint::class.java,
            )
        val eventRepo = widgetEntryPoint.eventRepo()
        val widgetDataStore = widgetEntryPoint.widgetDataStore()
        val glanceManager = GlanceAppWidgetManager(context)
        val appWidgetId = glanceManager.getAppWidgetId(id)
        val selectedEventIds = widgetDataStore.getSelectedEventIds(appWidgetId).first()
        val eventItems =
            if (selectedEventIds.isEmpty()) {
                emptyList()
            } else {
                eventRepo.getActiveEventsByIds(selectedEventIds)
            }

        provideContent { GlanceTheme { WidgetContent(eventItems = eventItems, context = context) } }
    }
}

suspend fun refreshWidget(context: Context) {
    EventWidget().updateAll(context)
}

fun addEvents() {
}

@Composable
fun WidgetContent(eventItems: List<EventItem>, context: Context) {
    val size = LocalSize.current
    val scope = rememberCoroutineScope()

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val startActivityAction: Action = actionStartActivity(intent)

    Scaffold(
        modifier = GlanceModifier.fillMaxSize().clickable(startActivityAction),
        titleBar = {
            Box(
                modifier = GlanceModifier.fillMaxWidth().padding(Dimensions.default),
                contentAlignment = Alignment.TopEnd,
            ) {
                Row {
                    CircleIconButton(
                        imageProvider = ImageProvider(R.drawable.baseline_refresh_24),
                        onClick = { scope.launch { refreshWidget(context) } },
                        contentDescription = "Refresh widget",
                    )
                    Spacer(modifier = GlanceModifier.width(Dimensions.half))
                    CircleIconButton(
                        imageProvider = ImageProvider(R.drawable.outline_add_24),
                        onClick = { addEvents() },
                        contentDescription = "Add event",
                    )
                }
            }
        },
    ) {
        if (eventItems.isEmpty()) {
            Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Text(
                    text = "No events selected.\nConfigure widget.",
                    style =
                        TextStyle(
                            fontSize = 12.sp,
                            color = GlanceTheme.colors.onSurface,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Start,
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
