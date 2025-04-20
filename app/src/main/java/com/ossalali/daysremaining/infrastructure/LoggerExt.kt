package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.App

/**
 * Get the application logger instance.
 * This can be used from anywhere in the application.
 */
fun appLogger(): Logger {
    return try {
        App.getInstance().logger
    } catch (_: Exception) {
        Logger.default
    }
}

// Extension functions for common log types

fun Any.logDebug(message: String) {
    appLogger().debug(message)
}

fun Any.logInfo(message: String) {
    appLogger().info(message)
}

fun Any.logWarn(message: String) {
    appLogger().warn(message)
}

fun Any.logError(message: String, throwable: Throwable? = null) {
    appLogger().error(message, throwable)
} 