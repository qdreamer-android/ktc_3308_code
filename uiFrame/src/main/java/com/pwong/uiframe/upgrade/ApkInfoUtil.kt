package com.pwong.uiframe.upgrade

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.pwong.library.utils.FileUtils
import java.io.File


/**
 * @author android
 * @date 2019/7/2
 */
object ApkInfoUtil {

    fun startInstall(context: Context, uri: Uri) {
        val install = Intent(Intent.ACTION_VIEW)
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val filePath = FileUtils.getRealFilePath(context, uri)
            val contentUri = FileProvider.getUriForFile(context, context.packageName + ".fileProvider", File(filePath))
            install.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            install.setDataAndType(uri, "application/vnd.android.package-archive")
        }
        context.startActivity(install)
    }


    /**
     * 获取apk程序信息[packageName,versionName...]
     *
     * @param context Context
     * @param path    apk path
     */
    private fun getApkInfo(context: Context, path: String?): PackageInfo? {
        if (path.isNullOrEmpty()) return null
        val pm: PackageManager = context.packageManager
        return pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES)
    }

    /**
     * 下载的apk和当前程序版本比较
     *
     * @param apkInfo apk file's packageInfo
     * @param context Context
     * @return 如果当前应用版本小于apk的版本则返回true
     */
    fun compare(context: Context, uri: Uri): Boolean {
        val apkInfo = getApkInfo(context, uri.path) ?: return false
        val localPackage: String = context.packageName
        if (apkInfo.packageName == localPackage) {
            try {
                val packageInfo: PackageInfo = context.packageManager.getPackageInfo(localPackage, 0)
                if (apkInfo.versionCode > packageInfo.versionCode) {
                    return true
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        return false
    }

}