package com.ossalali.daysremaining.widget

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.ossalali.daysremaining.MainActivity
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.ui.previews.DefaultWidgetPreviews
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.widget.EventWidget.Companion.HORIZONTAL_RECTANGLE
import com.ossalali.daysremaining.widget.EventWidget.Companion.SMALL_SQUARE
import com.ossalali.daysremaining.widget.EventWidget.Companion.getAutoSize
import com.ossalali.daysremaining.widget.di.WidgetRepositoryEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.abs

class EventWidget : GlanceAppWidget() {
    companion object {
        val SMALL_SQUARE = DpSize(150.dp, 50.dp)
        val HORIZONTAL_RECTANGLE = DpSize(250.dp, 50.dp)
        val BIG_SQUARE = DpSize(250.dp, 250.dp)

        fun getAutoSize(size: DpSize): AutoSize {
            return if (size.height >= BIG_SQUARE.height && size.width >= BIG_SQUARE.width) {
                AutoSize(fontSize = 40.sp, iconSize = 50.dp, cardSize = 250.dp)
            } else if (
                size.height >= HORIZONTAL_RECTANGLE.height &&
                    size.width >= HORIZONTAL_RECTANGLE.width
            ) {
                AutoSize(fontSize = 35.sp, iconSize = 35.dp, cardSize = 250.dp)
            } else if (size.height >= SMALL_SQUARE.height && size.width >= SMALL_SQUARE.width) {
                AutoSize(fontSize = 20.sp, iconSize = 20.dp, cardSize = 250.dp)
            } else {
                AutoSize(fontSize = 10.sp, iconSize = 10.dp, cardSize = 250.dp)
            }
        }
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

fun addEvents() {}

@Composable
fun WidgetContent(eventItems: List<EventItem>, context: Context) {
    val size = LocalSize.current
    val scope = rememberCoroutineScope()

    val startAppIntent =
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    val startActivityAction: Action = actionStartActivity(startAppIntent)

    Scaffold(
        modifier = GlanceModifier.fillMaxSize().clickable(startActivityAction),
        titleBar = {
            if (size != SMALL_SQUARE && size != HORIZONTAL_RECTANGLE) {
                Box(
                    modifier = GlanceModifier.fillMaxWidth().padding(Dimensions.default),
                    contentAlignment = Alignment.TopEnd,
                ) {
                    Row {
                        CircleIconButton(
                            modifier = GlanceModifier.size(getAutoSize(size).iconSize),
                            imageProvider = ImageProvider(R.drawable.baseline_refresh_24),
                            onClick = { scope.launch { refreshWidget(context) } },
                            contentDescription = "Refresh widget",
                            backgroundColor = GlanceTheme.colors.primaryContainer,
                        )
                        Spacer(modifier = GlanceModifier.width(Dimensions.half))
                        CircleIconButton(
                            modifier = GlanceModifier.size(getAutoSize(size).iconSize),
                            imageProvider = ImageProvider(R.drawable.outline_add_24),
                            onClick = { addEvents() },
                            contentDescription = "Add event",
                            backgroundColor = GlanceTheme.colors.primaryContainer,
                        )
                    }
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
                    EventItemCard(eventItems.first())
                } else {
                    eventItems.forEach { eventItem ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            LazyVerticalGrid(gridCells = GridCells.Adaptive(SMALL_SQUARE.width)) {
                                item { EventItemCard(eventItem) }
                            }
                        } else {
                            LazyVerticalGrid(gridCells = GridCells.Fixed(2)) {
                                item { EventItemCard(eventItem) }
                            }
                        }

                        Spacer(modifier = GlanceModifier.height(Dimensions.quarter))
                    }
                }
            }
        }
    }
}

@Composable
fun EventItemCard(eventItem: EventItem) {
    val size = LocalSize.current
    Box(
        modifier =
            GlanceModifier.padding(Dimensions.default)
                .cornerRadius(Dimensions.half)
                .background(GlanceTheme.colors.secondaryContainer)
    ) {
        Text(
            modifier = GlanceModifier.fillMaxWidth(),
            text = eventItem.title,
            style =
                TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = GlanceTheme.colors.primary,
                    textAlign = TextAlign.Center,
                ),
        )
        val daysRemaining = eventItem.numberOfDays.toInt()
        val daysText =
            when {
                daysRemaining == -1 -> "Yesterday"
                daysRemaining < 0 -> "${abs(daysRemaining)} days ago"
                daysRemaining == 0 -> "Today!"
                daysRemaining == 1 -> "Tomorrow"
                else -> "$daysRemaining days"
            }

        val textColor =
            when {
                daysRemaining < 0 -> GlanceTheme.colors.onSurface
                daysRemaining == 0 -> GlanceTheme.colors.error
                daysRemaining <= 7 -> GlanceTheme.colors.primary
                else -> GlanceTheme.colors.onSurface
            }

        Text(
            modifier = GlanceModifier.fillMaxWidth(),
            text = daysText,
            style =
                TextStyle(
                    fontSize = getAutoSize(size).fontSize,
                    color = textColor,
                    textAlign = TextAlign.Center,
                ),
        )
    }
}

@Suppress("unused")
@DefaultWidgetPreviews
@Composable
fun WidgetContentPreview() {
    val eventItem1 =
        EventItem(title = "Event 1", date = LocalDate.now().minusDays(2), description = "test")
    val eventItem2 =
        EventItem(title = "Event 1", date = LocalDate.now().minusDays(1), description = "test")
    val eventItem3 = EventItem(title = "Event 1", date = LocalDate.now(), description = "test")
    val eventItem4 =
        EventItem(title = "Event 2", date = LocalDate.now().plusDays(1), description = "test")
    val eventItem5 =
        EventItem(title = "Event 2", date = LocalDate.now().plusDays(20), description = "test")

    GlanceTheme {
        WidgetContent(
            listOf(eventItem1, eventItem2, eventItem3, eventItem4, eventItem5),
            LocalContext.current,
        )
    }
}
