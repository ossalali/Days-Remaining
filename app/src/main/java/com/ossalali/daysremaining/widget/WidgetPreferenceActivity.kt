package com.ossalali.daysremaining.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

        // Inside onCreate method, after viewModel is initialized:

        lifecycleScope.launch {
            viewModel.widgetUpdateRequest.collect {
                Log.d("WidgetPrefActivity", "Received request for immediate widget update.")
                triggerWidgetUpdateNow()
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

    // Inside WidgetPreferenceActivity class
    private suspend fun triggerWidgetUpdateNow() {
        Log.d("WidgetPrefActivity", "Triggering immediate widget update for $appWidgetId")
        try {
            delay(250) // Added delay for DataStore persistence

            val glanceId = GlanceAppWidgetManager(this@WidgetPreferenceActivity).getGlanceIdBy(appWidgetId)
            EventWidget().update(this@WidgetPreferenceActivity, glanceId)
            // Add a second update after a short delay to ensure changes are applied
            delay(100) // Using the same delay as in finishWithSuccess
            EventWidget().update(this@WidgetPreferenceActivity, glanceId)
            Log.d("WidgetPrefActivity", "Immediate widget update for $appWidgetId completed.")
        } catch (e: Exception) {
            Log.e("WidgetPrefActivity", "Error during immediate widget update for $appWidgetId: ${e.message}", e)
        }
    }

    private fun finishWithSuccess() {
        // Set the result to OK and include the widget ID
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(Activity.RESULT_OK, resultValue)

        lifecycleScope.launch {
            try {
                // Wait for DataStore save to complete
                delay(200)

                Log.d("WidgetPrefActivity", "Updating widget $appWidgetId")

                // Update only the specific widget that was configured
                val glanceId =
                    GlanceAppWidgetManager(this@WidgetPreferenceActivity).getGlanceIdBy(appWidgetId)
                EventWidget().update(this@WidgetPreferenceActivity, glanceId)

                // Add a second update after a short delay to ensure changes are applied
                delay(100)
                EventWidget().update(this@WidgetPreferenceActivity, glanceId)

                Log.d("WidgetPrefActivity", "Widget update completed successfully")
                
            } catch (e: Exception) {
                Log.e("WidgetPrefActivity", "Error during widget update: ${e.message}", e)
            } finally {
                // Small final delay before finishing
                delay(100)
                Log.d("WidgetPrefActivity", "Finishing WidgetPreferenceActivity")
                finish()
            }
        }
    }
}