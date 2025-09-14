package com.ossalali.daysremaining.widget

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.Dp
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
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.lazy.items
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
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.widget.EventWidget.Companion.ADD_EVENT_ACTION
import com.ossalali.daysremaining.widget.EventWidget.Companion.EVENT_ID
import com.ossalali.daysremaining.widget.EventWidget.Companion.VIEW_EVENT_ACTION
import com.ossalali.daysremaining.widget.di.WidgetRepositoryEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class WidgetUiState(
    val titleFontSize: TextUnit,
    val daysFontSize: TextUnit,
    val iconSize: Dp,
    val gridColumns: Int,
    val showTitleBar: Boolean,
    val isSingleItem: Boolean,
    val maxItems: Int = 14,
    val selectedSize: String,
)

class EventWidget : GlanceAppWidget() {
  companion object {
    const val VIEW_EVENT_ACTION = "com.ossalali.daysremaining.action.VIEW_EVENT"
    const val ADD_EVENT_ACTION = "com.ossalali.daysremaining.action.ADD_EVENT"
    const val EVENT_ID = "EVENT_ID"

    private val TINY_SQUARE = DpSize(120.dp, 50.dp)
    private val SMALL_SQUARE = DpSize(250.dp, 250.dp)
    private val MEDIUM_SQUARE = DpSize(400.dp, 400.dp)
    private val BIG_SQUARE = DpSize(400.dp, 500.dp)
    private val EXTRA_BIG_SQUARE = DpSize(400.dp, 600.dp)
    private val SMALL_HORIZONTAL_RECTANGLE = DpSize(250.dp, 120.dp)
    private val VERTICAL_RECTANGLE = DpSize(120.dp, 300.dp)
    private val LARGE_RECTANGLE = DpSize(300.dp, 250.dp)
    private val LARGER_RECTANGLE = DpSize(300.dp, 400.dp)
    private val TALLISH_RECTANGLE = DpSize(120.dp, 300.dp)
    private val TALL_RECTANGLE = DpSize(120.dp, 500.dp)
    private val TALLER_RECTANGLE = DpSize(120.dp, 600.dp)
    private val WIDE_RECTANGLE = DpSize(300.dp, 120.dp)
    private val WIDE_TALL_RECTANGLE = DpSize(300.dp, 300.dp)
    private val WIDER_TALLER_RECTANGLE = DpSize(300.dp, 500.dp)
    private val WIDER_EVEN_TALLER_RECTANGLE = DpSize(300.dp, 600.dp)

    /**
     * Maps each defined DpSize to a specific UI configuration. The 'when' block is ordered from
     * largest to smallest to ensure the correct state is selected for the available space.
     */
    fun getWidgetUiState(size: DpSize): WidgetUiState {
      return when {
        size.width >= EXTRA_BIG_SQUARE.width && size.height >= EXTRA_BIG_SQUARE.height ->
            WidgetUiState(
                titleFontSize = 20.sp,
                daysFontSize = 40.sp,
                iconSize = 50.dp,
                gridColumns = 2,
                showTitleBar = true,
                isSingleItem = false,
                maxItems = 10,
                selectedSize = "EXTRA_BIG_SQUARE",
            )
        size.width >= BIG_SQUARE.width && size.height >= BIG_SQUARE.height ->
            WidgetUiState(
                titleFontSize = 18.sp,
                daysFontSize = 36.sp,
                iconSize = 50.dp,
                gridColumns = 2,
                showTitleBar = true,
                isSingleItem = false,
                maxItems = 8,
                selectedSize = "BIG_SQUARE",
            )
        size.width >= MEDIUM_SQUARE.width && size.height >= MEDIUM_SQUARE.height ->
            WidgetUiState(
                titleFontSize = 16.sp,
                daysFontSize = 32.sp,
                iconSize = 50.dp,
                gridColumns = 2,
                showTitleBar = true,
                isSingleItem = false,
                maxItems = 6,
                selectedSize = "MEDIUM_SQUARE",
            )
        size.width >= WIDER_EVEN_TALLER_RECTANGLE.width &&
            size.height >= WIDER_EVEN_TALLER_RECTANGLE.height ->
            WidgetUiState(
                titleFontSize = 14.sp,
                daysFontSize = 28.sp,
                iconSize = 50.dp,
                gridColumns = 1,
                showTitleBar = true,
                isSingleItem = false,
                selectedSize = "WIDER_EVEN_TALLER_RECTANGLE",
            )
        size.width >= WIDER_TALLER_RECTANGLE.width &&
            size.height >= WIDER_TALLER_RECTANGLE.height ->
            WidgetUiState(
                titleFontSize = 14.sp,
                daysFontSize = 28.sp,
                iconSize = 28.dp,
                gridColumns = 1,
                showTitleBar = true,
                isSingleItem = false,
                maxItems = 10,
                selectedSize = "WIDER_TALLER_RECTANGLE",
            )
        size.width >= WIDE_TALL_RECTANGLE.width && size.height >= WIDE_TALL_RECTANGLE.height ->
            WidgetUiState(
                titleFontSize = 14.sp,
                daysFontSize = 28.sp,
                iconSize = 28.dp,
                gridColumns = 2,
                showTitleBar = true,
                isSingleItem = false,
                maxItems = 8,
                selectedSize = "WIDE_TALL_RECTANGLE",
            )
        size.width >= TALLER_RECTANGLE.width && size.height >= TALLER_RECTANGLE.height ->
            WidgetUiState(
                titleFontSize = 14.sp,
                daysFontSize = 28.sp,
                iconSize = 28.dp,
                gridColumns = 1,
                showTitleBar = true,
                isSingleItem = false,
                maxItems = 7,
                selectedSize = "TALLER_RECTANGLE",
            )
        size.width >= TALL_RECTANGLE.width && size.height >= TALL_RECTANGLE.height ->
            WidgetUiState(
                titleFontSize = 14.sp,
                daysFontSize = 28.sp,
                iconSize = 28.dp,
                gridColumns = 1,
                showTitleBar = true,
                isSingleItem = false,
                maxItems = 5,
                selectedSize = "TALL_RECTANGLE",
            )
        size.width >= TALLISH_RECTANGLE.width && size.height >= TALLISH_RECTANGLE.height ->
            WidgetUiState(
                titleFontSize = 14.sp,
                daysFontSize = 28.sp,
                iconSize = 28.dp,
                gridColumns = 1,
                showTitleBar = true,
                isSingleItem = false,
                maxItems = 4,
                selectedSize = "TALLISH_RECTANGLE",
            )
        size.width >= WIDE_RECTANGLE.width &&
            size.height >= WIDE_RECTANGLE.height &&
            size.height < LARGE_RECTANGLE.height ->
            WidgetUiState(
                titleFontSize = 12.sp,
                daysFontSize = 22.sp,
                iconSize = 24.dp,
                gridColumns = 4,
                showTitleBar = false,
                isSingleItem = false,
                maxItems = 2,
                selectedSize = "WIDE_RECTANGLE",
            )
        size.width >= LARGER_RECTANGLE.width && size.height >= LARGER_RECTANGLE.height ->
            WidgetUiState(
                titleFontSize = 14.sp,
                daysFontSize = 28.sp,
                iconSize = 28.dp,
                gridColumns = 2,
                showTitleBar = true,
                isSingleItem = false,
                maxItems = 4,
                selectedSize = "LARGER_RECTANGLE",
            )
        size.width >= LARGE_RECTANGLE.width && size.height >= LARGE_RECTANGLE.height ->
            WidgetUiState(
                titleFontSize = 14.sp,
                daysFontSize = 28.sp,
                iconSize = 28.dp,
                gridColumns = 2,
                showTitleBar = true,
                isSingleItem = false,
                maxItems = 4,
                selectedSize = "LARGE_RECTANGLE",
            )
        size.height >= VERTICAL_RECTANGLE.height && size.width >= VERTICAL_RECTANGLE.width ->
            WidgetUiState(
                titleFontSize = 14.sp,
                daysFontSize = 24.sp,
                iconSize = 26.dp,
                gridColumns = 1,
                showTitleBar = true,
                isSingleItem = false,
                maxItems = 4,
                selectedSize = "VERTICAL_RECTANGLE",
            )
        size.width >= SMALL_SQUARE.width && size.height >= SMALL_SQUARE.height ->
            WidgetUiState(
                titleFontSize = 14.sp,
                daysFontSize = 26.sp,
                iconSize = 26.dp,
                gridColumns = 1,
                showTitleBar = true,
                isSingleItem = false,
                maxItems = 2,
                selectedSize = "SMALL_SQUARE",
            )

        size.width >= SMALL_HORIZONTAL_RECTANGLE.width &&
            size.height >= SMALL_HORIZONTAL_RECTANGLE.height ->
            WidgetUiState(
                titleFontSize = 12.sp,
                daysFontSize = 22.sp,
                iconSize = 0.dp,
                gridColumns = 2,
                showTitleBar = false,
                isSingleItem = true,
                maxItems = 1,
                selectedSize = "SMALL_HORIZONTAL_RECTANGLE",
            )
        size.width >= TINY_SQUARE.width && size.height >= TINY_SQUARE.height ->
            WidgetUiState(
                titleFontSize = 12.sp,
                daysFontSize = 20.sp,
                iconSize = 0.dp,
                gridColumns = 1,
                showTitleBar = false,
                isSingleItem = true,
                maxItems = 1,
                selectedSize = "TINY_SQUARE",
            )
        else ->
            WidgetUiState(
                titleFontSize = 12.sp,
                daysFontSize = 20.sp,
                iconSize = 0.dp,
                gridColumns = 1,
                showTitleBar = false,
                isSingleItem = true,
                maxItems = 1,
                selectedSize = "DEFAULT",
            )
      }
    }
  }

