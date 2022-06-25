package com.pwong.uiframe.utils

import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

object CrashHandler : Thread.UncaughtExceptionHandler {

    private var mErrorListener: OnCrashListener? = null

    fun init(listener: OnCrashListener? = null) {
        mErrorListener = listener
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, throwable: Throwable) {
        mErrorListener?.onCrash(t, throwable, getPrintWriter(throwable))
            ?: Thread.getDefaultUncaughtExceptionHandler()?.uncaughtException(t, throwable)
        exitProcess(0)
    }

    private fun getPrintWriter(throwable: Throwable): StringWriter {
        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        throwable.printStackTrace(printWriter)
        var cause = throwable.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        return writer
    }

    interface OnCrashListener {
        fun onCrash(thread: Thread, throwable: Throwable, writer: StringWriter?)
    }
}