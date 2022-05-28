package com.pwong.uiframe.upgrade

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import androidx.annotation.RequiresPermission
import java.io.File

/**
 * @author android
 * @date 2018/12/7
 */
class AppDownloadManager(private val context: Context) {

    private val downloadManager: DownloadManager by lazy {
        (context.applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
    }

    fun getApkDownloader() = downloadManager

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun startDownLoadApk(apkUrl: String, version: String, title: String? = null, desc: String? = null): Long {
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(apkUrl))
        // 设置通知栏标题
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setTitle(if (title.isNullOrEmpty()) {
            try {
                val info = context.packageManager.getPackageInfo(context.packageName, 0)
                context.resources.getString(info.applicationInfo.labelRes)
            } catch (e: Exception) {
                "应用升级"
            }
        } else {
            title
        })
        request.setDescription(desc ?: "应用升级中，请稍候...")
        // request.setAllowedOverRoaming(false)
        request.setVisibleInDownloadsUi(true)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val fileName = "${context.packageName}-$version.apk"
//        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) , fileName)
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (file.exists()) {
            file.delete()
        }

        //设置文件存放目录
        // file:///storage/emulated/0/Android/data/your-package/files/Download/update.apk
        // request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
        // file:///storage/emulated/0/Download/update.apk
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        //第三种 自定义文件路径 request.setDestinationUri()

        val id = downloadManager.enqueue(request) ?: -1   // 异步下载
        DownloadCache.setApkDownloadID(context, id)
        return id
    }

    /**
     * 获取保存文件的地址
     *
     * @param downloadId an ID for the download, unique across the system.
     *                   This ID is used to make future calls related to this download.
     * @see FileDownloadManager#getDownloadPath(long)
     */
    fun getDownloadUri(downloadId: Long): Uri? {
        return downloadManager.getUriForDownloadedFile(downloadId)
    }

    /**
     * 获取下载状态
     *
     * @param downloadId an ID for the download, unique across the system.
     *                   This ID is used to make future calls related to this download.
     * @return int
     * @see DownloadManager#STATUS_PENDING
     * @see DownloadManager#STATUS_PAUSED
     * @see DownloadManager#STATUS_RUNNING
     * @see DownloadManager#STATUS_SUCCESSFUL
     * @see DownloadManager#STATUS_FAILED
     */
    fun getDownloadStatus(downloadId: Long): Int {
        return getDownloadInfo(downloadId, DownloadManager.COLUMN_STATUS)
    }

    fun getDownloadSize(downloadId: Long): Int {
        return getDownloadInfo(downloadId, DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
    }

    fun getDownloadProgress(downloadId: Long): Int {
        return getDownloadInfo(downloadId, DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
    }

    private fun getDownloadInfo(downloadId: Long, columnName: String): Int {
        val query: DownloadManager.Query = DownloadManager.Query().setFilterById(downloadId)
        val cursor: Cursor? = downloadManager.query(query)
        cursor?.use { c ->
            if (c.moveToFirst()) {
                return c.getInt(c.getColumnIndexOrThrow(columnName))
            }
        }
        return -1
    }
}