  override val sizeMode =
      SizeMode.Responsive(
          setOf(
              TINY_SQUARE,
              SMALL_HORIZONTAL_RECTANGLE,
              WIDE_RECTANGLE,
              VERTICAL_RECTANGLE,
              TALLER_RECTANGLE,
              TALLISH_RECTANGLE,
              TALL_RECTANGLE,
              SMALL_SQUARE,
              WIDER_EVEN_TALLER_RECTANGLE,
              WIDER_TALLER_RECTANGLE,
              WIDE_TALL_RECTANGLE,
              LARGE_RECTANGLE,
              LARGER_RECTANGLE,
              MEDIUM_SQUARE,
              BIG_SQUARE,
              EXTRA_BIG_SQUARE,
          )
      )

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    val widgetEntryPoint =
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetRepositoryEntryPoint::class.java,
        )
    val eventRepo = widgetEntryPoint.eventRepo()
    val settingsRepo = widgetEntryPoint.settingsRepo()
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
    val customDateNotation = settingsRepo.customDateNotation.first()

    provideContent {
      GlanceTheme {
        WidgetContent(
            eventItems = eventItems,
            customDateNotation = customDateNotation,
            context = context,
        )
      }
    }
  }
}

suspend fun refreshWidget(context: Context) {
  EventWidget().updateAll(context)
}

