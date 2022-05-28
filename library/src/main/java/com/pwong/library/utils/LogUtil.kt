package com.pwong.library.utils

import android.util.Log
import java.io.File
import java.io.FileWriter

object LogUtil {

    private const val showLog = true
    private const val writeLog = true
    private const val TAG = "YongAsr"

    private var mLogFileName: String? = ""

    fun logI(msg: String) {
        if (showLog) {
            logI(TAG, msg)
        }
        writeLog("【${TAG}】: $msg")
    }

    fun logI(TAG: String, msg: String) {
        if (showLog) {
            Log.i(TAG, msg)
        }
        writeLog("【${TAG}】: $msg")
    }

    fun logE(msg: String) {
        if (showLog) {
            logE(TAG, msg)
        }
        writeLog("【${TAG}】: $msg")
    }

    fun logE(TAG: String, msg: String) {
        if (showLog) {
            Log.e(TAG, msg)
        }
        writeLog("【${TAG}】: $msg")
    }

    fun writeLog(msg: String) {
        writeLog(msg, mLogFileName)
    }

    /**
     * @param fileName  写入日志的文件名
     * @param msg       写入的日志消息
     */
    fun writeLog(msg: String?, fileName: String? = null) {
        if (!writeLog || msg.isNullOrEmpty() || StorageUtil.CACHE_PATH_LOG.isEmpty()) return
        var writer: FileWriter? = null
        try {
            val dirFile = File(StorageUtil.CACHE_PATH_LOG)
            if (!dirFile.exists() || !dirFile.isDirectory) {
                dirFile.mkdirs()
            }
            val logFileName = if (fileName.isNullOrEmpty()) {
                if (mLogFileName.isNullOrEmpty()) {
                    mLogFileName = TimeUtil.stampToDate(System.currentTimeMillis(), "yyyyMMddHH") + ".log"
                }
                mLogFileName!!
            } else {
                fileName
            }
            writer = FileWriter(File(dirFile, logFileName), true)
            writer.write("${TimeUtil.getCompleteTime()}\t$msg\n")
            writer.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            writer?.close()
        }
    }
}