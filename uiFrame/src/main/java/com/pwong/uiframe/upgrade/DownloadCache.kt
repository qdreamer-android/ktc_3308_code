package com.pwong.uiframe.upgrade

import android.content.Context
import com.pwong.library.utils.SPUtils

/**
 * @author android
 * @date 2020/6/1
 */
object DownloadCache {

    private const val DOWNLOAD_ID = "downLoadId"
    private const val APK_MD5 = "apkMD5"

    fun getApkMD5(context: Context): String {
        return SPUtils[context, APK_MD5, ""] as String
    }

    fun setApkMD5(context: Context, md5: String) {
        SPUtils.put(context, APK_MD5, md5)
    }

    fun getApkDownloadID(context: Context): Long {
        return SPUtils[context, DOWNLOAD_ID, -1L] as Long
    }

    fun setApkDownloadID(context: Context, downloadID: Long) {
        SPUtils.put(context, DOWNLOAD_ID, downloadID)
    }

}