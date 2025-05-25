package com.ossalali.daysremaining.widget

import android.appwidget.AppWidgetManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

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
            // Pass the ViewModel instance to your Composable
            WidgetPreferenceScreen(viewModel = viewModel)
        }
    }
}