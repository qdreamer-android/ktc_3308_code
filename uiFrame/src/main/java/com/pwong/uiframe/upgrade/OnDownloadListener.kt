package com.pwong.uiframe.upgrade

/**
 * @author android
 * @date 2020/6/1
 */
interface OnDownloadListener {

    fun onSuccess()

    fun onFailed()

    fun onDownloadStatus(status: Int) {}

    fun onProgress(progress: Int)

}