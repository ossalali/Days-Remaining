package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.App

/** Get the application logger instance. This can be used from anywhere in the application. */
fun appLogger(): Logger {
  return try {
    App.getInstance().logger
  } catch (_: Exception) {
    Logger.default
  }
}
