package com.ossalali.daysremaining.widget.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

// Define the DataStore instance at the top level of the file
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_preferences")

class WidgetDataStore @Inject constructor(private val context: Context) {

    private fun selectedEventsKey(appWidgetId: Int) = stringPreferencesKey("selected_events_for_widget_$appWidgetId")

    // Function to save a list of event IDs
    suspend fun saveSelectedEventIds(appWidgetId: Int, eventIds: List<Int>) {
        context.dataStore.edit { preferences ->
            // Serialize the list to a String (e.g., comma-separated or JSON)
            // Using JSON with kotlinx.serialization for robustness:
            val jsonString = Json.encodeToString(eventIds)
            preferences[selectedEventsKey(appWidgetId)] = jsonString
        }
    }

    // Function to read the list of event IDs
    fun getSelectedEventIds(appWidgetId: Int): Flow<List<Int>> {
        return context.dataStore.data.map { preferences ->
            val jsonString = preferences[selectedEventsKey(appWidgetId)]
            if (jsonString != null) {
                try {
                    Json.decodeFromString<List<Int>>(jsonString)
                } catch (e: SerializationException) { // Catch specific exception
                    // Handle deserialization error, e.g., log it and return empty list
                    android.util.Log.e("WidgetDataStore", "Error deserializing event IDs for widget $appWidgetId: ${e.message}")
                    emptyList<Int>()
                } catch (e: Exception) {
                    // Catch any other unexpected errors during deserialization
                    android.util.Log.e("WidgetDataStore", "Unexpected error deserializing event IDs for widget $appWidgetId: ${e.message}")
                    emptyList<Int>()
                }
            } else {
                emptyList<Int>()
            }
        }
    }
}
