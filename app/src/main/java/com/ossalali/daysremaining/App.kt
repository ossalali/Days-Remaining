package com.ossalali.daysremaining

import android.app.Application
import com.ossalali.daysremaining.infrastructure.Logger
import com.ossalali.daysremaining.infrastructure.NotificationHelper
import com.ossalali.daysremaining.settings.AutoArchiver
import com.ossalali.daysremaining.settings.SettingsRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltAndroidApp
class App : Application() {

  @Inject lateinit var logger: Logger

  @Inject lateinit var settingsRepository: SettingsRepository

  @Inject lateinit var autoArchiver: AutoArchiver

  @Inject lateinit var notificationHelper: NotificationHelper

  companion object {
    private lateinit var instance: App

    fun getInstance(): App = instance
  }

  override fun onCreate() {
    super.onCreate()
    instance = this
    logger.i("Application started")

    // Initialize notification channel at startup
    notificationHelper.createNotificationChannel(this)
    notificationHelper.logNotificationStatus(this)

    CoroutineScope(Dispatchers.Default).launch {
      val enabled = settingsRepository.autoArchive.first()
      if (enabled) {
        autoArchiver.start(this@App)
      }
    }
  }
}
