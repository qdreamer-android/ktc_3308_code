package com.pwong.library.utils

import android.content.Context
import java.io.File

object StorageUtil {

    private var CACHE_PATH_ROOT = ""

    fun getDirPathAfterMkdirs(dirPath: String): String {
        val filePath = "$CACHE_PATH_ROOT$dirPath${File.separator}"
        val dirFile = File(filePath)
        if (!dirFile.exists() || !dirFile.isDirectory) {
            dirFile.mkdirs()
        }
        return filePath
    }

    fun initCachePath(context: Context) {
        CACHE_PATH_ROOT = (context.getExternalFilesDir(null)?.absolutePath ?: context.filesDir.absolutePath) + File.separator
    }


}