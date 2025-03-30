package com.ossalali.daysremaining.widget

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
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

class EventWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: androidx.glance.GlanceId) {
        val widgetEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetRepositoryEntryPoint::class.java
        )
        val eventRepo = widgetEntryPoint.eventRepo()
        val event = eventRepo.getFirstEvent()

        provideContent {
            val colorProvider = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                GlanceTheme.colors
            } else {
                WidgetColorScheme.colors
            }
            GlanceTheme(colors = colorProvider) {
                WidgetContent(event)
            }
        }
    }
}

@Composable
fun WidgetContent(eventItem: EventItem) {
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
            Column {
                Text(
                    text = eventItem.title,
                    style = TextStyle(fontSize = 12.sp),
                    maxLines = 1
                )
                Row {
                    Text(
                        text = eventItem.getNumberOfDays().toString(),
                        style = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                    Text(
                        text = "d",
                        style = TextStyle(fontSize = 12.sp),
                        modifier = GlanceModifier.padding(start = 4.dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}