@Composable
fun WidgetContent(eventItems: List<EventItem>, customDateNotation: Boolean, context: Context) {
  val size = LocalSize.current
  val scope = rememberCoroutineScope()

  val uiState = EventWidget.getWidgetUiState(size)

  val generalAppIntent =
      Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      }
  val startActivityAction: Action = actionStartActivity(generalAppIntent)

  val addEventIntent =
      Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        action = ADD_EVENT_ACTION
      }
  val addEventAction = actionStartActivity(addEventIntent)

  Scaffold(
      modifier = GlanceModifier.fillMaxSize().clickable(startActivityAction),
      titleBar = {
        if (uiState.showTitleBar) {
          Box(
              modifier = GlanceModifier.fillMaxWidth().padding(Dimensions.default),
              contentAlignment = Alignment.TopEnd,
          ) {
            Row {
              CircleIconButton(
                  modifier = GlanceModifier.size(uiState.iconSize),
                  imageProvider = ImageProvider(R.drawable.baseline_refresh_24),
                  onClick = { scope.launch { refreshWidget(context) } },
                  contentDescription = "Refresh widget",
                  backgroundColor = GlanceTheme.colors.primaryContainer,
              )
              Spacer(modifier = GlanceModifier.width(Dimensions.half))
              CircleIconButton(
                  modifier = GlanceModifier.size(uiState.iconSize),
                  imageProvider = ImageProvider(R.drawable.outline_add_24),
                  onClick = addEventAction,
                  contentDescription = "Add event",
                  backgroundColor = GlanceTheme.colors.primaryContainer,
              )
            }
          }
        }
      },
  ) {
    if (eventItems.isEmpty()) {
      Box(
          modifier = GlanceModifier.fillMaxSize().clickable(startActivityAction),
          contentAlignment = Alignment.Center,
      ) {
        Text(
            text = "No events selected.\nConfigure widget.",
            style =
                TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                ),
        )
      }
    } else {
      if (uiState.isSingleItem) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          EventItemCard(
              eventItem = eventItems.first(),
              customDateNotation = customDateNotation,
              uiState = uiState,
              context = context,
          )
        }
      } else {
        val gridCells =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
              GridCells.Adaptive(140.dp)
            } else {
              GridCells.Fixed(uiState.gridColumns)
            }

        val itemsToShow = eventItems.take(uiState.maxItems)

        LazyVerticalGrid(modifier = GlanceModifier.fillMaxSize(), gridCells = gridCells) {
          items(itemsToShow, itemId = { event -> event.id.toLong() }) { eventItem ->
            EventItemCard(
                eventItem = eventItem,
                customDateNotation = customDateNotation,
                uiState = uiState,
                context = context,
            )
          }
        }
      }
    }
  }
}

@Composable
fun EventItemCard(
    eventItem: EventItem,
    customDateNotation: Boolean,
    uiState: WidgetUiState,
    context: Context,
) {
  val eventDetailIntent =
      Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        action = VIEW_EVENT_ACTION
        putExtra(EVENT_ID, eventItem.id)
      }
  val itemClickAction = actionStartActivity(eventDetailIntent)

  Box(modifier = GlanceModifier.padding(Dimensions.quarter).clickable(itemClickAction)) {
    Column(
        modifier =
            GlanceModifier.fillMaxSize()
                .background(GlanceTheme.colors.secondaryContainer)
                .cornerRadius(Dimensions.default)
                .padding(Dimensions.half),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
          modifier = GlanceModifier.fillMaxWidth(),
          text = eventItem.title,
          style =
              TextStyle(
                  fontSize = uiState.titleFontSize,
                  fontWeight = FontWeight.Normal,
                  color = GlanceTheme.colors.primary,
                  textAlign = TextAlign.Center,
              ),
          maxLines = 1,
      )

      Spacer(modifier = GlanceModifier.height(Dimensions.quarter))

        val daysRemaining = eventItem.getNumberOfDays(customDateNotation)

      Text(
          modifier = GlanceModifier.fillMaxWidth(),
          text = daysRemaining,
          style =
              TextStyle(
                  fontSize = uiState.daysFontSize,
                  textAlign = TextAlign.Center,
              ),
      )
    }
  }
}
