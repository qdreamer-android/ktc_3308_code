package com.pwong.uiframe.upgrade

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.util.Patterns
import androidx.annotation.RequiresPermission
import com.pwong.library.utils.FileUtils
import com.pwong.library.utils.VerityUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author android
 * @date 2020/6/1
 */
class UpgradeHelper(private val context: Context, private val autoUpgrade: Boolean = true) {

    private var mDownloadListener: OnDownloadListener? = null

    private var mDisposable: Disposable? = null

    private var mReceiver: BroadcastReceiver? = null

    private val mManager: AppDownloadManager by lazy {
        AppDownloadManager(context)
    }

    fun installToBrowser(apkUrl: String) {
        if (Patterns.WEB_URL.matcher(apkUrl).matches()) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl)))
        }
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun downloadApk(apkUrl: String, version: String, title: String? = null, desc: String? = null, md5: String? = null) {
        mDisposable?.dispose()
        val downloadId = DownloadCache.getApkDownloadID(context)
        if (downloadId == -1L) {
            val id = mManager.startDownLoadApk(apkUrl, version, title, desc)
            loopProgress(id)
        } else {
            if (md5.isNullOrBlank()) {
                when (val status = mManager.getDownloadStatus(downloadId)) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        if (autoUpgrade) {
                            val uri = mManager.getDownloadUri(downloadId)
                            val filePath = FileUtils.getRealFilePath(context, uri)
                            if (filePath.isNullOrBlank() || !FileUtils.isExistFile(filePath)) {
                                removeDownloadID(downloadId)
                                val id = mManager.startDownLoadApk(apkUrl, version, title, desc)
                                loopProgress(id)
                            } else {
                                mDownloadListener?.onSuccess()
                                toUpgrade(uri, downloadId)
                            }
                        } else {
                            mDownloadListener?.onSuccess()
                        }
                    }
                    DownloadManager.STATUS_FAILED -> {
                        removeDownloadID(downloadId)
                        val id = mManager.startDownLoadApk(apkUrl, version, title, desc)
                        loopProgress(id)
                    }
                    else -> {
                        mDownloadListener?.onDownloadStatus(status)
                    }
                }
            } else {
                val uri = mManager.getDownloadUri(downloadId)
                val filePath = FileUtils.getRealFilePath(context, uri)
                if (!filePath.isNullOrBlank() && FileUtils.isExistFile(filePath) && md5 == VerityUtil.getFileMD5(File(filePath))) {
                    toUpgrade(uri, downloadId)
                } else {
                    val id = mManager.startDownLoadApk(apkUrl, version, title, desc)
                    loopProgress(id)
                }
            }
        }
    }

    private fun toUpgrade(uri: Uri?, downloadId: Long) {
        mDisposable?.dispose()
        if (uri != null) {
            if (ApkInfoUtil.compare(context, uri)) {
                ApkInfoUtil.startInstall(context, uri)
            } else {
                mDownloadListener?.onFailed()
                removeDownloadID(downloadId)
            }
        } else {
            mDownloadListener?.onFailed()
            removeDownloadID(downloadId)
        }
    }

    private fun loopProgress(downloadId: Long) {
        mDisposable?.dispose()
        mDisposable = Observable.interval(100, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val status = mManager.getDownloadStatus(downloadId)
                if (status == DownloadManager.STATUS_FAILED) {
                    mDisposable?.dispose()
                    mDownloadListener?.onFailed()
                    removeDownloadID(downloadId)
                } else {
                    val duration = mManager.getDownloadSize(downloadId)
                    val pos = mManager.getDownloadProgress(downloadId)
                    val progress = if (duration > 0 && pos > 0) {
                        (pos * 100F / duration).toInt()
                    } else {
                        0
                    }
                    mDownloadListener?.onProgress(progress)
                }
            }
    }

    fun registerInstallReceiver() {
        if (mReceiver == null) {
            mReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                        mDisposable?.dispose()
                        val downloadId = DownloadCache.getApkDownloadID(this@UpgradeHelper.context)
                        if (downloadId != -1L) {
                            if (autoUpgrade) {
                                val uri = mManager.getDownloadUri(downloadId)
                                val filePath = FileUtils.getRealFilePath(context, uri)
                                if (filePath.isNullOrBlank() || !FileUtils.isExistFile(filePath)) {
                                    mDownloadListener?.onFailed()
                                    removeDownloadID(downloadId)
                                } else {
                                    mDownloadListener?.onSuccess()
                                    toUpgrade(uri, downloadId)
                                }
                            } else {
                                mDownloadListener?.onSuccess()
                            }
                        } else {
                            mDownloadListener?.onFailed()
                        }
                    }
                }
            }
        }
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        context.registerReceiver(mReceiver!!, filter)
    }

    private fun removeDownloadID(downloadId: Long) {
        DownloadCache.setApkDownloadID(context, -1L)
        if (downloadId != -1L) {
            mManager.getApkDownloader().remove(downloadId)
        }
    }

    fun unregisterInstallReceiver() {
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver!!)
        }
        mDisposable?.dispose()
    }

    fun addOnDownloadListener(listener: OnDownloadListener?) {
        mDownloadListener = listener
    }

}