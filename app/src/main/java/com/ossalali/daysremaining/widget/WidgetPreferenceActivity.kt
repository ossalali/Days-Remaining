package com.ossalali.daysremaining.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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

    @Inject lateinit var viewModelAssistedFactory: WidgetPreferenceScreenViewModel.Factory

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setResult(RESULT_CANCELED)

        appWidgetId =
          intent
            ?.extras
            ?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId)

        Log.d("WidgetPrefActivity", "AppWidgetId: $appWidgetId")
        Log.d("WidgetPrefActivity", "AppWidgetOptions: $appWidgetOptions")

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
            WidgetPreferenceScreen(viewModel = viewModel, onSaveComplete = { finishWithSuccess() })
        }
    }

    private fun finishWithSuccess() {
        val resultValue =
          Intent().apply { putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId) }
        setResult(RESULT_OK, resultValue)

        lifecycleScope.launch {
            try {
                delay(200)

                Log.d("WidgetPrefActivity", "Updating widget $appWidgetId")

                val glanceId =
                  GlanceAppWidgetManager(this@WidgetPreferenceActivity).getGlanceIdBy(appWidgetId)
                EventWidget().update(this@WidgetPreferenceActivity, glanceId)

                delay(100)
                EventWidget().update(this@WidgetPreferenceActivity, glanceId)

                Log.d("WidgetPrefActivity", "Widget update completed successfully")
            } catch (e: Exception) {
                Log.e("WidgetPrefActivity", "Error during widget update: ${e.message}", e)
            } finally {
                delay(100)
                Log.d("WidgetPrefActivity", "Finishing WidgetPreferenceActivity")
                finish()
            }
        }
    }
}
