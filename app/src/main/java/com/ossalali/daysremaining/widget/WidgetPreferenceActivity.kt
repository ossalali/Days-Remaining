package com.ossalali.daysremaining.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // Add this
import androidx.lifecycle.ViewModel // Add this
import androidx.lifecycle.ViewModelProvider // Add this
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WidgetPreferenceActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelAssistedFactory: WidgetPreferenceScreenViewModel.Factory

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the result to RESULT_CANCELED initially. If the user backs out of the activity,
        // the widget will be deleted.
        setResult(Activity.RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId)

        Log.d("WidgetPrefActivity", "AppWidgetId: $appWidgetId")
        Log.d("WidgetPrefActivity", "AppWidgetOptions: $appWidgetOptions")

        // Create ViewModel using the factory
        val viewModel: WidgetPreferenceScreenViewModel by viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(WidgetPreferenceScreenViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return viewModelAssistedFactory.create(appWidgetId, appWidgetOptions) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }

        setContent {
            // Pass the ViewModel instance and a callback to finish the activity
            WidgetPreferenceScreen(
                viewModel = viewModel,
                onSaveComplete = { finishWithSuccess() }
            )
        }
    }

    private fun finishWithSuccess() {
        // Set the result to OK and include the widget ID
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(Activity.RESULT_OK, resultValue)

        // Update the widget
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val widgetIds = intArrayOf(appWidgetId)
        val updateIntent = Intent(this, EventWidgetReceiver::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        }
        sendBroadcast(updateIntent)

        finish()
    }
}