package com.ossalali.daysremaining.infrastructure

import android.util.Log
import com.ossalali.daysremaining.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central logging utility for the application. Provides consistent logging across the app with
 * support for different log levels and contexts.
 */
@Singleton
class Logger @Inject constructor() {

    companion object {
        private const val APP_TAG = "DaysRemaining"

        val default = Logger()

        /** Get the calling class name to use as tag */
        private fun getCallerClassName(): String {
            val stackTrace = Thread.currentThread().stackTrace
            for (i in 4 until stackTrace.size) {
                val className = stackTrace[i].className
                if (!className.contains("Logger")) {
                    return className.substringAfterLast('.')
                }
            }
            return APP_TAG
        }
    }

    /** Log a debug message, visible only in debug builds */
    fun d(message: String, tag: String? = null) {
        if (BuildConfig.DEBUG) {
            Log.d(tag ?: getCallerClassName(), message)
        }
    }

    /** Log an info message */
    fun i(message: String, tag: String? = null) {
        Log.i(tag ?: getCallerClassName(), message)
    }

    /** Log a warning message */
    fun w(message: String, tag: String? = null) {
        Log.w(tag ?: getCallerClassName(), message)
    }

    /** Log an error message */
    fun e(message: String, throwable: Throwable? = null, tag: String? = null) {
        val logTag = tag ?: getCallerClassName()
        if (throwable != null) {
            Log.e(logTag, message, throwable)
        } else {
            Log.e(logTag, message)
        }
    }

    /** Log a message with a specific context */
    fun withContext(context: String): ContextLogger {
        return ContextLogger(this, context)
    }

    /** Helper class to provide context-specific logging */
    class ContextLogger(private val logger: Logger, private val context: String) {
        private val contextTag = "$APP_TAG-$context"

        fun debug(message: String) = logger.d(message, contextTag)

        fun info(message: String) = logger.i(message, contextTag)

        fun warn(message: String) = logger.w(message, contextTag)

        fun error(message: String, throwable: Throwable? = null) =
          logger.e(message, throwable, contextTag)
    }
}
