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

fun Any.logDebug(message: String) {
    appLogger().d(message)
}

fun Any.logInfo(message: String) {
    appLogger().i(message)
}

fun Any.logWarn(message: String) {
    appLogger().w(message)
}

fun Any.logError(message: String, throwable: Throwable? = null) {
    appLogger().e(message, throwable)
}
