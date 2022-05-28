package com.pwong.library.utils

import android.content.Context
import java.io.File

/**
 * @author android
 * @date 20-5-5
 */
object StorageUtil {

    var CACHE_PATH_ROOT = ""
        private set
    var CACHE_PATH_IMAGE = ""
        get() = getDirPathAfterMkdirs("image")
        private set
    var CACHE_PATH_VIDEO = ""
        get() = getDirPathAfterMkdirs("video")
        private set
    var CACHE_PATH_AUDIO = ""
        get() = getDirPathAfterMkdirs("audio")
        private set
    var CACHE_PATH_AUDIO_PROCESS = ""
        get() = getDirPathAfterMkdirs("process")
        private set
    var CACHE_PATH_LOG = ""
        get() = getDirPathAfterMkdirs("log")
        private set
    fun getDirPathAfterMkdirs(dirPath: String): String {
        val filePath = "$CACHE_PATH_ROOT$dirPath${File.separator}"
        val dirFile = File(filePath)
        if (!dirFile.exists() || !dirFile.isDirectory) {
            dirFile.mkdirs()
        }
        return filePath
    }
    fun getDirAfterMkdirs(dirPath: String): File {
        val filePath = "$CACHE_PATH_ROOT$dirPath${File.separator}"
        val dirFile = File(filePath)
        if (!dirFile.exists() || !dirFile.isDirectory) {
            dirFile.mkdirs()
        }
        return dirFile
    }

    fun initCachePath(context: Context) {
        CACHE_PATH_ROOT = (context.getExternalFilesDir(null)?.absolutePath ?: context.filesDir.absolutePath) + File.separator
//        CACHE_PATH_ROOT = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            (context.getExternalFilesDir(null)?.absolutePath ?: context.filesDir.absolutePath) + File.separator
//        } else {
//            Environment.getExternalStorageDirectory().absolutePath + File.separator + context.packageName + File.separator
//        }
        CACHE_PATH_IMAGE = "${CACHE_PATH_ROOT}image${File.separator}"
        CACHE_PATH_VIDEO = "${CACHE_PATH_ROOT}video${File.separator}"
        CACHE_PATH_AUDIO = "${CACHE_PATH_ROOT}audio${File.separator}"
        CACHE_PATH_AUDIO_PROCESS = "${CACHE_PATH_ROOT}process${File.separator}"
        CACHE_PATH_LOG = "${CACHE_PATH_ROOT}log${File.separator}"
    }


}