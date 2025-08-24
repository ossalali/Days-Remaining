package com.ossalali.daysremaining.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val DS_NAME = "settings"
private val Context.dataStore by preferencesDataStore(name = DS_NAME)

private object Keys {
    val DARK = booleanPreferencesKey("dark_mode")
    val NOTIFS = booleanPreferencesKey("notifications_enabled")
    val AUTO_ARCHIVE = booleanPreferencesKey("auto_archive_enabled")
}

@Singleton
class SettingsRepository
@Inject
constructor(@param:ApplicationContext private val context: Context) {
    val darkMode: Flow<Boolean> =
        context.dataStore.data.map { preferences -> preferences[Keys.DARK] ?: false }

    val notifications: Flow<Boolean> =
        context.dataStore.data.map { preferences -> preferences[Keys.NOTIFS] ?: false }

    val autoArchive: Flow<Boolean> =
        context.dataStore.data.map { preferences -> preferences[Keys.AUTO_ARCHIVE] ?: false }

    suspend fun setDarkMode(enabled: Boolean) =
        context.dataStore.edit { preferences -> preferences[Keys.DARK] = enabled }

    suspend fun setNotifications(enabled: Boolean) =
        context.dataStore.edit { preferences -> preferences[Keys.NOTIFS] = enabled }

    suspend fun setAutoArchive(enabled: Boolean) =
        context.dataStore.edit { preferences -> preferences[Keys.AUTO_ARCHIVE] = enabled }
}
